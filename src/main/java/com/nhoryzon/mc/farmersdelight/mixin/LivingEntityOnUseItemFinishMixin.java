package com.nhoryzon.mc.farmersdelight.mixin;

import com.nhoryzon.mc.farmersdelight.registry.EffectsRegistry;
import com.nhoryzon.mc.farmersdelight.tag.Tags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class LivingEntityOnUseItemFinishMixin {

    @Inject(method = "finishUsing", at = @At("HEAD"))
    private void canHaveStatusEffect(World world, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        if (entity.getActiveItem().isIn(Tags.COMFORT_FOODS)) {
            entity.addStatusEffect(new StatusEffectInstance(EffectsRegistry.COMFORT.get(), 6000, 0));
        }
    }

}