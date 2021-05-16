package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.util.BlockStateUtils;
import com.nhoryzon.mc.farmersdelight.util.WorldEventUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class TatamiMatBlock extends HorizontalFacingBlock {
    public static final EnumProperty<BedPart> PART = Properties.BED_PART;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(.0d, .0d, .0d, 16.d, 2.d, 16.d);

    public TatamiMatBlock() {
        super(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).hardness(.3f).resistance(.3f));
        setDefaultState(getStateManager().getDefaultState().with(PART, BedPart.FOOT));
    }

    private static Direction getDirectionToOther(BedPart part, Direction direction) {
        return part == BedPart.FOOT ? direction : direction.getOpposite();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, PART);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        Direction facing = context.getPlayerFacing();
        BlockPos pos = context.getBlockPos();
        BlockPos pairPos = pos.offset(facing);

        return context.getWorld().getBlockState(pairPos).canReplace(context) ? getDefaultState().with(FACING, facing) : null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient()) {
            BlockPos facingPos = pos.offset(state.get(FACING));
            world.setBlockState(facingPos, state.with(PART, BedPart.HEAD));
            world.updateNeighbors(pos, Blocks.AIR);
            state.updateNeighbors(world, pos, 3);
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && player.isCreative()) {
            BedPart part = state.get(PART);
            if (part == BedPart.FOOT) {
                BlockPos pairPos = pos.offset(getDirectionToOther(part, state.get(FACING)));
                BlockState pairState = world.getBlockState(pairPos);
                if (pairState.isOf(this) && pairState.get(PART) == BedPart.HEAD) {
                    world.setBlockState(pairPos, Blocks.AIR.getDefaultState(), BlockStateUtils.DEFAULT | BlockStateUtils.NO_NEIGHBOR_DROPS);
                    world.syncWorldEvent(player, WorldEventUtils.BREAK_BLOCK_EFFECTS, pairPos, Block.getRawIdFromState(pairState));
                }
            }
        }

        super.onBreak(world, pos, state, player);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos,
            BlockPos posFrom) {
        if (direction == getDirectionToOther(state.get(PART), state.get(FACING))) {
            return state.canPlaceAt(world, pos) && newState.isOf(this) && newState.get(PART) != state.get(PART) ? state
                    : Blocks.AIR.getDefaultState();
        } else {
            return !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction,
                    newState, world, pos, posFrom);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return !world.isAir(pos.down());
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}