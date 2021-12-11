package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import com.nhoryzon.mc.farmersdelight.registry.ItemsRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.OrderedTick;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class RiceCropBlock extends PlantBlock implements Fertilizable, FluidFillable {

    public static final IntProperty AGE = Properties.AGE_3;
    public static final BooleanProperty SUPPORTING = BooleanProperty.of("supporting");
    public static final int MAX_AGE = 3;

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[] {
            Block.createCuboidShape(3.d, .0d, 3.d, 13.d, 8.d, 13.d),
            Block.createCuboidShape(3.d, .0d, 3.d, 13.d, 10.d, 13.d),
            Block.createCuboidShape(2.d, .0d, 2.d, 14.d, 12.d, 14.d),
            Block.createCuboidShape(1.d, .0d, 1.d, 15.d, 16.d, 15.d)
    };

    public RiceCropBlock() {
        super(FabricBlockSettings.copyOf(Blocks.WHEAT));
        setDefaultState(getStateManager().getDefaultState().with(AGE, 0).with(SUPPORTING, false));
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        BlockState upperState = world.getBlockState(pos.up());
        if (upperState.getBlock() instanceof RiceUpperCropBlock riceUpperCropBlock) {
            return !riceUpperCropBlock.isMature(upperState);
        }

        return true;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int ageGrowth = Math.min(getAge(state) + getBonemealAgeIncrease(world), 7);
        if (ageGrowth <= MAX_AGE) {
            world.setBlockState(pos, state.with(AGE, ageGrowth));
        } else {
            BlockState top = world.getBlockState(pos.up());
            if (top.getBlock() == BlocksRegistry.RICE_UPPER_CROP.get()) {
                Fertilizable growable = (Fertilizable) world.getBlockState(pos.up()).getBlock();
                if (growable.isFertilizable(world, pos.up(), top, false)) {
                    growable.grow(world, world.getRandom(), pos.up(), top);
                }
            } else {
                RiceUpperCropBlock riceUpper = (RiceUpperCropBlock) BlocksRegistry.RICE_UPPER_CROP.get();
                int remainingGrowth = ageGrowth - MAX_AGE - 1;
                if (riceUpper.getDefaultState().canPlaceAt(world, pos.up()) && world.isAir(pos.up())) {
                    world.setBlockState(pos, state.with(AGE, MAX_AGE));
                    world.setBlockState(pos.up(), riceUpper.getDefaultState().with(RiceUpperCropBlock.RICE_AGE, remainingGrowth), 2);
                }
            }
        }
    }

    @Override
    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        return false;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        FluidState fluid = world.getFluidState(pos);
        return super.canPlaceAt(state, world, pos) && fluid.isIn(FluidTags.WATER) && fluid.getLevel() == 8;
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return super.canPlantOnTop(floor, world, pos) || floor.isOf(BlocksRegistry.RICH_SOIL.get());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(AGE, SUPPORTING);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(ItemsRegistry.RICE.get());
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        FluidState fluid = context.getWorld().getFluidState(context.getBlockPos());
        return fluid.isIn(FluidTags.WATER) && fluid.getLevel() == 8 ? super.getPlacementState(context) : null;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos,
            BlockPos posFrom) {
        BlockState superState = super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
        if (!superState.isAir()) {
            world.getFluidTickScheduler().scheduleTick(OrderedTick.create(Fluids.WATER, pos));
            if (direction == Direction.UP) {
                return superState.with(SUPPORTING, isSupportingRiceUpper(newState));
            }
        }

        return superState;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return Fluids.WATER.getStill(false);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);

        if (!world.isRegionLoaded(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            return;
        }

        if (world.getLightLevel(pos.up(), 0) >= 6 && getAge(state) <= MAX_AGE && random.nextInt(3) == 0) {
            randomGrowTick(state, world, pos);
        }
    }

    private void randomGrowTick(BlockState state, ServerWorld world, BlockPos pos) {
        int currentAge = getAge(state);
        if (currentAge == MAX_AGE) {
            RiceUpperCropBlock riceUpper = (RiceUpperCropBlock) BlocksRegistry.RICE_UPPER_CROP.get();
            if (riceUpper.getDefaultState().canPlaceAt(world, pos.up()) && world.isAir(pos.up())) {
                world.setBlockState(pos.up(), riceUpper.getDefaultState());
            }
        } else {
            world.setBlockState(pos, withAge(currentAge + 1), 2);
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE_BY_AGE[getAge(state)];
    }

    public boolean isSupportingRiceUpper(BlockState topState) {
        return topState.isOf(BlocksRegistry.RICE_UPPER_CROP.get());
    }

    public BlockState withAge(int age) {
        return getDefaultState().with(AGE, age);
    }

    protected int getAge(BlockState state) {
        return state.get(AGE);
    }

    protected int getBonemealAgeIncrease(World world) {
        return MathHelper.nextInt(world.getRandom(), 1, 4);
    }

}