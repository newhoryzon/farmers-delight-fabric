package com.nhoryzon.mc.farmersdelight.world.placement;

import com.mojang.serialization.Codec;
import com.nhoryzon.mc.farmersdelight.registry.PlacementModifiersRegistry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.AbstractConditionalPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public class BiomeIsOverworldPlacementModifier extends AbstractConditionalPlacementModifier {

    private static final BiomeIsOverworldPlacementModifier INSTANCE = new BiomeIsOverworldPlacementModifier();

    public static final Codec<BiomeIsOverworldPlacementModifier> CODEC = Codec.unit(INSTANCE);

    @Override
    protected boolean shouldPlace(FeaturePlacementContext context, Random random, BlockPos pos) {
        return context.getWorld().getBiome(pos).isIn(BiomeTags.IS_OVERWORLD);
    }

    @Override
    public PlacementModifierType<?> getType() {
        return PlacementModifiersRegistry.BIOME_IS_OVERWORLD.type();
    }

}
