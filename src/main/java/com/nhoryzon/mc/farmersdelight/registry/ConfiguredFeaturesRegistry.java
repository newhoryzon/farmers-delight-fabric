package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public enum ConfiguredFeaturesRegistry {

    PATCH_WILD_CABBAGES("patch_wild_cabbages",
            () -> new ConfiguredFeature<>(Feature.RANDOM_PATCH, createRandomPatchFeatureConfig(BlocksRegistry.WILD_CABBAGES.get(), 64, 2, 2)),
            "cabbages", RarityFilterPlacementModifier.of(32), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()),
    PATCH_WILD_ONIONS("patch_wild_onions",
            () -> new ConfiguredFeature<>(Feature.RANDOM_PATCH, createRandomPatchFeatureConfig(BlocksRegistry.WILD_ONIONS.get(), 64, 2, 2)),
            "onions", RarityFilterPlacementModifier.of(48), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()),
    PATCH_WILD_TOMATOES("patch_wild_tomatoes",
            () -> new ConfiguredFeature<>(Feature.RANDOM_PATCH, createRandomPatchFeatureConfig(BlocksRegistry.WILD_TOMATOES.get(), 64, 2, 2)),
            "tomatoes", RarityFilterPlacementModifier.of(32), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()),
    PATCH_WILD_CARROTS("patch_wild_carrots",
            () -> new ConfiguredFeature<>(Feature.RANDOM_PATCH, createRandomPatchFeatureConfig(BlocksRegistry.WILD_CARROTS.get(), 64, 2, 2)),
            "carrots", RarityFilterPlacementModifier.of(48), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()),
    PATCH_WILD_POTATOES("patch_wild_potatoes",
            () -> new ConfiguredFeature<>(Feature.RANDOM_PATCH, createRandomPatchFeatureConfig(BlocksRegistry.WILD_POTATOES.get(), 64, 2, 2)),
            "potatoes", RarityFilterPlacementModifier.of(48), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()),
    PATCH_WILD_BEETROOTS("patch_wild_beetroots",
            () -> new ConfiguredFeature<>(Feature.RANDOM_PATCH, createRandomPatchFeatureConfig(BlocksRegistry.WILD_BEETROOTS.get(), 64, 2, 2)),
            "beetroots", RarityFilterPlacementModifier.of(32), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()),
    PATCH_WILD_RICE("patch_wild_rice",
            () -> new ConfiguredFeature<>(Feature.RANDOM_PATCH, createRandomPatchFeatureConfig(BlocksRegistry.WILD_RICE.get(), 64, 4, 4, BlockPredicate.not(BlockPredicate.IS_AIR))),
            "rice", RarityFilterPlacementModifier.of(32), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of());

    private final String configPathName;
    private final String featurePathName;
    private final Supplier<? extends ConfiguredFeature<?, ?>> featureConfigSupplier;
    private final PlacementModifier[] placementModifierList;

    private ConfiguredFeature<?, ?> configuredFeature;
    private RegistryKey<ConfiguredFeature<?, ?>> configuredFeatureRegistryKey;
    private PlacedFeature feature;
    private RegistryKey<PlacedFeature> featureRegistryKey;

    ConfiguredFeaturesRegistry(String configPathName,
            Supplier<? extends ConfiguredFeature<?, ?>> featureConfigSupplier,
            String featurePathName, PlacementModifier... placementModifierList) {
        this.configPathName = configPathName;
        this.featureConfigSupplier = featureConfigSupplier;
        this.featurePathName = featurePathName;
        this.placementModifierList = placementModifierList;
    }

    @SuppressWarnings("SameParameterValue")
    private static RandomPatchFeatureConfig createRandomPatchFeatureConfig(Block block, int tries, int spreadX, int spreadZ) {
        return new RandomPatchFeatureConfig(tries, spreadX, spreadZ, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK,
                new SimpleBlockFeatureConfig(BlockStateProvider.of(block))));
    }

    @SuppressWarnings("SameParameterValue")
    private static RandomPatchFeatureConfig createRandomPatchFeatureConfig(Block block, int tries, int spreadX, int spreadZ, BlockPredicate blockPredicate) {
        return new RandomPatchFeatureConfig(tries, spreadX, spreadZ, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK,
                new SimpleBlockFeatureConfig(BlockStateProvider.of(block)), blockPredicate));
    }

    public static void registerAll() {
        for (ConfiguredFeaturesRegistry value : values()) {
            Identifier configId = new Identifier(FarmersDelightMod.MOD_ID, value.configPathName);
            value.configuredFeature = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, configId, value.featureConfigSupplier.get());
            value.configuredFeatureRegistryKey = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, configId);

            Identifier featureId = new Identifier(FarmersDelightMod.MOD_ID, value.featurePathName);
            value.feature = Registry.register(BuiltinRegistries.PLACED_FEATURE, featureId,
                    new PlacedFeature(RegistryEntry.of(value.configuredFeature), List.of(value.placementModifierList)));
            value.featureRegistryKey = RegistryKey.of(Registry.PLACED_FEATURE_KEY, featureId);
        }
    }

    public ConfiguredFeature<? extends FeatureConfig, ?> config() {
        return configuredFeature;
    }

    public RegistryKey<ConfiguredFeature<? extends FeatureConfig, ?>> configKey() {
        return configuredFeatureRegistryKey;
    }

    public PlacedFeature get() {
        return feature;
    }

    public RegistryKey<PlacedFeature> key() {
        return featureRegistryKey;
    }

}
