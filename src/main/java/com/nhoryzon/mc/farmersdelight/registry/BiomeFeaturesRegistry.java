package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.world.configuration.WildCropConfiguration;
import com.nhoryzon.mc.farmersdelight.world.feature.WildCropFeature;
import com.nhoryzon.mc.farmersdelight.world.feature.WildRiceCropFeature;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;

import java.util.function.Supplier;

public enum BiomeFeaturesRegistry {

    WILD_RICE("wild_rice", () -> new WildRiceCropFeature(RandomPatchFeatureConfig.CODEC)),
    WILD_CROP("wild_crop", () -> new WildCropFeature(WildCropConfiguration.CODEC));

    private final String pathName;
    private final Supplier<Feature<? extends FeatureConfig>> featureSupplier;
    private Feature<? extends FeatureConfig> feature;

    BiomeFeaturesRegistry(String pathName, Supplier<Feature<? extends FeatureConfig>> featureSupplier) {
        this.pathName = pathName;
        this.featureSupplier = featureSupplier;
    }


    public static void registerAll() {
        for (BiomeFeaturesRegistry value : values()) {
            Registry.register(Registries.FEATURE, new Identifier(FarmersDelightMod.MOD_ID, value.pathName), value.featureSupplier.get());
        }
    }

    public Feature<? extends FeatureConfig> get() {
        if (feature == null) {
            feature = featureSupplier.get();
        }
        return feature;
    }

}
