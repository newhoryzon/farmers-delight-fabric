package com.nhoryzon.mc.farmersdelight.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Set;
import java.util.stream.Collectors;

public class HotCocoaItem extends MilkBottleItem {
    public HotCocoaItem(Settings settings) {
        super(settings);
    }

    @Override
    public void affectConsumer(ItemStack stack, World world, LivingEntity user) {
        Set<StatusEffect> compatibleEffectList = user.getStatusEffects().stream()
                .filter(this::canEffectInstanceBeRemovedByMilk)
                .map(StatusEffectInstance::getEffectType).collect(Collectors.toSet());

        if (compatibleEffectList.size() > 0) {
            compatibleEffectList.stream().skip(world.getRandom().nextInt(compatibleEffectList.size())).findFirst().ifPresent(user::removeStatusEffect);
        }
    }

    private boolean canEffectInstanceBeRemovedByMilk(StatusEffectInstance effectInstance) {
        return effectInstance.getEffectType().getType() == StatusEffectType.HARMFUL;
    }
}