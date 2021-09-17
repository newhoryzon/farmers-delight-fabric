package com.nhoryzon.mc.farmersdelight.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class TatamiBlock extends Block {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty PAIRED = BooleanProperty.of("paired");

    public TatamiBlock() {
        super(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL));
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.DOWN).with(PAIRED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, PAIRED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        Direction face = context.getSide();
        BlockPos targetPos = context.getBlockPos().offset(face.getOpposite());
        BlockState targetState = context.getWorld().getBlockState(targetPos);
        boolean pairing = context.getPlayer() != null && !context.getPlayer().isSneaking() && targetState.isOf(this) &&
                !Boolean.TRUE.equals(targetState.get(PAIRED));

        return getDefaultState().with(FACING, face.getOpposite()).with(PAIRED, pairing);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient()) {
            if (placer != null && placer.isSneaking()) {
                return;
            }

            BlockPos facingPos = pos.offset(state.get(FACING));
            BlockState facingState = world.getBlockState(facingPos);
            if (facingState.isOf(this) && !Boolean.TRUE.equals(facingState.get(PAIRED))) {
                world.setBlockState(facingPos, state.with(FACING, state.get(FACING).getOpposite()).with(PAIRED, true));
                world.updateNeighbors(pos, Blocks.AIR);
                state.updateNeighbors(world, pos, 3);
            }
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos,
            BlockPos posFrom) {
        if (direction.equals(state.get(FACING)) && Boolean.TRUE.equals(state.get(PAIRED)) && world.getBlockState(posFrom).isOf(Blocks.AIR)) {
            return state.with(PAIRED, false);
        }

        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}