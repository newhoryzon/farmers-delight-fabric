package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import com.nhoryzon.mc.farmersdelight.registry.ItemsRegistry;
import com.nhoryzon.mc.farmersdelight.registry.SoundsRegistry;
import com.nhoryzon.mc.farmersdelight.util.BlockStateUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.Random;

public class TomatoBushCropBlock extends CropBlock implements Fertilizable {
    public static final IntProperty AGE = Properties.AGE_7;
    private static final int TOMATO_BEARING_AGE = 7;
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 6.d, 16.d),
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 6.d, 16.d),
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 10.d, 16.d),
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 10.d, 16.d),
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 13.d, 16.d),
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 13.d, 16.d),
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 16.d, 16.d),
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 16.d, 16.d)};

    public TomatoBushCropBlock() {
        super(FabricBlockSettings.copyOf(Blocks.WHEAT));
        setDefaultState(getStateManager().getDefaultState().with(AGE, 0));
    }

    @Override
    public IntProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return !isMature(state);
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int newAge = getAge(state) + getGrowthAmount(world);
        int maxAge = getMaxAge();
        if (newAge > maxAge) {
            newAge = maxAge;
        }

        world.setBlockState(pos, withAge(newAge), BlockStateUtils.BLOCK_UPDATE);
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(Blocks.FARMLAND) || floor.isOf(BlocksRegistry.RICH_SOIL_FARMLAND.get());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(ItemsRegistry.TOMATO_SEED.get());
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return (world.getLightLevel(pos, 0) >= 8 || world.isSkyVisible(pos)) && super.canPlaceAt(state, world, pos);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return state.get(AGE) < TOMATO_BEARING_AGE;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof RavagerEntity && world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            world.breakBlock(pos, true, entity);
        }

        super.onEntityCollision(state, world, pos, entity);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int age = state.get(AGE);
        boolean isMature = age == TOMATO_BEARING_AGE;

        if (!isMature && player.getStackInHand(hand).getItem() == Items.BONE_MEAL) {
            return ActionResult.PASS;
        } else if (isMature) {
            int j = 1 + world.getRandom().nextInt(2);
            dropStack(world, pos, new ItemStack(ItemsRegistry.TOMATO.get(), j));
            world.playSound(null, pos, SoundsRegistry.ITEM_TOMATO_PICK_FROM_BUSH.get(), SoundCategory.BLOCKS, 1.f, .8f + world.getRandom().nextFloat() * .4f);
            world.setBlockState(pos, state.with(AGE, TOMATO_BEARING_AGE - 2), 2);

            return ActionResult.SUCCESS;
        } else {
            return super.onUse(state, world, pos, player, hand, hit);
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE_BY_AGE[state.get(AGE)];
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);

        if (world.isChunkLoaded(pos.getX(), pos.getZ())) {
            return;
        }

        if (world.getLightLevel(pos, 0) >= 9) {
            int age = getAge(state);
            if (age < getMaxAge()) {
                float growthChance = getGrowthChance(world, pos);
                if (random.nextInt((int) (25.f / growthChance) + 1) == 0) {
                    world.setBlockState(pos, withAge(age + 1), BlockStateUtils.BLOCK_UPDATE);
                }
            }
        }
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