package com.nhoryzon.mc.farmersdelight.effect;

import com.nhoryzon.mc.farmersdelight.mixin.accessors.PlayerExhaustionAccessorMixin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;

/**
 * This effect prevents hunger loss by constantly decreasing the exhaustion level.
 * If the player can spend saturation to heal damage, the effect halts to let them do so, until they can't any more.
 * This means players can grow hungry by healing damage, but no further than 1.5 points, allowing them to eat more and keep healing.
 */
public class NourishmentEffect extends StatusEffect {

    public NourishmentEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if  (!entity.getEntityWorld().isClient() && entity instanceof PlayerEntity player) {
            HungerManager hungerManager = player.getHungerManager();
            boolean isPlayerHealingWithHunger = player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)
                    && player.canFoodHeal() && hungerManager.getFoodLevel() >= 18;

            if (!isPlayerHealingWithHunger) {
                float exhaustion = ((PlayerExhaustionAccessorMixin) hungerManager).getExhaustion();
                float reduction = Math.min(exhaustion, 4.f);
                if (exhaustion > .0f) {
                    player.addExhaustion(-reduction);
                }
            }
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

}