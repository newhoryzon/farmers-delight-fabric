package com.nhoryzon.mc.farmersdelight.item.enumeration;

import com.nhoryzon.mc.farmersdelight.item.ConsumableItem;
import com.nhoryzon.mc.farmersdelight.registry.EffectsRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

import java.util.function.Supplier;

public enum Foods {

    // Raw Crops
    CABBAGE(2, .4f),
    TOMATO(1, .3f),
    ONION(2, .4f),

    // Drinks (mostly for effects)
    APPLE_CIDER(0, 0, () -> new StatusEffectInstance(StatusEffects.ABSORPTION, ConsumableItem.SHORT_DURATION, 0), 1.f, false, false, true),

    // Basic Foods
    FRIED_EGG(4, .4f),
    TOMATO_SAUCE(4, .4f),
    WHEAT_DOUGH(2, .3f, () -> new StatusEffectInstance(StatusEffects.HUNGER, ConsumableItem.BRIEF_DURATION, 0), .3f),
    RAW_PASTA(2, .3f, () -> new StatusEffectInstance(StatusEffects.HUNGER, ConsumableItem.BRIEF_DURATION, 0), .3f),
    PIE_CRUST(2, .2f),
    PUMPKIN_SLICE(3, .3f),
    CABBAGE_LEAF(1, .4f, false, true),
    MINCED_BEEF(2, .3f, true, true),
    BEEF_PATTY(4, .8f, true, true),
    CHICKEN_CUTS(1, .3f, () -> new StatusEffectInstance(StatusEffects.HUNGER, ConsumableItem.BRIEF_DURATION, 0), .3f, true, true, false),
    COOKED_CHICKEN_CUTS(3, .6f, true, true),
    BACON(2, .3f, true, true),
    COOKED_BACON(4, .8f, true, true),
    COD_SLICE(1, .1f, false, true),
    COOKED_COD_SLICE(3, .5f, false, true),
    SALMON_SLICE(1, .1f, false, true),
    COOKED_SALMON_SLICE(3, .8f, false, true),
    MUTTON_CHOP(1, .3f, true, true),
    COOKED_MUTTON_CHOP(3, .8f, true, true),
    HAM(5, .3f),
    SMOKED_HAM(10, .8f),

    // Sweets
    POPSICLE(3, .2f, null, .0f, false, true, true),
    COOKIES(2, .1f, false, true),
    CAKE_SLICE(2, .1f, () -> new StatusEffectInstance(StatusEffects.SPEED, 400, 0), 1.f, false, true, false),
    PIE_SLICE(3, .3f, () -> new StatusEffectInstance(StatusEffects.SPEED, ConsumableItem.BRIEF_DURATION, 0), 1.f, false, true, false),
    FRUIT_SALAD(6, .6f, () -> new StatusEffectInstance(StatusEffects.REGENERATION, 100, 0), 1.f),
    GLOW_BERRY_CUSTARD(7, .6f, () -> new StatusEffectInstance(StatusEffects.GLOWING, 100, 0), 1.f, false, false, true),

    // Handheld Foods
    MIXED_SALAD(6, .6f, () -> new StatusEffectInstance(StatusEffects.REGENERATION, 100, 0), 1.f),
    NETHER_SALAD(5, .4f, () -> new StatusEffectInstance(StatusEffects.NAUSEA, 240, 0), .3f),
    BARBECUE_STICK(8, .9f),
    EGG_SANDWICH(8, .8f),
    CHICKEN_SANDWICH(10, .8f),
    HAMBURGER(11, .8f),
    BACON_SANDWICH(10, .8f),
    MUTTON_WRAP(11, .8f),
    DUMPLINGS(8, .8f),
    STUFFED_POTATO(10, .7f),
    CABBAGE_ROLLS(6, .5f),
    SALMON_ROLL(7, .6f),
    COD_ROLL(7, .6f),
    KELP_ROLL(12, .6f),
    KELP_ROLL_SLICE(6, .5f, false, true),

    // Bowl Foods
    COOKED_RICE(6, .4f, () -> new StatusEffectInstance(EffectsRegistry.COMFORT.get(), ConsumableItem.BRIEF_DURATION, 0), 1.f),
    BONE_BROTH(8, .7f, () -> new StatusEffectInstance(EffectsRegistry.COMFORT.get(), ConsumableItem.SHORT_DURATION, 0), 1.f),
    BEEF_STEW(12, .8f, () -> new StatusEffectInstance(EffectsRegistry.COMFORT.get(), ConsumableItem.MEDIUM_DURATION, 0), 1.f),
    VEGETABLE_SOUP(12, .8f, () -> new StatusEffectInstance(EffectsRegistry.COMFORT.get(), ConsumableItem.MEDIUM_DURATION, 0), 1.f),
    FISH_STEW(12, .8f, () -> new StatusEffectInstance(EffectsRegistry.COMFORT.get(), ConsumableItem.MEDIUM_DURATION, 0), 1.f),
    CHICKEN_SOUP(14, .75f, () -> new StatusEffectInstance(EffectsRegistry.COMFORT.get(), ConsumableItem.LONG_DURATION, 0), 1.f),
    FRIED_RICE(14, .75f, () -> new StatusEffectInstance(EffectsRegistry.COMFORT.get(), ConsumableItem.LONG_DURATION, 0), 1.f),
    PUMPKIN_SOUP(14, .75f, () -> new StatusEffectInstance(EffectsRegistry.COMFORT.get(), ConsumableItem.LONG_DURATION, 0), 1.f),
    BAKED_COD_STEW(14, .75f, () -> new StatusEffectInstance(EffectsRegistry.COMFORT.get(), ConsumableItem.LONG_DURATION, 0), 1.f),
    NOODLE_SOUP(14, .75f, () -> new StatusEffectInstance(EffectsRegistry.COMFORT.get(), ConsumableItem.LONG_DURATION, 0), 1.f),

    // Plated Foods
    BACON_AND_EGGS(10, .6f, () -> new StatusEffectInstance(EffectsRegistry.NOURISHMENT.get(), ConsumableItem.SHORT_DURATION, 0), 1.f),
    RATATOUILLE(10, .6f, () -> new StatusEffectInstance(EffectsRegistry.NOURISHMENT.get(), ConsumableItem.SHORT_DURATION, 0), 1.f),
    STEAK_AND_POTATOES(12, .8f, () -> new StatusEffectInstance(EffectsRegistry.NOURISHMENT.get(), ConsumableItem.MEDIUM_DURATION, 0), 1.f),
    PASTA_WITH_MEATBALLS(12, .8f, () -> new StatusEffectInstance(EffectsRegistry.NOURISHMENT.get(), ConsumableItem.MEDIUM_DURATION, 0), 1.f),
    PASTA_WITH_MUTTON_CHOP(12, .8f, () -> new StatusEffectInstance(EffectsRegistry.NOURISHMENT.get(), ConsumableItem.MEDIUM_DURATION, 0), 1.f),
    MUSHROOM_RICE(12, .8f, () -> new StatusEffectInstance(EffectsRegistry.NOURISHMENT.get(), ConsumableItem.MEDIUM_DURATION, 0), 1.f),
    ROASTED_MUTTON_CHOPS(14, .75f, () -> new StatusEffectInstance(EffectsRegistry.NOURISHMENT.get(), ConsumableItem.LONG_DURATION, 0), 1.f),
    VEGETABLE_NOODLES(14, .75f, () -> new StatusEffectInstance(EffectsRegistry.NOURISHMENT.get(), ConsumableItem.LONG_DURATION, 0), 1.f),
    SQUID_INK_PASTA(14, .75f, () -> new StatusEffectInstance(EffectsRegistry.NOURISHMENT.get(), ConsumableItem.LONG_DURATION, 0), 1.f),
    GRILLED_SALMON(14, .75f, () -> new StatusEffectInstance(EffectsRegistry.NOURISHMENT.get(), ConsumableItem.MEDIUM_DURATION, 0), 1.f),

    // Feast Portions
    ROAST_CHICKEN(14, .75f, () -> new StatusEffectInstance(EffectsRegistry.NOURISHMENT.get(), ConsumableItem.LONG_DURATION, 0), 1.f),
    STUFFED_PUMPKIN(14, .75f, () -> new StatusEffectInstance(EffectsRegistry.NOURISHMENT.get(), ConsumableItem.LONG_DURATION, 0), 1.f),
    HONEY_GLAZED_HAM(14, .75f, () -> new StatusEffectInstance(EffectsRegistry.NOURISHMENT.get(), ConsumableItem.LONG_DURATION, 0), 1.f),
    SHEPHERDS_PIE(14, .75f, () -> new StatusEffectInstance(EffectsRegistry.NOURISHMENT.get(), ConsumableItem.LONG_DURATION, 0), 1.f),

    DOG_FOOD(4, .2f, true, false);

    private final Supplier<FoodComponent> food;

    Foods(int hunger, float saturation) {
        this(hunger, saturation, null, .0f, false, false, false);
    }

    Foods(int hunger, float saturation, boolean isMeat, boolean isFastToEat) {
        this(hunger, saturation, null, .0f, isMeat, isFastToEat, false);
    }

    Foods(int hunger, float saturation, Supplier<StatusEffectInstance> effect, float effectChance) {
        this(hunger, saturation, effect, effectChance, false, false, false);
    }

    Foods(int hunger, float saturation, Supplier<StatusEffectInstance> effect, float effectChance, boolean isMeat, boolean isFastToEat, boolean alwaysEdible) {
        food = () -> {
            FoodComponent.Builder builder = new FoodComponent.Builder();
            builder.hunger(hunger).saturationModifier(saturation);
            if (effect != null) {
                builder.statusEffect(effect.get(), effectChance);
            }
            if (isMeat) {
                builder.meat();
            }
            if (isFastToEat) {
                builder.snack();
            }
            if (alwaysEdible) {
                builder.alwaysEdible();
            }

            return builder.build();
        };
    }

    public FoodComponent get() {
        return food.get();
    }
}