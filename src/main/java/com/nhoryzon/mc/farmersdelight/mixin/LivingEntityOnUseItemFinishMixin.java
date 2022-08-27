package com.nhoryzon.mc.farmersdelight.mixin;

import com.nhoryzon.mc.farmersdelight.Configuration;
import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.registry.EffectsRegistry;
import com.nhoryzon.mc.farmersdelight.registry.TagsRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class LivingEntityOnUseItemFinishMixin {

    @Shadow public abstract Item getItem();

    @Inject(method = "finishUsing", at = @At("HEAD"))
    private void canHaveStatusEffect(World world, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack itemUsing = entity.getActiveItem();

        if (FarmersDelightMod.CONFIG.isRabbitStewJumpBoost() && itemUsing.isOf(Items.RABBIT_STEW)) {
            entity.addStatusEffect(
                    new StatusEffectInstance(StatusEffects.JUMP_BOOST, Configuration.DURATION_RABBIT_STEW_JUMP, 1));
        }

        if (FarmersDelightMod.CONFIG.isVanillaSoupExtraEffects() && entity.getActiveItem().isIn(TagsRegistry.COMFORT_FOODS)) {
            entity.addStatusEffect(
                    new StatusEffectInstance(EffectsRegistry.COMFORT.get(), Configuration.DURATION_VANILLA_SOUP, 0));
        }
    }

}