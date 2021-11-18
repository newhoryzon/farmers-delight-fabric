package com.nhoryzon.mc.farmersdelight.item;

import com.google.common.collect.Lists;
import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.tag.Tags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;

import java.util.List;

public class DogFoodItem extends LivingEntityFeedItem {

    protected static final List<StatusEffectInstance> EFFECT_LIST = Lists.newArrayList(
            new StatusEffectInstance(StatusEffects.SPEED, 6000, 0),
            new StatusEffectInstance(StatusEffects.STRENGTH, 6000, 0),
            new StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 0)
    );

    public DogFoodItem(Settings settings) {
        super(settings);
    }

    @Override
    public List<StatusEffectInstance> getStatusEffectApplied() {
        return EFFECT_LIST;
    }

    @Override
    public TranslatableText getTooltipTextWhenFeeding() {
        return FarmersDelightMod.i18n("tooltip.dog_food.when_feeding");
    }

    @Override
    public boolean canFeed(ItemStack stack, PlayerEntity feeder, LivingEntity entity, Hand hand) {
        if (entity instanceof WolfEntity wolf && entity.getType().isIn(Tags.DOG_FOOD_USERS)) {
            return wolf.isAlive() && wolf.isTamed();
        }

        return false;
    }

    @Override
    public boolean isConsumable() {
        return true;
    }

}