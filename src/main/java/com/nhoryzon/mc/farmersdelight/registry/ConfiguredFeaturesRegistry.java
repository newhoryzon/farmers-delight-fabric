package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;

public enum ConfiguredFeaturesRegistry {

    PATCH_WILD_CABBAGES("patch_wild_cabbages"),
    PATCH_WILD_ONIONS("patch_wild_onions"),
    PATCH_WILD_TOMATOES("patch_wild_tomatoes"),
    PATCH_WILD_CARROTS("patch_wild_carrots"),
    PATCH_WILD_POTATOES("patch_wild_potatoes"),
    PATCH_WILD_BEETROOTS("patch_wild_beetroots"),
    PATCH_WILD_RICE("patch_wild_rice"),
    PATCH_SANDY_SHRUB_BONEMEAL("patch_sandy_shrub");

    private final Identifier featureIdentifier;
    private RegistryKey<ConfiguredFeature<?, ?>> configuredFeatureRegistryKey;
    private RegistryKey<PlacedFeature> featureRegistryKey;

    ConfiguredFeaturesRegistry(String featurePathName) {
        this.featureIdentifier = new Identifier(FarmersDelightMod.MOD_ID, featurePathName);
    }

    public static void registerAll() {
        for (ConfiguredFeaturesRegistry value : values()) {
            value.configuredFeatureRegistryKey = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, value.featureIdentifier);
            value.featureRegistryKey = RegistryKey.of(RegistryKeys.PLACED_FEATURE, value.featureIdentifier);
        }
    }

    public RegistryKey<ConfiguredFeature<? extends FeatureConfig, ?>> configKey() {
        return configuredFeatureRegistryKey;
    }

    public RegistryKey<PlacedFeature> key() {
        return featureRegistryKey;
    }

    public Identifier identifier() {
        return featureIdentifier;
    }

}
