package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.entity.block.SkilletBlockEntity;
import com.nhoryzon.mc.farmersdelight.registry.BlockEntityTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.SoundsRegistry;
import com.nhoryzon.mc.farmersdelight.registry.TagsRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class SkilletBlock extends BlockWithEntity {

    public static final int MINIMUM_COOKING_TIME = 60;

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty SUPPORT = BooleanProperty.of("support");

    protected static final VoxelShape SHAPE = Block.createCuboidShape(1.d, .0d, 1.d, 15.d, 4.d, 15.d);
    protected static final VoxelShape SHAPE_WITH_TRAY = VoxelShapes.union(SHAPE, Block.createCuboidShape(.0d, -1.d, .0d, 16.d, .0d, 16.d));

    public SkilletBlock() {
        super(FabricBlockSettings.of(Material.METAL).strength(.5f, 6.f).sounds(BlockSoundGroup.LANTERN));
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(SUPPORT, false));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityTypesRegistry.SKILLET.get().instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient()) {
            return checkType(type, BlockEntityTypesRegistry.SKILLET.get(), SkilletBlockEntity::animationTick);
        } else {
            return checkType(type, BlockEntityTypesRegistry.SKILLET.get(), SkilletBlockEntity::cookingTick);
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SkilletBlockEntity skilletBlockEntity) {
            if (!world.isClient()) {
                ItemStack heldStack = player.getStackInHand(hand);
                EquipmentSlot heldSlot = hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                if (heldStack.isEmpty()) {
                    ItemStack extractedStack = skilletBlockEntity.removeItem();
                    if (!player.isCreative()) {
                        player.equipStack(heldSlot, extractedStack);
                    }

                    return ActionResult.SUCCESS;
                } else {
                    ItemStack remainderStack = skilletBlockEntity.addItemToCook(heldStack, player);
                    if (remainderStack.getCount() != heldStack.getCount()) {
                        if (!player.isCreative()) {
                            player.equipStack(heldSlot, remainderStack);
                        }
                        world.playSound(null, pos, SoundEvents.BLOCK_LANTERN_PLACE, SoundCategory.BLOCKS, .7f, 1.f);

                        return ActionResult.SUCCESS;
                    }
                }
            }

            return ActionResult.CONSUME;
        }

        return ActionResult.PASS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (world.getBlockEntity(pos) instanceof SkilletBlockEntity skilletBlockEntity) {
                ItemScatterer.spawn(world, pos, skilletBlockEntity.getInventory());
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, SUPPORT);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return getDefaultState().with(FACING, context.getPlayerFacing()).with(SUPPORT, getTrayState(context.getWorld(), context.getBlockPos()));
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack stack = super.getPickStack(world, pos, state);
        SkilletBlockEntity skilletEntity = (SkilletBlockEntity) world.getBlockEntity(pos);
        NbtCompound nbt = new NbtCompound();
        if (skilletEntity != null) {
            skilletEntity.writeSkilletItem(nbt);
        }
        if (!nbt.isEmpty()) {
            stack = ItemStack.fromNbt(nbt.getCompound("Skillet"));
        }

        return stack;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SkilletBlockEntity skilletBlockEntity && skilletBlockEntity.isCooking()) {
            double x = (double) pos.getX() + 0.5D;
            double y = pos.getY();
            double z = (double) pos.getZ() + 0.5D;
            if (random.nextInt(10) == 0) {
                world.playSound(x, y, z, SoundsRegistry.BLOCK_SKILLET_SIZZLE.get(), SoundCategory.BLOCKS,
                        .4f, random.nextFloat() * .2f + .9f, false);
            }
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Boolean.TRUE.equals(state.get(SUPPORT)) ? SHAPE_WITH_TRAY : SHAPE;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    private boolean getTrayState(WorldAccess worldAccess, BlockPos pos) {
        return worldAccess.getBlockState(pos.down()).isIn(TagsRegistry.TRAY_HEAT_SOURCES);
    }

    /**
     * Calculates the total cooking time for the Skillet, affected by Fire Aspect.
     * Assuming a default of 30 seconds (600 ticks), the time is divided by 5, then reduced further per level of Fire Aspect, to a minimum of 3 seconds.
     * Times are always rounded to a multiple of 20, to ensure exact seconds.
     */
    public static int getSkilletCookingTime(int originalCookingTime, int fireAspectLevel) {
        int cookingTime = originalCookingTime > 0 ? originalCookingTime : 600;
        int cookingSeconds = cookingTime / 20;
        float cookingTimeReduction = .2f;

        if (fireAspectLevel > 0) {
            cookingTimeReduction -= fireAspectLevel * .05;
        }

        int result = (int) (cookingSeconds * cookingTimeReduction) * 20;

        return MathHelper.clamp(result, MINIMUM_COOKING_TIME, originalCookingTime);
    }

}
