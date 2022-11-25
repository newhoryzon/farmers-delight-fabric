package com.nhoryzon.mc.farmersdelight.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Set;

/**
 * This effect extends the player's natural regeneration, regardless of how hungry they are.
 * Comfort does not care for amplifiers; it will always heal at the same slow pace.
 * If the player has saturation to spend, or has the Regeneration effect, Comfort does nothing.
 */
public class ComfortEffect extends StatusEffect {

    public ComfortEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.hasStatusEffect(StatusEffects.REGENERATION) ||
                (entity instanceof PlayerEntity player && player.getHungerManager().getSaturationLevel() > 0.0)) {
            return;
        }

        if (entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(1.f);
        }
    }


    @Override
    public boolean canApplyUpdateEffect(int tickDuration, int amplifier) {
        return tickDuration % 80 == 0;
    }

}