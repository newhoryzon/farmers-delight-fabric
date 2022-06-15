package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import com.nhoryzon.mc.farmersdelight.util.BlockStateUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class MushroomColonyBlock extends PlantBlock implements Fertilizable {
    public static final int GROWING_LIGHT_LEVEL = 12;
    public static final int PLACING_LIGHT_LEVEL = 13;

    public static final IntProperty AGE = Properties.AGE_3;
    public static final int MAX_AGE = 3;

    protected static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.createCuboidShape(4.d, .0d, 4.d, 12.d, 8.d, 12.d),
            Block.createCuboidShape(3.d, .0d, 3.d, 13.d, 10.d, 13.d),
            Block.createCuboidShape(2.d, .0d, 2.d, 14.d, 12.d, 14.d),
            Block.createCuboidShape(1.d, .0d, 1.d, 15.d, 14.d, 15.d)};

    private final Item mushroomType;

    public MushroomColonyBlock(Settings settings, Item mushroomType) {
        super(settings);
        this.mushroomType = mushroomType;
        setDefaultState(getStateManager().getDefaultState().with(AGE, 0));
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int age = Math.min(3, state.get(AGE) + 1);
        world.setBlockState(pos, state.with(AGE, age), BlockStateUtils.BLOCK_UPDATE);
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return state.get(AGE) < 3;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        int age = state.get(AGE);
        BlockState groundState = world.getBlockState(pos.down());
        if (age < MAX_AGE && groundState.getBlock() == BlocksRegistry.RICH_SOIL.get() && world.getLightLevel(pos.up(), 0) <=
                GROWING_LIGHT_LEVEL && random.nextInt(5) == 0) {
            world.setBlockState(pos, state.with(AGE, age + 1), BlockStateUtils.BLOCK_UPDATE);
        }
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(mushroomType);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(AGE);
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOpaqueFullCube(world, pos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockpos = pos.down();
        BlockState blockstate = world.getBlockState(blockpos);
        if (blockstate.isIn(BlockTags.MUSHROOM_GROW_BLOCK)) {
            return true;
        } else {
            return world.getLightLevel(pos, 0) < PLACING_LIGHT_LEVEL && canPlantOnTop(blockstate, world, pos);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int age = state.get(AGE);
        ItemStack heldItem = player.getStackInHand(hand);

        if (age > 0 && heldItem.getItem() instanceof ShearsItem) {
            dropStack(world, pos, getPickStack(world, pos, state));
            world.playSound(null, pos, SoundEvents.ENTITY_MOOSHROOM_SHEAR, SoundCategory.BLOCKS, 1.f, 1.f);
            world.setBlockState(pos, state.with(AGE, age - 1), BlockStateUtils.BLOCK_UPDATE);
            if (!world.isClient()) {
                heldItem.damage(1, player, playerIn -> playerIn.sendToolBreakStatus(hand));
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE_BY_AGE[state.get(AGE)];
    }

}
