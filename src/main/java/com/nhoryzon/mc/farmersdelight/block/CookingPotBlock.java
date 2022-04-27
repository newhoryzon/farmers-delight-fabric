package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.entity.block.CookingPotBlockEntity;
import com.nhoryzon.mc.farmersdelight.item.inventory.ItemStackHandler;
import com.nhoryzon.mc.farmersdelight.registry.BlockEntityTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.SoundsRegistry;
import com.nhoryzon.mc.farmersdelight.tag.Tags;
import com.nhoryzon.mc.farmersdelight.util.CompoundTagUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
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

import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class CookingPotBlock extends BlockWithEntity implements InventoryProvider, Waterloggable {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty SUPPORTED = Properties.DOWN;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(2.d, .0d, 2.d, 14.d, 10.d, 14.d);
    protected static final VoxelShape SHAPE_SUPPORTED = VoxelShapes.union(SHAPE, Block.createCuboidShape(.0d, -1.d, .0d, 16.d, .0d, 16.d));

    public CookingPotBlock() {
        super(FabricBlockSettings.of(Material.METAL).hardness(2.f).resistance(6.f).sounds(BlockSoundGroup.LANTERN));
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(SUPPORTED, false).with(WATERLOGGED, false));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityTypesRegistry.COOKING_POT.get().instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, BlockEntityTypesRegistry.COOKING_POT.get(), CookingPotBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, SUPPORTED, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockPos blockPos = context.getBlockPos();
        World world = context.getWorld();
        FluidState fluidState = world.getFluidState(blockPos);

        return getDefaultState()
                .with(FACING, context.getPlayerFacing().getOpposite())
                .with(SUPPORTED, needsTrayForHeatSource(world.getBlockState(blockPos.down())))
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack itemStack = super.getPickStack(world, pos, state);
        CookingPotBlockEntity blockEntity = (CookingPotBlockEntity) world.getBlockEntity(pos);
        if (blockEntity != null) {
            NbtCompound tag = blockEntity.writeMeal(new NbtCompound());
            if (!tag.isEmpty()) {
                itemStack.setSubNbt("BlockEntityTag", tag);
            } else {
                itemStack.setCustomName(blockEntity.getCustomName());
            }
        }

        return itemStack;
    }

    @Override
    @Environment(value= EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);
        NbtCompound tag = stack.getSubNbt("BlockEntityTag");
        if (tag != null) {
            NbtCompound inventoryTag = tag.getCompound(CompoundTagUtils.TAG_KEY_INVENTORY);
            if (inventoryTag.contains("Items", 9)) {
                ItemStackHandler handler = new ItemStackHandler();
                handler.fromTag(inventoryTag);
                ItemStack meal = handler.getStack(6);
                if (!meal.isEmpty()) {
                    MutableText servingsOf = meal.getCount() == 1
                            ? FarmersDelightMod.i18n("tooltip.cooking_pot.single_serving")
                            : FarmersDelightMod.i18n("tooltip.cooking_pot.many_servings", meal.getCount());
                    tooltip.add(servingsOf.formatted(Formatting.GRAY));
                    MutableText mealName = meal.getName().shallowCopy();
                    tooltip.add(mealName.formatted(meal.getRarity().formatting));
                }
            }
        } else {
            tooltip.add(FarmersDelightMod.i18n("tooltip.cooking_pot.empty").formatted(Formatting.GRAY));
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName() && world.getBlockEntity(pos) instanceof CookingPotBlockEntity cookingPotBlockEntity) {
            cookingPotBlockEntity.setCustomName(itemStack.getName());
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CookingPotBlockEntity cookingPotBlockEntity && cookingPotBlockEntity.isAboveLitHeatSource()) {
            double dX = pos.getX() + .5d;
            double dY = pos.getY();
            double dZ = pos.getZ() + .5d;
            if (random.nextInt(10) == 0) {
                world.playSound(dX, dY, dZ, SoundsRegistry.BLOCK_COOKING_POT_BOIL.get(), SoundCategory.BLOCKS, .5f, random.nextFloat() * .2f + .9f, false);
            }
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CookingPotBlockEntity) {
            return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
        }

        return 0;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient() && world.getBlockEntity(pos) instanceof CookingPotBlockEntity cookingPotBlockEntity) {
            ItemStack serving = cookingPotBlockEntity.useHeldItemOnMeal(player.getStackInHand(hand));
            if (serving != ItemStack.EMPTY) {
                if (!player.getInventory().insertStack(serving)) {
                    player.dropItem(serving, false);
                }
                world.playSound(null, pos, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.BLOCKS, 1.f, 1.f);
            } else {
                NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
                if (screenHandlerFactory != null) {
                    player.openHandledScreen(screenHandlerFactory);
                }
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (world.getBlockEntity(pos) instanceof CookingPotBlockEntity cookingPotBlockEntity) {
                ItemScatterer.spawn(world, pos, cookingPotBlockEntity.getDroppableInventory());
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

        if (direction == Direction.DOWN) {
            return state.with(SUPPORTED, needsTrayForHeatSource(newState));
        }

        return state;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Boolean.TRUE.equals(state.get(SUPPORTED)) ? SHAPE_SUPPORTED : SHAPE;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return Boolean.TRUE.equals(state.get(WATERLOGGED)) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof CookingPotBlockEntity cookingPotBlockEntity) {
            return cookingPotBlockEntity.getInventory();
        }

        return null;
    }

    private boolean needsTrayForHeatSource(BlockState state) {
        return state.isIn(Tags.TRAY_HEAT_SOURCES);
    }

}