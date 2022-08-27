package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import com.nhoryzon.mc.farmersdelight.registry.TagsRegistry;
import com.nhoryzon.mc.farmersdelight.util.MathUtils;
import com.nhoryzon.mc.farmersdelight.util.WorldEventUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.StemBlock;
import net.minecraft.block.TallFlowerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class RichSoilFarmlandBlock extends FarmlandBlock {
    public RichSoilFarmlandBlock() {
        super(FabricBlockSettings.copyOf(Blocks.FARMLAND).sounds(BlockSoundGroup.ROOTED_DIRT));
    }

    private static boolean hasWater(WorldView world, BlockPos pos) {
        for (BlockPos blockpos : BlockPos.iterate(pos.add(-4, 0, -4), pos.add(4, 1, 4))) {
            if (world.getFluidState(blockpos).isIn(FluidTags.WATER)) {
                return true;
            }
        }

        return false;
    }

    public static void turnToRichSoil(BlockState state, World worldIn, BlockPos pos) {
        worldIn.setBlockState(pos, pushEntitiesUpBeforeBlockChange(state, BlocksRegistry.RICH_SOIL.get().getDefaultState(), worldIn, pos));
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState aboveState = world.getBlockState(pos.up());
        return super.canPlaceAt(state, world, pos) || aboveState.getBlock() instanceof StemBlock;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            turnToRichSoil(state, world, pos);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int moisture = state.get(MOISTURE);
        if (!hasWater(world, pos) && !world.hasRain(pos.up())) {
            if (moisture > 0) {
                world.setBlockState(pos, state.with(MOISTURE, moisture - 1), 2);
            }
        } else if (moisture < 7) {
            world.setBlockState(pos, state.with(MOISTURE, 7), 2);
        } else if (moisture == 7) {
            if (FarmersDelightMod.CONFIG.getRichSoilBoostChance() == 0.0) {
                return;
            }

            BlockState aboveState = world.getBlockState(pos.up());
            Block aboveBlock = aboveState.getBlock();

            // Do nothing if the plant is unaffected by rich soil farmland
            if (aboveState.isIn(TagsRegistry.UNAFFECTED_BY_RICH_SOIL) || aboveBlock instanceof TallFlowerBlock) {
                return;
            }

            // If all else fails, and it's a plant, give it a growth boost now and then!
            if (aboveBlock instanceof Fertilizable growable
                    && MathUtils.RAND.nextFloat() <= FarmersDelightMod.CONFIG.getRichSoilBoostChance()
                    && growable.isFertilizable(world, pos.up(), aboveState, false)) {
                growable.grow(world, world.getRandom(), pos.up(), aboveState);
                if (!world.isClient()) {
                    world.syncWorldEvent(WorldEventUtils.BONEMEAL_PARTICLES, pos.up(), 0);
                }
            }
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return !getDefaultState().canPlaceAt(context.getWorld(), context.getBlockPos()) ? BlocksRegistry.RICH_SOIL.get().getDefaultState() : super.getPlacementState(context);
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        // Rich Soil is immune to trampling
    }

}