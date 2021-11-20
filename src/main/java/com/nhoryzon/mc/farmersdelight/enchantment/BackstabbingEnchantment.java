package com.nhoryzon.mc.farmersdelight.enchantment;

import com.nhoryzon.mc.farmersdelight.item.KnifeItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public class BackstabbingEnchantment extends Enchantment {

    public BackstabbingEnchantment() {
        super(Rarity.COMMON, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof KnifeItem;
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinPower(int level) {
        return 15 + (level - 1) * 9;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + 50;
    }

    /**
     * Determines whether the attacker is facing a 90-100 degree cone behind the target's looking direction.
     *
     * @param target the target entity attacked
     * @param attackerLocation location of the attacker
     * @return true if the attackerLocation is in a code for 90-100 degree behind the target.
     */
    public static boolean isLookingBehindTarget(LivingEntity target, Vec3d attackerLocation) {
        if (attackerLocation != null) {
            Vec3d vec3d = target.getRotationVec(1.f);
            Vec3d vec3d1 = attackerLocation.subtract(target.getPos()).normalize();
            vec3d1 = new Vec3d(vec3d1.x, .0d, vec3d1.z);
            return vec3d1.dotProduct(vec3d) < -.5d;
        }
        return false;
    }

    public static float getBackstabbingDamagePerLevel(float amount, int level) {
        float multiplier = ((level * .4f) + 1.f);
        return amount * multiplier;
    }

}