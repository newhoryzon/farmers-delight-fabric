package com.nhoryzon.mc.farmersdelight.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class LivingEntityFeedItem extends ConsumableItem {

    protected LivingEntityFeedItem(Settings settings) {
        super(settings);
    }

    public abstract List<StatusEffectInstance> getStatusEffectApplied();

    public abstract TranslatableText getTooltipTextWhenFeeding();

    public abstract boolean canFeed(ItemStack stack, PlayerEntity feeder, LivingEntity entity, Hand hand);

    public boolean isConsumable() {
        return false;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (isConsumable()) {
            return super.finishUsing(stack, world, user);
        } else if (isFood()) {
            return user.eatFood(world, stack);
        }

        return stack;
    }

    @Override
    @Environment(value= EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(getTooltipTextWhenFeeding().formatted(Formatting.GRAY));

        for (StatusEffectInstance effectInstance : getStatusEffectApplied()) {
            MutableText effectDescription = new LiteralText(" ");
            MutableText effectName = new TranslatableText(effectInstance.getTranslationKey());
            effectDescription.append(effectName);
            StatusEffect effect = effectInstance.getEffectType();

            if (effectInstance.getAmplifier() > 0) {
                effectDescription.append(" ").append(new TranslatableText("potion.potency." + effectInstance.getAmplifier()));
            }

            if (effectInstance.getDuration() > 20) {
                effectDescription.append(" (").append(StatusEffectUtil.durationToString(effectInstance, 1.f)).append(")");
            }

            tooltip.add(effectDescription.formatted(effect.getCategory().getFormatting()));
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (canFeed(stack, user, entity, hand)) {
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

}