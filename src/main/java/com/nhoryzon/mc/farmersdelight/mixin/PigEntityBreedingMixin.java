package com.nhoryzon.mc.farmersdelight.mixin;

import com.nhoryzon.mc.farmersdelight.registry.ItemsRegistry;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PigEntity.class)
public abstract class PigEntityBreedingMixin {

    @Inject(method = "isBreedingItem", at = @At("TAIL"), cancellable = true)
    private void isBreedingItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!Boolean.TRUE.equals(cir.getReturnValue())) {
            cir.setReturnValue(Ingredient.ofItems(
                    ItemsRegistry.CABBAGE.get(),
                    ItemsRegistry.TOMATO.get()).test(stack));
        }
    }

}
