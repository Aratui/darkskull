package com.exemplo.darkskull.mixin.client;

import com.exemplo.darkskull.DarkSkullClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class PlayerSkinMixin {
    private static final Identifier DARK = Identifier.of("darkskull", "textures/skin/dark.png");

    @Inject(method = "getSkinTextures", at = @At("RETURN"), cancellable = true)
    private void darkskull$skin(CallbackInfoReturnable<SkinTextures> cir) {
        if (!DarkSkullClient.dark) return;
        SkinTextures o = cir.getReturnValue();
        cir.setReturnValue(new SkinTextures(
            DARK, null, o.capeTexture(), o.elytraTexture(), o.model(), o.secure()));
    }
}
