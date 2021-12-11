package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.entity.block.CuttingBoardBlockEntity;
import com.nhoryzon.mc.farmersdelight.registry.BlockEntityTypesRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.OrderedTick;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class CuttingBoardBlock extends BlockWithEntity implements Waterloggable {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(1.d, .0d, 1.d, 15.d, 1.d, 15.d);

    public static void spawnCuttingParticles(World worldIn, BlockPos pos, ItemStack stack, int count) {
        for (int i = 0; i < count; ++i) {
            Vec3d vec3d = new Vec3d((worldIn.getRandom().nextFloat() - .5d) * .1d, Math.random() * .1d + .1d,
                    (worldIn.getRandom().nextFloat() - .5d) * .1d);
            if (worldIn instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), pos.getX() + .5f,
                        pos.getY() + .1f, pos.getZ() + .5f, 1, vec3d.x, vec3d.y + .05d, vec3d.z, .0d);
            } else {
                worldIn.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), pos.getX() + .5f, pos.getY() + .1f,
                        pos.getZ() + .5f, vec3d.x, vec3d.y + .05d, vec3d.z);
            }
        }
    }

    public CuttingBoardBlock() {
        super(FabricBlockSettings.of(Material.WOOD).hardness(2.f).resistance(2.f).sounds(BlockSoundGroup.WOOD));
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityTypesRegistry.CUTTING_BOARD.get().instantiate(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
        return getDefaultState().with(FACING, context.getPlayerFacing().getOpposite()).with(WATERLOGGED,
                fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public boolean canMobSpawnInside() {
        return true;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ActionResult result = ActionResult.PASS;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof CuttingBoardBlockEntity cuttingBoardBlockEntity)) {
            return result;
        }

        ItemStack itemHeld = player.getStackInHand(hand);

        if (cuttingBoardBlockEntity.isEmpty()) {
            result = tryAddItemFromPlayerHand(world, cuttingBoardBlockEntity, player, hand);
        } else if (!itemHeld.isEmpty()) {
            result = tryProcessCuttingUsingToolInHand(world, cuttingBoardBlockEntity, player, hand);
        } else if (hand.equals(Hand.MAIN_HAND)) {
            pullOutItemWithPlayer(world, cuttingBoardBlockEntity, player);
            return ActionResult.SUCCESS;
        }

        return result;
    }

    private ActionResult tryAddItemFromPlayerHand(World world, CuttingBoardBlockEntity cuttingBoardBlockEntity, PlayerEntity player, Hand hand) {
        ItemStack itemHeld = player.getStackInHand(hand);
        ItemStack itemOffhand = player.getOffHandStack();

        boolean canAddItemForHand = (itemOffhand.isEmpty() || !hand.equals(Hand.MAIN_HAND) || (itemHeld.getItem() instanceof BlockItem))
                && !itemHeld.isEmpty();
        if (canAddItemForHand && cuttingBoardBlockEntity.addItem(player.getAbilities().creativeMode ? itemHeld.copy() : itemHeld)) {
            world.playSound(null, cuttingBoardBlockEntity.getPos(), SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.f, .8f);

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private ActionResult tryProcessCuttingUsingToolInHand(World world, CuttingBoardBlockEntity cuttingBoardBlockEntity, PlayerEntity player, Hand hand) {
        ItemStack itemHeld = player.getStackInHand(hand);
        ItemStack boardItem = cuttingBoardBlockEntity.getStoredItem().copy();
        if (cuttingBoardBlockEntity.processItemUsingTool(itemHeld, player)) {
            spawnCuttingParticles(world, cuttingBoardBlockEntity.getPos(), boardItem, 5);
            return ActionResult.SUCCESS;
        }

        return ActionResult.CONSUME;
    }

    private void pullOutItemWithPlayer(World world, CuttingBoardBlockEntity cuttingBoardBlockEntity, PlayerEntity player) {
        BlockPos pos = cuttingBoardBlockEntity.getPos();

        if (player.isCreative()) {
            cuttingBoardBlockEntity.removeItem();
        } else if (!player.getInventory().insertStack(cuttingBoardBlockEntity.removeItem())) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), cuttingBoardBlockEntity.removeItem());
        }

        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_WOOD_HIT, SoundCategory.BLOCKS, .25f, .5f);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (world.getBlockEntity(pos) instanceof CuttingBoardBlockEntity cuttingBoardBlockEntity) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), cuttingBoardBlockEntity.getStoredItem());
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos,
            BlockPos posFrom) {
        if (Boolean.TRUE.equals(state.get(WATERLOGGED))) {
            world.getFluidTickScheduler().scheduleTick(OrderedTick.create(Fluids.WATER, pos));
        }

        return direction == Direction.DOWN && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState()
                : super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos floorPos = pos.down();

        return hasTopRim(world, floorPos) || sideCoversSmallSquare(world, floorPos, Direction.UP);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
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