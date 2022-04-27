package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import com.nhoryzon.mc.farmersdelight.util.BlockStateUtils;
import com.nhoryzon.mc.farmersdelight.util.WorldEventUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.OrderedTick;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class WildRiceCropBlock extends TallPlantBlock implements Waterloggable, Fertilizable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public WildRiceCropBlock() {
        super(FabricBlockSettings.copyOf(Blocks.TALL_GRASS).sounds(BlockSoundGroup.WET_GRASS));
        setDefaultState(getStateManager().getDefaultState().with(WATERLOGGED, true).with(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return (double) random.nextFloat() < .3f;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        dropStack(world, pos, new ItemStack(this));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        FluidState fluid = world.getFluidState(pos);
        BlockPos floorPos = pos.down();
        if (state.get(HALF) == DoubleBlockHalf.LOWER) {
            return super.canPlaceAt(state, world, pos) && canPlantOnTop(world.getBlockState(floorPos), world, floorPos) && fluid.isIn(
                    FluidTags.WATER) && fluid.getLevel() == 8;
        }

        return super.canPlaceAt(state, world, pos) && world.getBlockState(pos.down()).getBlock() == BlocksRegistry.WILD_RICE.get();
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(), getDefaultState().with(WATERLOGGED, false).with(HALF, DoubleBlockHalf.UPPER));
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            if (player.isCreative()) {
                removeBottomHalf(world, pos, state, player);
            } else {
                dropStacks(state, world, pos, null, player, player.getMainHandStack());
            }
        }

        world.syncWorldEvent(player, WorldEventUtils.BREAK_BLOCK_EFFECTS, pos, getRawIdFromState(state));
        if (state.isIn(BlockTags.GUARDED_BY_PIGLINS)) {
            PiglinBrain.onGuardedBlockInteracted(player, false);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos,
            BlockPos posFrom) {
        BlockState blockstate = super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
        DoubleBlockHalf half = state.get(HALF);
        if (!blockstate.isAir()) {
            world.getFluidTickScheduler().scheduleTick(OrderedTick.create(Fluids.WATER, pos));
        }
        if (direction.getAxis() != Direction.Axis.Y || half == DoubleBlockHalf.LOWER != (direction == Direction.UP) ||
                newState.getBlock() == this && newState.get(HALF) != half) {
            return half == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos) ? Blocks.AIR
                    .getDefaultState() : state;
        } else {
            return Blocks.AIR.getDefaultState();
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockPos pos = context.getBlockPos();
        FluidState fluid = context.getWorld().getFluidState(pos);
        return pos.getY() < context.getWorld().getHeight() - 1
                && fluid.isIn(FluidTags.WATER)
                && fluid.getLevel() == 8
                && context.getWorld().getBlockState(pos.up()).isAir()
                ? super.getPlacementState(context) : null;
    }

    @Override
    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return state.get(HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(HALF) == DoubleBlockHalf.LOWER
                ? Fluids.WATER.getStill(false)
                : Fluids.EMPTY.getDefaultState();
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return false;
    }

    protected static void removeBottomHalf(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        DoubleBlockHalf half = state.get(HALF);
        if (half == DoubleBlockHalf.UPPER) {
            BlockPos floorPos = pos.down();
            BlockState floorState = world.getBlockState(floorPos);
            if (floorState.getBlock() == state.getBlock() && floorState.get(HALF) == DoubleBlockHalf.LOWER) {
                world.setBlockState(floorPos, Blocks.WATER.getDefaultState(), BlockStateUtils.DEFAULT | BlockStateUtils.NO_NEIGHBOR_DROPS);
                world.syncWorldEvent(player, WorldEventUtils.BREAK_BLOCK_EFFECTS, floorPos, Block.getRawIdFromState(floorState));
            }
        }
    }
}