package com.nhoryzon.mc.farmersdelight.world.feature;

import com.mojang.serialization.Codec;
import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;

public class RiceCropFeature extends Feature<RandomPatchFeatureConfig> {
    public RiceCropFeature(Codec<RandomPatchFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<RandomPatchFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        RandomPatchFeatureConfig config = context.getConfig();
        Random random = context.getRandom();
        BlockPos blockPos = world.getTopPosition(Heightmap.Type.OCEAN_FLOOR_WG, origin);

        int i = 0;
        BlockPos.Mutable blockPosMutable = new BlockPos.Mutable();

        for (int j = 0; j < config.tries(); ++j) {
            blockPosMutable.set(blockPos).move(
                    random.nextInt(config.xzSpread() + 1) - random.nextInt(config.xzSpread() + 1),
                    random.nextInt(config.ySpread() + 1) - random.nextInt(config.ySpread() + 1),
                    random.nextInt(config.xzSpread() + 1) - random.nextInt(config.xzSpread() + 1));

            if (world.getBlockState(blockPosMutable).getBlock() == Blocks.WATER && world.getBlockState(blockPosMutable.up()).getBlock() == Blocks.AIR) {
                BlockState bottomRiceState = BlocksRegistry.WILD_RICE.get().getDefaultState().with(TallPlantBlock.HALF, DoubleBlockHalf.LOWER);
                if (bottomRiceState.canPlaceAt(world, blockPosMutable)) {
                    TallPlantBlock.placeAt(world, bottomRiceState, blockPosMutable, 2);
                    ++i;
                }
            }
        }

        return i > 0;
    }
}