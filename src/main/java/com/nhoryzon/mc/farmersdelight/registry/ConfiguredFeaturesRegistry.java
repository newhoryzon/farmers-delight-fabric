package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.world.feature.RiceCropFeature;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.RandomPatchFeature;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public enum ConfiguredFeaturesRegistry {

    PATCH_WILD_CABBAGES("patch_wild_cabbages", ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP, 10,
            () -> buildFeatureConfig(BlocksRegistry.WILD_CABBAGES.get(), 64, 2, 2, Blocks.SAND),
            "cabbages", () -> new RandomPatchFeature(RandomPatchFeatureConfig.CODEC)),
    PATCH_WILD_ONIONS("patch_wild_onions", ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE, 8,
            () -> buildFeatureConfig(BlocksRegistry.WILD_ONIONS.get(), 64, 2, 2, Blocks.GRASS_BLOCK),
            "onions", () -> new RandomPatchFeature(RandomPatchFeatureConfig.CODEC)),
    PATCH_WILD_TOMATOES("patch_wild_tomatoes", ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP, 10,
            () -> buildFeatureConfig(BlocksRegistry.WILD_TOMATOES.get(), 64, 2, 2, Blocks.GRASS_BLOCK, Blocks.SAND, Blocks.RED_SAND),
            "tomatoes", () -> new RandomPatchFeature(RandomPatchFeatureConfig.CODEC)),
    PATCH_WILD_CARROTS("patch_wild_carrots", ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE, 8,
            () -> buildFeatureConfig(BlocksRegistry.WILD_CARROTS.get(), 64, 2, 2, Blocks.GRASS_BLOCK),
            "carrots", () -> new RandomPatchFeature(RandomPatchFeatureConfig.CODEC)),
    PATCH_WILD_POTATOES("patch_wild_potatoes", ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE, 8,
            () -> buildFeatureConfig(BlocksRegistry.WILD_POTATOES.get(), 64, 2, 2, Blocks.GRASS_BLOCK),
            "potatoes", () -> new RandomPatchFeature(RandomPatchFeatureConfig.CODEC)),
    PATCH_WILD_BEETROOTS("patch_wild_beetroots", ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP, 10,
            () -> buildFeatureConfig(BlocksRegistry.WILD_BEETROOTS.get(), 64, 2, 2, Blocks.SAND),
            "beetroots", () -> new RandomPatchFeature(RandomPatchFeatureConfig.CODEC)),
    PATCH_WILD_RICE("patch_wild_rice", ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP, 10,
            () -> buildFeatureConfig(BlocksRegistry.WILD_RICE.get(), 64, 4, 4, Blocks.DIRT),
            "rice", () -> new RiceCropFeature(RandomPatchFeatureConfig.CODEC));

    private final String pathName;
    private final ConfiguredDecorator<?> decorator;
    private final int chance;
    private final Supplier<? extends FeatureConfig> featureConfigSupplier;
    private final String featurePathName;
    private final Supplier<? extends Feature<? extends FeatureConfig>> featureSupplier;
    private FeatureConfig config;
    private Feature<? extends FeatureConfig> feature;
    private RegistryKey<ConfiguredFeature<?, ?>> configuredFeatureRegistryKey;
    private ConfiguredFeature<?, ?> configuredFeature;

    ConfiguredFeaturesRegistry(String pathName, ConfiguredDecorator<?> decorator, int chance,
            Supplier<? extends FeatureConfig> featureConfigSupplier,
            String featurePathName, Supplier<? extends Feature<? extends FeatureConfig>> featureSupplier) {
        this.pathName = pathName;
        this.decorator = decorator;
        this.chance = chance;
        this.featureConfigSupplier = featureConfigSupplier;
        this.featurePathName = featurePathName;
        this.featureSupplier = featureSupplier;
    }

    @SuppressWarnings("SameParameterValue")
    private static RandomPatchFeatureConfig buildFeatureConfig(Block block, int tries, int spreadX, int spreadZ, Block... allowedList) {
        return new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(block.getDefaultState()), new SimpleBlockPlacer())
                .tries(tries).spreadX(spreadX).spreadZ(spreadZ)
                .whitelist(Collections.unmodifiableSet(Arrays.stream(allowedList).collect(Collectors.toSet())))
                .cannotProject().build();
    }

    public static void registerAll() {
        for (ConfiguredFeaturesRegistry value : values()) {
            Registry.register(Registry.FEATURE, new Identifier(FarmersDelightMod.MOD_ID, value.featurePathName), value.feature());
            Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, value.key().getValue(), value.get());
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends FeatureConfig> T config() {
        if (config == null) {
            config = featureConfigSupplier.get();
        }

        return (T) config;
    }

    public Feature<? extends FeatureConfig> feature() {
        if (feature == null) {
            feature = featureSupplier.get();
        }

        return feature;
    }

    public RegistryKey<ConfiguredFeature<?, ?>> key() {
        if (configuredFeatureRegistryKey == null) {
            configuredFeatureRegistryKey = RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, new Identifier(FarmersDelightMod.MOD_ID, pathName));
        }

        return configuredFeatureRegistryKey;
    }

    public ConfiguredFeature<?, ?> get() {
        if (configuredFeature == null) {
            configuredFeature = feature().configure(config()).decorate(decorator).applyChance(chance);
        }

        return configuredFeature;
    }

}
