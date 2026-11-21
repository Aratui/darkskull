package com.exemplo.darkskull;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.nio.file.*;

import static net.minecraft.server.command.CommandManager.literal;

public class DarkSkull implements ModInitializer {
    public static boolean active = false;
    private static final String TEAM = "darkskull";
    private static Path file;

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(DarkPayload.ID, DarkPayload.CODEC);
        file = FabricLoader.getInstance().getConfigDir().resolve("darkskull.flag");
        active = Files.exists(file);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (active) {
                addToTeam(server, handler.getPlayer());
                ServerPlayNetworking.send(handler.getPlayer(), new DarkPayload(true));
            }
        });

        CommandRegistrationCallback.EVENT.register((d, access, env) ->
            d.register(literal("darkskull").requires(s -> s.hasPermissionLevel(2))
                .then(literal("reset").executes(ctx -> {
                    setActive(ctx.getSource().getServer(), false);
                    ctx.getSource().sendFeedback(() -> Text.literal("Efeito removido."), true);
                    return 1;
                }))));
    }

    public static void trigger(MinecraftServer server) {
        if (active) return;
        setActive(server, true);
        server.getPlayerManager().broadcast(
            Text.literal("A cabeça queimou... todos foram consumidos pela escuridão."), false);
    }

    private static void setActive(MinecraftServer server, boolean value) {
        active = value;
        try {
            if (value) Files.writeString(file, "1");
            else Files.deleteIfExists(file);
        } catch (Exception e) { e.printStackTrace(); }

        Scoreboard sb = server.getScoreboard();
        if (value) {
            for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList())
                addToTeam(server, p);
        } else {
            Team t = sb.getTeam(TEAM);
            if (t != null) sb.removeTeam(t);
        }
        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList())
            ServerPlayNetworking.send(p, new DarkPayload(value));
    }

    private static void addToTeam(MinecraftServer server, ServerPlayerEntity p) {
        Scoreboard sb = server.getScoreboard();
        Team t = sb.getTeam(TEAM);
        if (t == null) {
            t = sb.addTeam(TEAM);
            t.setNameTagVisibilityRule(AbstractTeam.VisibilityRule.NEVER);
        }
        sb.addScoreHolderToTeam(p.getNameForScoreboard(), t);
    }
}
