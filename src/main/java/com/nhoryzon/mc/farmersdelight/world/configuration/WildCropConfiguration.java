package com.nhoryzon.mc.farmersdelight.world.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.Optional;

public record WildCropConfiguration(
        int tries, int xzSpread, int ySpread,
        RegistryEntry<PlacedFeature> primaryFeature,
        RegistryEntry<PlacedFeature> secondaryFeature,
        RegistryEntry<PlacedFeature> floorFeature) implements FeatureConfig {

    public static final Codec<WildCropConfiguration> CODEC = RecordCodecBuilder.create((config) -> config.group(
            Codecs.POSITIVE_INT.fieldOf("tries").orElse(64).forGetter(WildCropConfiguration::tries),
            Codecs.NONNEGATIVE_INT.fieldOf("xz_spread").orElse(4).forGetter(WildCropConfiguration::xzSpread),
            Codecs.NONNEGATIVE_INT.fieldOf("y_spread").orElse(3).forGetter(WildCropConfiguration::ySpread),
            PlacedFeature.REGISTRY_CODEC.fieldOf("primary_feature").forGetter(WildCropConfiguration::primaryFeature),
            PlacedFeature.REGISTRY_CODEC.fieldOf("secondary_feature").forGetter(WildCropConfiguration::secondaryFeature),
            PlacedFeature.REGISTRY_CODEC.optionalFieldOf("floor_feature").forGetter(floorConfig -> Optional.ofNullable(floorConfig.floorFeature))
    ).apply(config, (tries, xzSpread, yspread, primary, secondary, floor) -> floor.map(placedFeatureHolder -> new WildCropConfiguration(tries, xzSpread, yspread, primary, secondary, placedFeatureHolder)).orElseGet(() -> new WildCropConfiguration(tries, xzSpread, yspread, primary, secondary, null))));

    public int tries() {
        return this.tries;
    }

    public int xzSpread() {
        return this.xzSpread;
    }

    public int ySpread() {
        return this.ySpread;
    }

    public RegistryEntry<PlacedFeature> primaryFeature() {
        return this.primaryFeature;
    }

    public RegistryEntry<PlacedFeature> secondaryFeature() {
        return this.secondaryFeature;
    }

    public RegistryEntry<PlacedFeature> floorFeature() {
        return this.floorFeature;
    }

}
