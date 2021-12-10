package com.nhoryzon.mc.farmersdelight.registry;

import com.google.common.collect.ImmutableList;
import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.decorator.*;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import org.intellij.lang.annotations.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.world.gen.feature.VegetationPlacedFeatures.NOT_IN_SURFACE_WATER_MODIFIER;

public class PlacedFeaturesRegistry {
    public static final PlacedFeature PATCH_WILD_BEETROOTS_DECORATED;
    public static final PlacedFeature PATCH_WILD_CABBAGES_DECORATED;
    public static final PlacedFeature PATCH_WILD_ONIONS_DECORATED;
    public static final PlacedFeature PATCH_WILD_TOMATOES_DECORATED;
    public static final PlacedFeature PATCH_WILD_CARROTS_DECORATED;
    public static final PlacedFeature PATCH_WILD_POTATOES_DECORATED;
    public static final PlacedFeature PATCH_WILD_RICE_DECORATED;

    public PlacedFeaturesRegistry() {
    }

    public static List<PlacementModifier> modifiers(int count) {
        return List.of(CountPlacementModifier.of(count), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of());
    }

    private static List<PlacementModifier> modifiersWithChance(int chance, @Nullable PlacementModifier modifier) {
        ImmutableList.Builder<PlacementModifier> builder = ImmutableList.builder();
        if (modifier != null) {
            builder.add(modifier);
        }

        if (chance != 0) {
            builder.add(RarityFilterPlacementModifier.of(chance));
        }

        builder.add(SquarePlacementModifier.of());
        builder.add(PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP);
        builder.add(BiomePlacementModifier.of());
        return builder.build();
    }

    private static ImmutableList.Builder<Object> modifiersBuilder(PlacementModifier countModifier) {
        return ImmutableList.builder().add(countModifier).add(SquarePlacementModifier.of()).add(NOT_IN_SURFACE_WATER_MODIFIER).add(PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP).add(BiomePlacementModifier.of());
    }

    public static ImmutableList<Object> modifiers(PlacementModifier modifier) {
        return modifiersBuilder(modifier).build();
    }

    public static ImmutableList<Object> modifiersWithWouldSurvive(PlacementModifier modifier, Block block) {
        return modifiersBuilder(modifier).add(BlockFilterPlacementModifier.of(BlockPredicate.wouldSurvive(block.getDefaultState(), BlockPos.ORIGIN))).build();
    }


    static {
        PATCH_WILD_CABBAGES_DECORATED = PlacedFeatures.register("patch_wild_cabbages_decorated", NewConfigFeaturesRegistry.PATCH_WILD_CABBAGES.withPlacement(RarityFilterPlacementModifier.of(30), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()));
        PATCH_WILD_ONIONS_DECORATED = PlacedFeatures.register("patch_wild_onions_decorated", NewConfigFeaturesRegistry.PATCH_WILD_ONIONS.withPlacement(RarityFilterPlacementModifier.of(48), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()));
        PATCH_WILD_TOMATOES_DECORATED = PlacedFeatures.register("patch_wild_tomatoes_decorated", NewConfigFeaturesRegistry.PATCH_WILD_TOMATOES.withPlacement(RarityFilterPlacementModifier.of(30), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()));
        PATCH_WILD_CARROTS_DECORATED = PlacedFeatures.register("patch_wild_carrots_decorated", NewConfigFeaturesRegistry.PATCH_WILD_CARROTS.withPlacement(RarityFilterPlacementModifier.of(48), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()));
        PATCH_WILD_POTATOES_DECORATED = PlacedFeatures.register("patch_wild_potatoes_decorated", NewConfigFeaturesRegistry.PATCH_WILD_POTATOES.withPlacement(RarityFilterPlacementModifier.of(48), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()));
        PATCH_WILD_BEETROOTS_DECORATED = PlacedFeatures.register("patch_wild_beetroots_decorated", NewConfigFeaturesRegistry.PATCH_WILD_BEETROOTS.withPlacement(RarityFilterPlacementModifier.of(30), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()));
        PATCH_WILD_RICE_DECORATED = PlacedFeatures.register("patch_wild_rice_decorated", NewConfigFeaturesRegistry.PATCH_WILD_RICE.withPlacement(RarityFilterPlacementModifier.of(5), SquarePlacementModifier.of(), PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP, BiomePlacementModifier.of()));
    }

}
