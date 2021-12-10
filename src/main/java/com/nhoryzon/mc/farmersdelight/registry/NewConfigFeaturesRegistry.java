package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.world.feature.RiceCropFeature;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.BiasedToBottomIntProvider;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.decorator.BlockFilterPlacementModifier;
import net.minecraft.world.gen.decorator.PlacementModifier;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class NewConfigFeaturesRegistry {
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> PATCH_WILD_BEETROOTS;
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> PATCH_WILD_CABBAGES;
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> PATCH_WILD_ONIONS;
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> PATCH_WILD_TOMATOES;
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> PATCH_WILD_CARROTS;
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> PATCH_WILD_POTATOES;
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> PATCH_WILD_RICE;






    public NewConfigFeaturesRegistry() {
    }

    private static RandomPatchFeatureConfig createRandomPatchFeatureConfig(BlockStateProvider block, int tries) {
        return ConfiguredFeatures.createRandomPatchFeatureConfig(tries, Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(block)).withInAirFilter());
    }

    static {
        PATCH_WILD_CABBAGES = ConfiguredFeatures.register("patch_wild_cabbages", Feature.RANDOM_PATCH.configure(new RandomPatchFeatureConfig(64, 2, 2, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(BlocksRegistry.WILD_CABBAGES.get()))).withInAirFilter())));
        PATCH_WILD_ONIONS = ConfiguredFeatures.register("patch_wild_onions", Feature.RANDOM_PATCH.configure(new RandomPatchFeatureConfig(64, 2, 2, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(BlocksRegistry.WILD_ONIONS.get()))).withInAirFilter())));
        PATCH_WILD_TOMATOES = ConfiguredFeatures.register("patch_wild_tomatoes", Feature.RANDOM_PATCH.configure(new RandomPatchFeatureConfig(64, 2, 2, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(BlocksRegistry.WILD_CARROTS.get()))).withInAirFilter())));
        PATCH_WILD_CARROTS = ConfiguredFeatures.register("patch_wild_carrots", Feature.RANDOM_PATCH.configure(new RandomPatchFeatureConfig(64, 2, 2, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(BlocksRegistry.WILD_TOMATOES.get()))).withInAirFilter())));
        PATCH_WILD_POTATOES = ConfiguredFeatures.register("patch_wild_potatoes", Feature.RANDOM_PATCH.configure(new RandomPatchFeatureConfig(64, 2, 2, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(BlocksRegistry.WILD_POTATOES.get()))).withInAirFilter())));
        PATCH_WILD_BEETROOTS = ConfiguredFeatures.register("patch_wild_beetroots", Feature.RANDOM_PATCH.configure(new RandomPatchFeatureConfig(64, 2, 2, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(BlocksRegistry.WILD_BEETROOTS.get()))).withInAirFilter())));
        PATCH_WILD_RICE = ConfiguredFeatures.register("patch_wild_rice", RiceCropFeature.RANDOM_PATCH.configure(new RandomPatchFeatureConfig(64, 4, 4, () -> RiceCropFeature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(BlocksRegistry.WILD_RICE.get()))).withBlockPredicateFilter(BlockPredicate.not(BlockPredicate.IS_AIR)))));
    }
}
