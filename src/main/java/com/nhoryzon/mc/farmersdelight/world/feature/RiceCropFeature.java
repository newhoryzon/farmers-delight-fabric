package com.nhoryzon.mc.farmersdelight.world.feature;

import com.mojang.serialization.Codec;
import com.nhoryzon.mc.farmersdelight.block.WildRiceCropBlock;
import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;

import java.util.Random;

public class RiceCropFeature extends Feature<RandomPatchFeatureConfig> {
    public RiceCropFeature(Codec<RandomPatchFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos,
            RandomPatchFeatureConfig config) {
        BlockPos blockPos = world.getTopPosition(Heightmap.Type.OCEAN_FLOOR_WG, pos);

        int i = 0;
        BlockPos.Mutable blockPosMutable = new BlockPos.Mutable();

        for (int j = 0; j < config.tries; ++j) {
            blockPosMutable.set(blockPos).move(
                    random.nextInt(config.spreadX + 1) - random.nextInt(config.spreadX + 1),
                    random.nextInt(config.spreadY + 1) - random.nextInt(config.spreadY + 1),
                    random.nextInt(config.spreadZ + 1) - random.nextInt(config.spreadZ + 1));

            if (world.getBlockState(blockPosMutable).getBlock() == Blocks.WATER && world.getBlockState(blockPosMutable.up()).getBlock() == Blocks.AIR) {
                BlockState bottomRiceState = BlocksRegistry.WILD_RICE.get().getDefaultState().with(WildRiceCropBlock.HALF, DoubleBlockHalf.LOWER);
                if (bottomRiceState.canPlaceAt(world, blockPosMutable)) {
                    ((WildRiceCropBlock) bottomRiceState.getBlock()).placeAt(world, blockPosMutable, 2);
                    ++i;
                }
            }
        }

        return i > 0;
    }
}