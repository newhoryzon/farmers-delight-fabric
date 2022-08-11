package com.nhoryzon.mc.farmersdelight.mixin;

import com.nhoryzon.mc.farmersdelight.item.SkilletItem;
import com.nhoryzon.mc.farmersdelight.registry.SoundsRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityAttackWithSkilletMixin {

    @Inject(at = @At("HEAD"), method = "attack")
    private void attackWithSkillet(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        float attackPower = player.getAttackCooldownProgress(.0f);
        ItemStack tool = player.getStackInHand(Hand.MAIN_HAND);
        if (tool.getItem() instanceof SkilletItem) {
            if (attackPower > .8f) {
                float pitch = .9f + (player.getRandom().nextFloat() * .2f);
                player.getEntityWorld().playSound(player, player.getX(), player.getY(), player.getZ(),
                        SoundsRegistry.ITEM_SKILLET_ATTACK_STRONG.get(), SoundCategory.PLAYERS, 1.f, pitch);
            } else {
                player.getEntityWorld().playSound(player, player.getX(), player.getY(), player.getZ(),
                        SoundsRegistry.ITEM_SKILLET_ATTACK_WEAK.get(), SoundCategory.PLAYERS, .8f, .9f);
            }
        }
    }

}