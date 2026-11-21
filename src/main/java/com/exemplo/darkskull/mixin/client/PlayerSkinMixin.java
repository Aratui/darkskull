package com.exemplo.darkskull.mixin.client;

import com.exemplo.darkskull.DarkSkullClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class PlayerSkinMixin {
    private static final Identifier DARK_PATH = Identifier.of("darkskull", "textures/skin/dark.png");
    private static final Identifier DARK_ID = Identifier.of("darkskull", "skin/dark");
    private static boolean failed = false;

    @Inject(method = {"getSkinTextures", "getSkin"}, at = @At("RETURN"), cancellable = true)
    private void darkskull$skin(CallbackInfoReturnable<Object> cir) {
        if (!DarkSkullClient.dark || failed) return;
        Object o = cir.getReturnValue();
        if (o == null) return;
        try {
            Class<?> c = o.getClass();
            RecordComponent[] rc = c.getRecordComponents();
            Object[] vals = new Object[rc.length];
            Class<?>[] types = new Class<?>[rc.length];
            for (int i = 0; i < rc.length; i++) {
                types[i] = rc[i].getType();
                var acc = rc[i].getAccessor();
                acc.setAccessible(true);
                vals[i] = acc.invoke(o);
            }
            Object dark = makeTexture(types[0], vals[0]);
            if (dark == null) { failed = true; return; }
            vals[0] = dark;
            Constructor<?> k = c.getDeclaredConstructor(types);
            k.setAccessible(true);
            cir.setReturnValue(k.newInstance(vals));
        } catch (Throwable t) {
            failed = true;
            t.printStackTrace();
        }
    }

    private static Object makeTexture(Class<?> type, Object original) throws Exception {
        if (type == Identifier.class) return DARK_PATH;
        if (original == null) return null;
        for (Constructor<?> k : original.getClass().getDeclaredConstructors()) {
            Class<?>[] pt = k.getParameterTypes();
            boolean all = pt.length > 0;
            for (Class<?> p : pt) if (p != Identifier.class) all = false;
            if (!all) continue;
            k.setAccessible(true);
            if (pt.length == 1) return k.newInstance(DARK_ID);
            if (pt.length == 2) return k.newInstance(DARK_ID, DARK_PATH);
        }
        return null;
    }
}
