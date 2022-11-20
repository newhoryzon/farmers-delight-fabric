package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import com.nhoryzon.mc.farmersdelight.registry.ItemsRegistry;
import com.nhoryzon.mc.farmersdelight.registry.SoundsRegistry;
import com.nhoryzon.mc.farmersdelight.registry.TagsRegistry;
import com.nhoryzon.mc.farmersdelight.util.BlockStateUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class TomatoVineBlock extends CropBlock {

    public static final IntProperty VINE_AGE = Properties.AGE_3;
    public static final BooleanProperty ROPELOGGED = BooleanProperty.of("ropelogged");

    private static final VoxelShape SHAPE = Block.createCuboidShape(.0d, .0d, .0d, 16.d, 16.d, 16.d);

    public TomatoVineBlock() {
        super(Settings.copy(Blocks.WHEAT));
        setDefaultState(getStateManager().getDefaultState().with(getAgeProperty(), 0).with(ROPELOGGED, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int age = state.get(getAgeProperty());
        boolean isMature = age == getMaxAge();
        if (!isMature && player.getStackInHand(hand).isOf(Items.BONE_MEAL)) {
            return ActionResult.PASS;
        } else if (isMature) {
            int quantity = 1 + world.random.nextInt(2);
            Block.dropStack(world, pos, new ItemStack(ItemsRegistry.TOMATO.get(), quantity));

            if (world.random.nextFloat() < .05f) {
                Block.dropStack(world, pos, new ItemStack(ItemsRegistry.ROTTEN_TOMATO.get()));
            }

            world.playSound(null, pos, SoundsRegistry.BLOCK_TOMATO_PICK_FROM_BUSH.get(), SoundCategory.BLOCKS,
                    1.f, .8f + world.random.nextFloat() * .4f);
            world.setBlockState(pos, state.with(getAgeProperty(), 0), BlockStateUtils.BLOCK_UPDATE);

            return ActionResult.SUCCESS;
        } else {
            return super.onUse(state, world, pos, player, hand, hit);
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.isRegionLoaded(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            return;
        }

        if (world.getLightLevel(pos, 0) >= 9) {
            int age = getAge(state);
            if (age < getMaxAge()) {
                float speed = CropBlock.getAvailableMoisture(this, world, pos);
                if (random.nextInt((int) (25.f / speed) + 1) == 0) {
                    world.setBlockState(pos, state.with(getAgeProperty(), age + 1), BlockStateUtils.BLOCK_UPDATE);
                }
            }
            attemptRopeClimb(world, pos, random);
        }
    }

    @Override
    public BlockState withAge(int age) {
        return getDefaultState().with(getAgeProperty(), age);
    }

    @Override
    public IntProperty getAgeProperty() {
        return VINE_AGE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public int getMaxAge() {
        return 3;
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return ItemsRegistry.TOMATO_SEEDS.get();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(VINE_AGE, ROPELOGGED);
    }

    @Override
    protected int getGrowthAmount(World world) {
        return super.getGrowthAmount(world) / 2;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int newAge = getAge(state) + getGrowthAmount(world);
        int maxAge = getMaxAge();
        if (newAge > maxAge) {
            newAge = maxAge;
        }

        world.setBlockState(pos, state.with(getAgeProperty(), newAge));
        attemptRopeClimb(world, pos, random);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos belowPos = pos.down();
        BlockState belowState = world.getBlockState(belowPos);

        if (state.get(TomatoVineBlock.ROPELOGGED)) {
            return belowState.isOf(BlocksRegistry.TOMATO_CROP.get()) && hasGoodCropConditions(world, pos);
        }

        return super.canPlaceAt(state, world, pos);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity,
            ItemStack stack) {
        boolean isRopelogged = state.get(ROPELOGGED);
        super.afterBreak(world, player, pos, state, blockEntity, stack);

        if (isRopelogged) {
            destroyAndPlaceRope(world, pos);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world,
            BlockPos pos, BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            world.createAndScheduleBlockTick(pos, this, 1);
        }

        return state;
    }

    public void attemptRopeClimb(ServerWorld world, BlockPos pos, Random random) {
        if (random.nextFloat() < .3f) {
            BlockPos posAbove = pos.up();
            BlockState stateAbove = world.getBlockState(posAbove);
            boolean canClimb = FarmersDelightMod.CONFIG.isEnableTomatoVineClimbingTaggedRopes() ? stateAbove.isIn(TagsRegistry.ROPES)
                    : stateAbove.isOf(BlocksRegistry.ROPE.get());
            if (canClimb) {
                int vineHeight = 1;
                while (world.getBlockState(pos.up(vineHeight)).isOf(this)) {
                    ++vineHeight;
                }
                if (vineHeight < 3) {
                    world.setBlockState(posAbove, getDefaultState().with(ROPELOGGED, true));
                }
            }
        }
    }

    public boolean hasGoodCropConditions(WorldView level, BlockPos pos) {
        return level.getLightLevel(pos, 0) >= 8 || level.isSkyVisible(pos);
    }

    public void destroyAndPlaceRope(World world, BlockPos pos) {
        Block configuredRopeBlock = Registry.BLOCK.get(new Identifier(FarmersDelightMod.CONFIG.getDefaultTomatoVineRope()));
        Block finalRopeBlock = configuredRopeBlock != Blocks.AIR ? configuredRopeBlock : BlocksRegistry.ROPE.get();

        world.setBlockState(pos, finalRopeBlock.getDefaultState());
    }

}
