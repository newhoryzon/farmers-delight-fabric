package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import com.nhoryzon.mc.farmersdelight.registry.ItemsRegistry;
import com.nhoryzon.mc.farmersdelight.util.BlockStateUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.item.ItemConvertible;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class BuddingTomatoBlock extends BuddingBushBlock implements Fertilizable {

    public BuddingTomatoBlock() {
        super(Settings.copy(Blocks.WHEAT));
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(Blocks.FARMLAND) || floor.isOf(BlocksRegistry.RICH_SOIL_FARMLAND.get());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world,
            BlockPos pos, BlockPos neighborPos) {
        if (state.get(BuddingBushBlock.AGE) == 4) {
            world.setBlockState(pos, BlocksRegistry.TOMATO_CROP.get().getDefaultState(), BlockStateUtils.DEFAULT);
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canGrowPastMaxAge() {
        return true;
    }

    @Override
    public void growPastMaxAge(BlockState state, World world, BlockPos pos, Random random) {
        world.setBlockState(pos, BlocksRegistry.TOMATO_CROP.get().getDefaultState());
    }

    @Override
    protected ItemConvertible getBaseSeedId() {
        return ItemsRegistry.TOMATO_SEEDS.get();
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int maxAge = MAX_AGE;
        int ageGrowth = Math.min(getAge(state) + getBonemealAgeIncrease(world), 7);
        if (ageGrowth <= maxAge) {
            world.setBlockState(pos, state.with(AGE, ageGrowth));
        } else {
            world.setBlockState(pos, BlocksRegistry.TOMATO_CROP.get().getDefaultState().with(TomatoVineBlock.VINE_AGE, ageGrowth - maxAge - 1));
        }
    }

    protected int getBonemealAgeIncrease(World world) {
        return MathHelper.nextInt(world.random, 1, 4);
    }

}
