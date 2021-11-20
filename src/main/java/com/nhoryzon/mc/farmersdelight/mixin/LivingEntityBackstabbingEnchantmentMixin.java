package com.nhoryzon.mc.farmersdelight.mixin;

import com.nhoryzon.mc.farmersdelight.enchantment.BackstabbingEnchantment;
import com.nhoryzon.mc.farmersdelight.registry.EnchantmentsRegistry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({LivingEntity.class, PlayerEntity.class})
public class LivingEntityBackstabbingEnchantmentMixin {

    @ModifyVariable(at = @At("HEAD"), method = "damage", argsOnly = true)
    private float takeDamage(float amount, DamageSource source, float originalAmount) {
        Entity attacker = source.getAttacker();
        if (attacker instanceof PlayerEntity player) {
            ItemStack weapon = player.getMainHandStack();
            int level = EnchantmentHelper.getLevel(EnchantmentsRegistry.BACKSTABBING.get(), weapon);
            if (level > 0 && BackstabbingEnchantment.isLookingBehindTarget((LivingEntity)(Object)this, source.getPosition())) {
                World world = attacker.getEntityWorld();
                if (!world.isClient()) {
                    world.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.BLOCKS, 1.f, 1.f);

                    return BackstabbingEnchantment.getBackstabbingDamagePerLevel(originalAmount, level);
                }
            }
        }

        return amount;
    }

}