package com.nhoryzon.mc.farmersdelight.effect;

import com.nhoryzon.mc.farmersdelight.mixin.PlayerExhaustionAccessorMixin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;

public class NourishedEffect extends StatusEffect {

    public NourishedEffect() {
        super(StatusEffectType.BENEFICIAL, 0);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if  (!entity.getEntityWorld().isClient() && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            HungerManager hungerManager = player.getHungerManager();
            boolean isPlayerHealingWithSaturation = player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)
                    && player.canFoodHeal() && hungerManager.getSaturationLevel() > .0f && hungerManager.getFoodLevel() >= 20;

            if (!isPlayerHealingWithSaturation) {
                float exhaustion = ((PlayerExhaustionAccessorMixin) hungerManager).getExhaustion();
                if (exhaustion > .1f) {
                    player.addExhaustion(-.1f);
                }
            }
        }
    }

}
