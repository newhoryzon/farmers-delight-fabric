package com.nhoryzon.mc.farmersdelight.block;

import com.google.common.collect.ImmutableMap;
import com.nhoryzon.mc.farmersdelight.entity.block.BasketBlockEntity;
import com.nhoryzon.mc.farmersdelight.registry.BlockEntityTypesRegistry;
import com.nhoryzon.mc.farmersdelight.util.BlockStateUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.tick.OrderedTick;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class BasketBlock extends InventoryBlockWithEntity implements Waterloggable {

    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty ENABLED = Properties.ENABLED;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public static final VoxelShape OUT_SHAPE = VoxelShapes.fullCube();
    public static final ImmutableMap<Direction, VoxelShape> RENDER_SHAPE_FACING = new ImmutableMap.Builder<Direction, VoxelShape>()
            .put(Direction.DOWN, createCuboidShape(.0d, 15.d, .0d, 16.d, 16.d, 16.d))
            .put(Direction.UP, createCuboidShape(.0d, .0d, .0d, 16.d, 1.d, 16.d))
            .put(Direction.NORTH, createCuboidShape(.0d, .0d, 15.d, 16.d, 16.d, 16.d))
            .put(Direction.SOUTH, createCuboidShape(.0d, .0d, .0d, 16.d, 16.d, 1.d))
            .put(Direction.WEST, createCuboidShape(15.d, .0d, .0d, 16.d, 16.d, 16.d))
            .put(Direction.EAST, createCuboidShape(.0d, .0d, .0d, 1.d, 16.d, 16.d))
            .build();
    public static final ImmutableMap<Direction, VoxelShape> COLLISION_SHAPE_FACING = new ImmutableMap.Builder<Direction, VoxelShape>()
                    .put(Direction.DOWN, makeHollowCubeShape(createCuboidShape(2.d, .0d, 2.d, 14.d, 14.d, 14.d)))
                    .put(Direction.UP, makeHollowCubeShape(createCuboidShape(2.d, 2.d, 2.d, 14.d, 16.d, 14.d)))
                    .put(Direction.NORTH, makeHollowCubeShape(createCuboidShape(2.d, 2.d, .0d, 14.d, 14.d, 14.d)))
                    .put(Direction.SOUTH, makeHollowCubeShape(createCuboidShape(2.d, 2.d, 2.d, 14.d, 14.d, 16.d)))
                    .put(Direction.WEST, makeHollowCubeShape(createCuboidShape(.0d, 2.d, 2.d, 14.d, 14.d, 14.d)))
                    .put(Direction.EAST, makeHollowCubeShape(createCuboidShape(2.d, 2.d, 2.d, 16.d, 14.d, 14.d)))
                    .build();

    public BasketBlock() {
        super(FabricBlockSettings.of(Material.WOOD).hardness(1.5f).resistance(1.5f).sounds(BlockSoundGroup.SCAFFOLDING).nonOpaque());
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.UP).with(WATERLOGGED, false));
    }

    private static VoxelShape makeHollowCubeShape(VoxelShape cutout) {
        return VoxelShapes.combine(OUT_SHAPE, cutout, BooleanBiFunction.ONLY_FIRST).simplify();
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BasketBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, BlockEntityTypesRegistry.BASKET.get(), BasketBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, ENABLED, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
        return getDefaultState().with(FACING, context.getPlayerLookDirection().getOpposite()).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName() && world.getBlockEntity(pos) instanceof BasketBlockEntity basketBlockEntity) {
            basketBlockEntity.setCustomName(itemStack.getName());
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient() && world.getBlockEntity(pos) instanceof BasketBlockEntity basketBlockEntity) {
            player.openHandledScreen(basketBlockEntity);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        updateState(world, pos, state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos,
            BlockPos posFrom) {
        if (Boolean.TRUE.equals(state.get(WATERLOGGED))) {
            world.getFluidTickScheduler().scheduleTick(OrderedTick.create(Fluids.WATER, pos));
        }

        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE_FACING.get(state.get(FACING));
    }

    @Override
    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return RENDER_SHAPE_FACING.get(state.get(FACING));
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return OUT_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE_FACING.get(state.get(FACING));
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return Boolean.TRUE.equals(state.get(WATERLOGGED)) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    private void updateState(World world, BlockPos pos, BlockState state) {
        boolean isPowered = !world.isReceivingRedstonePower(pos);
        if (isPowered != Boolean.TRUE.equals(state.get(ENABLED))) {
            world.setBlockState(pos, state.with(ENABLED, isPowered), BlockStateUtils.BLOCK_UPDATE);

        }
    }
    
}