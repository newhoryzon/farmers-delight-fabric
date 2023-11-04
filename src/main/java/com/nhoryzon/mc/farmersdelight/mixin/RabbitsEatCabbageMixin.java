package com.nhoryzon.mc.farmersdelight.mixin;

import com.nhoryzon.mc.farmersdelight.registry.ItemsRegistry;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RabbitEntity.class)
public class RabbitsEatCabbageMixin {

    @Inject(at = @At("HEAD"), method = "isBreedingItem", cancellable = true)
    private void isCabbage(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.isOf(ItemsRegistry.CABBAGE.get()) || stack.isOf(ItemsRegistry.CABBAGE_LEAF.get())) {
            cir.setReturnValue(true);
        }
    }
}
