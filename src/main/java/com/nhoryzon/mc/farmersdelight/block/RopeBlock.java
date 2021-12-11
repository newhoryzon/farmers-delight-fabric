package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BellBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.tick.OrderedTick;

public class RopeBlock extends PaneBlock {
    private static final BooleanProperty TIED_TO_BELL = BooleanProperty.of("tied_to_bell");

    public RopeBlock() {
        super(FabricBlockSettings.of(Material.CARPET).noCollision().nonOpaque().hardness(.2f).resistance(.2f).sounds(BlockSoundGroup.WOOL));
        setDefaultState(getStateManager().getDefaultState().with(TIED_TO_BELL, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(TIED_TO_BELL);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return true;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        World world = context.getWorld();
        BlockPos posAbove = context.getBlockPos().up();
        BlockState state = super.getPlacementState(context);

        return (state != null ? state.with(TIED_TO_BELL, world.getBlockState(posAbove).getBlock() == Blocks.BELL) : null);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.getStackInHand(hand).isEmpty()) {
            BlockPos.Mutable mutable = pos.mutableCopy().move(Direction.UP);

            for (int i = 0; i < 24; i++) {
                BlockState blockStateAbove = world.getBlockState(mutable);
                Block blockAbove = blockStateAbove.getBlock();
                if (blockAbove == Blocks.BELL) {
                    ((BellBlock) blockAbove).ring(world, mutable, blockStateAbove.get(BellBlock.FACING).rotateYClockwise());

                    return ActionResult.SUCCESS;
                } else if (blockAbove == BlocksRegistry.ROPE.get()) {
                    mutable.move(Direction.UP);
                } else {
                    return ActionResult.PASS;
                }
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return context.getStack().getItem() == asItem();
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos,
            BlockPos posFrom) {
        if (Boolean.TRUE.equals(state.get(WATERLOGGED))) {
            world.getFluidTickScheduler().scheduleTick(OrderedTick.create(Fluids.WATER, pos));
        }

        boolean tiedToBell = state.get(TIED_TO_BELL);
        if (direction == Direction.UP) {
            tiedToBell = world.getBlockState(posFrom).getBlock() == Blocks.BELL;
        }

        return (direction.getAxis().isHorizontal() ? state.with(TIED_TO_BELL, tiedToBell).with(FACING_PROPERTIES.get(direction),
                connectsTo(newState, newState.isSideSolidFullSquare(world, posFrom, direction.getOpposite())))
                : super.getStateForNeighborUpdate(state.with(TIED_TO_BELL, tiedToBell), direction, newState, world, pos, posFrom));
    }
}