package com.exemplo.darkskull.mixin;

import com.exemplo.darkskull.DarkSkull;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Inject(method = "damage", at = @At("HEAD"))
    private void darkskull$burn(ServerWorld world, DamageSource source, float amount,
                                CallbackInfoReturnable<Boolean> cir) {
        ItemEntity self = (ItemEntity) (Object) this;
        if (self.getStack().isOf(Items.WITHER_SKELETON_SKULL)
                && source.isIn(DamageTypeTags.IS_FIRE)) {
            DarkSkull.trigger(world.getServer());
        }
    }
}
