package com.nhoryzon.mc.farmersdelight.mixin;

import com.nhoryzon.mc.farmersdelight.item.KnifeItem;
import com.nhoryzon.mc.farmersdelight.item.SkilletItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityKnockBackMixin {

    @Shadow @Nullable public abstract LivingEntity getAttacker();

    @ModifyVariable(at = @At("HEAD"), method = "takeKnockback(DDD)V", ordinal = 0, argsOnly = true)
    private double takeKnockbackStrength(double strength) {
        LivingEntity attacker = getAttacker();
        ItemStack tool = attacker != null ? attacker.getStackInHand(attacker.getActiveHand()) : ItemStack.EMPTY;

        if (tool.getItem() instanceof KnifeItem) {
            return strength - .1d;
        } else if (tool.getItem() instanceof SkilletItem) {
            return strength * 2.f;
        } else {
            return strength;
        }
    }

}