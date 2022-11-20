package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import com.nhoryzon.mc.farmersdelight.util.BlockStateUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

/**
 * A bush which grows, representing the earlier stage of another plant.
 * Once mature, a budding bush can "grow past" it, and turn into something different.
 */
public abstract class BuddingBushBlock extends PlantBlock {

    public static final int MAX_AGE = 3;

    public static final IntProperty AGE = IntProperty.of("age", 0, 4);

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 2.d, 16.d),
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 6.d, 16.d),
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 1.d, 16.d),
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 14.d, 16.d),
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 14.d, 16.d)};

    public BuddingBushBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE_BY_AGE[state.get(AGE)];
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(Blocks.FARMLAND);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return canGrowPastMaxAge() || !isMaxAge(state);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.isRegionLoaded(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            return;
        }

        if (world.getLightLevel(pos, 0) >= 9) {
            int age = getAge(state);
            if (age <= MAX_AGE) {
                float growthChance = getGrowthChance(world, pos);
                if (random.nextInt((int) (25.f / growthChance) + 1) == 0) {
                    if (isMaxAge(state)) {
                        growPastMaxAge(state, world, pos, random);
                    } else {
                        world.setBlockState(pos, getStateForAge(age + 1));
                    }
                }
            }
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return (world.getLightLevel(pos, 0) >= 8 || world.isSkyVisible(pos)) && super.canPlaceAt(state, world, pos);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof RavagerEntity && world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            world.breakBlock(pos, true, entity);
        }

        super.onEntityCollision(state, world, pos, entity);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(getBaseSeedId());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    public abstract void growPastMaxAge(BlockState state, World world, BlockPos pos, Random random);

    protected abstract ItemConvertible getBaseSeedId();

    public boolean canGrowPastMaxAge() {
        return false;
    }

    public boolean isMaxAge(BlockState state) {
        return getAge(state) >= MAX_AGE;
    }

    public int getAge(BlockState state) {
        return state.get(AGE);
    }

    public BlockState getStateForAge(int age) {
        return this.getDefaultState().with(AGE, age);
    }

    protected float getGrowthChance(World world, BlockPos pos) {
        float growthChance = 1.f;

        growthChance = getGrowthChanceWithGroundedFarmland(growthChance, world, pos);
        growthChance = getGrowthChanceWithSurroundingBlock(growthChance, world, pos);

        return growthChance;
    }

    protected float getGrowthChanceWithGroundedFarmland(float startingChance, World world, BlockPos pos) {
        float finalChance = startingChance;
        BlockPos floorPos = pos.down();
        BlockState blockState = world.getBlockState(floorPos);

        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                float floorGrowthChance = .0f;
                BlockState floorBlockState = world.getBlockState(floorPos.add(i, 0, j));
                if (canPlaceAt(blockState, world, pos.add(i, 0, j))) {
                    floorGrowthChance = 1.f;
                    if (floorBlockState.getBlock() instanceof FarmlandBlock && floorBlockState.get(FarmlandBlock.MOISTURE) == 7) {
                        floorGrowthChance = 3.f;
                    }
                }

                if (i != 0 || j != 0) {
                    floorGrowthChance /= 4.f;
                }

                finalChance += floorGrowthChance;
            }
        }

        return finalChance;
    }

    protected float getGrowthChanceWithSurroundingBlock(float startingChance, World world, BlockPos pos) {
        float finalChance = startingChance;

        BlockPos northPos = pos.north();
        BlockPos southPos = pos.south();
        BlockPos westPos = pos.west();
        BlockPos eastPos = pos.east();
        boolean isMatchedWestEast = world.getBlockState(westPos).isOf(this) || world.getBlockState(eastPos).isOf(this);
        boolean isMatchedNorthSouth = world.getBlockState(northPos).isOf(this) || world.getBlockState(southPos).isOf(this);
        if (isMatchedWestEast && isMatchedNorthSouth) {
            finalChance /= 2.f;
        } else {
            boolean flag2 = world.getBlockState(westPos.north()).isOf(this)
                    || world.getBlockState(eastPos.north()).isOf(this)
                    || world.getBlockState(eastPos.south()).isOf(this)
                    || world.getBlockState(westPos.south()).isOf(this);
            if (flag2) {
                finalChance /= 2.f;
            }
        }

        return finalChance;
    }

}
