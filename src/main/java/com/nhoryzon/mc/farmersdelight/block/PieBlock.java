package com.nhoryzon.mc.farmersdelight.block;

import com.mojang.datafixers.util.Pair;
import com.nhoryzon.mc.farmersdelight.tag.Tags;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class PieBlock extends Block {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final IntProperty BITES = IntProperty.of("bites", 0, 3);
    public static final int MAX_BITES = 4;

    protected static final VoxelShape SHAPE = Block.createCuboidShape(2.d, .0d, 2.d, 14.d, 4.d, 14.d);

    public final Item pieSlice;

    public PieBlock(Item pieSlice) {
        super(FabricBlockSettings.copyOf(Blocks.CAKE));
        this.pieSlice = pieSlice;
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(BITES, 0));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return getDefaultState().with(FACING, context.getPlayerFacing());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, BITES);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);

        if (player.getMainHandStack().isIn(Tags.KNIVES)) {
            PieBlock pieBlock = (PieBlock) state.getBlock();
            ItemStack pieSlices = pieBlock.getPieSliceStack();
            pieSlices.setCount(MAX_BITES - state.get(PieBlock.BITES));
            ItemScatterer.spawn(world, pos, DefaultedList.ofSize(1, pieSlices));
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemstack = player.getStackInHand(hand);
        if (world.isClient()) {
            if (itemstack.isIn(Tags.KNIVES)) {
                return cutSlice(world, pos, state);
            }

            if (consumeBite(world, pos, state, player) == ActionResult.SUCCESS) {
                return ActionResult.SUCCESS;
            }

            if (itemstack.isEmpty()) {
                return ActionResult.CONSUME;
            }
        }

        if (itemstack.isIn(Tags.KNIVES)) {
            return cutSlice(world, pos, state);
        }

        return consumeBite(world, pos, state, player);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos,
            BlockPos posFrom) {
        return direction == Direction.DOWN && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState()
                : super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).getMaterial().isSolid();
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return MAX_BITES - state.get(BITES);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    public ItemStack getPieSliceStack() {
        return new ItemStack(pieSlice);
    }

    protected ActionResult consumeBite(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!player.canConsume(false)) {
            return ActionResult.PASS;
        }

        ItemStack slice = getPieSliceStack();
        FoodComponent sliceFood = slice.getItem().getFoodComponent();

        player.getHungerManager().eat(slice.getItem(), slice);
        if (getPieSliceStack().getItem().isFood() && sliceFood != null) {
            for (Pair<StatusEffectInstance, Float> pair : sliceFood.getStatusEffects()) {
                if (!world.isClient() && pair.getFirst() != null && world.getRandom().nextFloat() < pair.getSecond()) {
                    player.addStatusEffect(new StatusEffectInstance(pair.getFirst()));
                }
            }
        }

        int bites = state.get(BITES);
        if (bites < MAX_BITES - 1) {
            world.setBlockState(pos, state.with(BITES, bites + 1), 3);
        } else {
            world.removeBlock(pos, false);
        }
        world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 0.8F, 0.8F);

        return ActionResult.SUCCESS;
    }

    protected ActionResult cutSlice(World world, BlockPos pos, BlockState state) {
        int bites = state.get(BITES);
        if (bites < MAX_BITES - 1) {
            world.setBlockState(pos, state.with(BITES, bites + 1), 3);
        } else {
            world.removeBlock(pos, false);
        }
        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), getPieSliceStack());
        world.playSound(null, pos, SoundEvents.BLOCK_WOOL_BREAK, SoundCategory.PLAYERS, 0.8F, 0.8F);

        return ActionResult.SUCCESS;
    }

}