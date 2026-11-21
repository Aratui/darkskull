package com.exemplo.darkskull;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class DarkSkullClient implements ClientModInitializer {
    public static volatile boolean dark = false;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(DarkPayload.ID,
            (payload, ctx) -> ctx.client().execute(() -> dark = payload.active()));
        ClientPlayConnectionEvents.DISCONNECT.register((h, c) -> dark = false);
    }
}
