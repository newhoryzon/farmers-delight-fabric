package com.nhoryzon.mc.farmersdelight.item;

import com.google.common.collect.Lists;
import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.registry.TagsRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.util.Hand;

import java.util.List;

public class HorseFeedItem extends LivingEntityFeedItem {

    protected static final List<StatusEffectInstance> EFFECT_LIST = Lists.newArrayList(
            new StatusEffectInstance(StatusEffects.SPEED, 6000, 1),
            new StatusEffectInstance(StatusEffects.JUMP_BOOST, 6000, 0));

    public HorseFeedItem() {
        super(new ModItemSettings().maxCount(16));
    }

    @Override
    public List<StatusEffectInstance> getStatusEffectApplied() {
        return EFFECT_LIST;
    }

    @Override
    public MutableText getTooltipTextWhenFeeding() {
        return FarmersDelightMod.i18n("tooltip.horse_feed.when_feeding");
    }

    @Override
    public boolean canFeed(ItemStack stack, PlayerEntity feeder, LivingEntity entity, Hand hand) {
        if (entity instanceof HorseEntity horse && entity.getType().isIn(TagsRegistry.HORSE_FEED_USERS)) {
            return horse.isAlive() && horse.isTame();
        }

        return false;
    }

}