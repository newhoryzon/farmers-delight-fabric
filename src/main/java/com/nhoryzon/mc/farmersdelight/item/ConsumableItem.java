package com.nhoryzon.mc.farmersdelight.item;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ConsumableItem extends Item {

    public static final int BRIEF_DURATION = 600;    // 30 seconds
    public static final int SHORT_DURATION = 1200;    // 1 minute
    public static final int MEDIUM_DURATION = 3600;    // 3 minutes
    public static final int LONG_DURATION = 6000;    // 5 minutes

    private static final MutableText NO_EFFECTS = (Text.translatable("effect.none")).formatted(Formatting.GRAY);

    private final boolean hasFoodEffectTooltip;
    private final boolean hasCustomTooltip;

    public ConsumableItem(Settings settings) {
        super(settings);
        this.hasFoodEffectTooltip = false;
        this.hasCustomTooltip = false;
    }

    public ConsumableItem(Settings settings, boolean hasFoodEffectTooltip) {
        super(settings);
        this.hasFoodEffectTooltip = hasFoodEffectTooltip;
        this.hasCustomTooltip = false;
    }

    public ConsumableItem(Settings settings, boolean hasFoodEffectTooltip, boolean hasCustomTooltip) {
        super(settings);
        this.hasFoodEffectTooltip = hasFoodEffectTooltip;
        this.hasCustomTooltip = hasCustomTooltip;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient()) {
            affectConsumer(stack, world, user);
        }

        ItemStack container = new ItemStack(stack.getItem().getRecipeRemainder());

        if (stack.isFood()) {
            super.finishUsing(stack, world, user);
        } else if (user instanceof PlayerEntity player) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
            }
            player.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }

        if (stack.isEmpty()) {
            return container;
        } else {
            if (user instanceof PlayerEntity player && !player.getAbilities().creativeMode && !player.getInventory().insertStack(container)) {
                player.dropItem(container, false);
            }

            return stack;
        }
    }

    public void affectConsumer(ItemStack stack, World world, LivingEntity user) {
        // Do nothing for basic consumable item
    }

    @Override
    @Environment(value= EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (FarmersDelightMod.CONFIG.isFoodEffectTooltip()) {
            if (hasCustomTooltip) {
                tooltip.add(FarmersDelightMod.i18n("tooltip." + this).formatted(Formatting.BLUE));
            }
            if (hasFoodEffectTooltip) {
                addFoodEffectTooltip(stack, tooltip, 1.f);
            }
        }
    }

    @Environment(value= EnvType.CLIENT)
    public static void addFoodEffectTooltip(ItemStack itemIn, List<Text> lores, float durationFactor) {
        FoodComponent foodStats = itemIn.getItem().getFoodComponent();
        if (foodStats == null) {
            return;
        }
        List<Pair<StatusEffectInstance, Float>> effectList = foodStats.getStatusEffects();
        List<Pair<EntityAttribute, EntityAttributeModifier>> attributeList = Lists.newArrayList();
        if (effectList.isEmpty()) {
            lores.add(NO_EFFECTS);
        } else {
            for (Pair<StatusEffectInstance, Float> effectPair : effectList) {
                StatusEffectInstance instance = effectPair.getFirst();
                MutableText iformattabletextcomponent = Text.translatable(instance.getTranslationKey());
                StatusEffect effect = instance.getEffectType();
                Map<EntityAttribute, EntityAttributeModifier> attributeMap = effect.getAttributeModifiers();
                if (!attributeMap.isEmpty()) {
                    for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : attributeMap.entrySet()) {
                        EntityAttributeModifier rawModifier = entry.getValue();
                        EntityAttributeModifier modifier = new EntityAttributeModifier(rawModifier.getName(), effect.adjustModifierAmount(instance.getAmplifier(), rawModifier), rawModifier.getOperation());
                        attributeList.add(new Pair<>(entry.getKey(), modifier));
                    }
                }

                if (instance.getAmplifier() > 0) {
                    iformattabletextcomponent = Text.translatable("potion.withAmplifier", iformattabletextcomponent, Text.translatable("potion.potency." + instance.getAmplifier()));
                }

                if (instance.getDuration() > 20) {
                    iformattabletextcomponent = Text.translatable("potion.withDuration", iformattabletextcomponent, StatusEffectUtil.durationToString(instance, durationFactor));
                }

                lores.add(iformattabletextcomponent.formatted(effect.getCategory().getFormatting()));
            }
        }

        if (!attributeList.isEmpty()) {
            lores.add(Text.empty());
            lores.add((Text.translatable("potion.whenDrank")).formatted(Formatting.DARK_PURPLE));

            for (Pair<EntityAttribute, EntityAttributeModifier> pair : attributeList) {
                EntityAttributeModifier modifier = pair.getSecond();
                double amount = modifier.getValue();
                double formattedAmount;
                if (modifier.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_BASE && modifier.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
                    formattedAmount = modifier.getValue();
                } else {
                    formattedAmount = modifier.getValue() * 100.d;
                }

                if (amount > 0.0D) {
                    lores.add((Text.translatable("attribute.modifier.plus." + modifier.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(formattedAmount), Text.translatable(pair.getFirst().getTranslationKey()))).formatted(Formatting.BLUE));
                } else if (amount < 0.0D) {
                    formattedAmount = formattedAmount * -1.d;
                    lores.add((Text.translatable("attribute.modifier.take." + modifier.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(formattedAmount), Text.translatable(pair.getFirst().getTranslationKey()))).formatted(Formatting.RED));
                }
            }
        }

    }

}