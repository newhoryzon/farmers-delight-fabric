package com.nhoryzon.mc.farmersdelight.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;

import java.util.Set;

public class ComfortEffect extends StatusEffect {
    public static final Set<StatusEffect> COMFORT_IMMUNITIES = Set.of(StatusEffects.SLOWNESS, StatusEffects.WEAKNESS, StatusEffects.HUNGER);

    public ComfortEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0);
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);
        COMFORT_IMMUNITIES.forEach(entity::removeStatusEffect);
    }


    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}