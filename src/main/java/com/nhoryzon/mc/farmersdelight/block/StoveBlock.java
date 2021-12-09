package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.entity.block.StoveBlockEntity;
import com.nhoryzon.mc.farmersdelight.registry.BlockEntityTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.SoundsRegistry;
import com.nhoryzon.mc.farmersdelight.util.BlockStateUtils;
import com.nhoryzon.mc.farmersdelight.util.MathUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShovelItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

public class StoveBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = Properties.LIT;

    public StoveBlock() {
        super(FabricBlockSettings.copyOf(Blocks.BRICKS).luminance(state -> Boolean.TRUE.equals(state.get(Properties.LIT)) ? 13 : 0));
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(LIT, false));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityTypesRegistry.STOVE.get().instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, BlockEntityTypesRegistry.STOVE.get(), StoveBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!Boolean.TRUE.equals(state.get(LIT))) {
            return tryLightUpByPlayerHand(state, world, pos, player, hand);
        }

        if (world.getBlockEntity(pos) instanceof StoveBlockEntity stoveBlockEntity) {
            return onUseByPlayerHand(stoveBlockEntity, state, world, pos, player, hand);
        }

        return ActionResult.PASS;
    }

    protected ActionResult onUseByPlayerHand(StoveBlockEntity stoveBlockEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        Optional<CampfireCookingRecipe> optional = stoveBlockEntity.findMatchingRecipe(itemStack);

        if (optional.isEmpty()) {
            return tryExtinguishByPlayerHand(state, world, pos, player, hand);
        }

        if (!world.isClient() && !stoveBlockEntity.isStoveBlockedAbove() && stoveBlockEntity.addItem(
                player.getAbilities().creativeMode ? itemStack.copy() : itemStack, optional.get().getCookTime())) {
            player.incrementStat(Stats.INTERACT_WITH_CAMPFIRE);

            return ActionResult.SUCCESS;
        }

        return ActionResult.CONSUME;
    }

    protected ActionResult tryLightUpByPlayerHand(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand) {
        ItemStack stackHand = player.getStackInHand(hand);

        if (!(stackHand.getItem() instanceof FlintAndSteelItem)) {
            return ActionResult.PASS;
        }

        world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.f,
                MathUtils.RAND.nextFloat() * .4f + .8f);
        world.setBlockState(pos, state.with(LIT, Boolean.TRUE), BlockStateUtils.DEFAULT_AND_RERENDER);
        stackHand.damage(1, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));

        return ActionResult.SUCCESS;
    }

    protected ActionResult tryExtinguishByPlayerHand(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand) {
        ItemStack stackHand = player.getStackInHand(hand);
        Item usedItem = stackHand.getItem();

        if (!(usedItem instanceof ShovelItem) && usedItem != Items.WATER_BUCKET) {
            return ActionResult.PASS;
        }

        extinguish(state, world, pos);
        if (!player.isCreative() && usedItem == Items.WATER_BUCKET) {
            player.setStackInHand(hand, new ItemStack(Items.BUCKET));
        }

        return ActionResult.SUCCESS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, LIT);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return getDefaultState().with(FACING, context.getPlayerFacing().getOpposite()).with(LIT, true);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        boolean isLit = world.getBlockState(pos).get(LIT);
        if (isLit && !entity.isFireImmune() && entity instanceof LivingEntity livingEntity &&
                !EnchantmentHelper.hasFrostWalker(livingEntity)) {
            entity.damage(DamageSource.HOT_FLOOR, 1.f);
        }

        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    @Environment(value= EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (Boolean.TRUE.equals(state.get(CampfireBlock.LIT))) {
            double dx = pos.getX() + .5d;
            double dy = pos.getY();
            double dz = pos.getZ() + .5d;
            if (random.nextInt(10) == 0) {
                world.playSound(dx, dy, dz, SoundsRegistry.BLOCK_STOVE_CRACKLE.get(), SoundCategory.BLOCKS, 1.f, 1.f, false);
            }

            Direction direction = state.get(FACING);
            Direction.Axis axis = direction.getAxis();
            double d0 = random.nextDouble() * .6d - .3d;
            double d1 = (axis == Direction.Axis.X ? direction.getOffsetX() * .52d : d0);
            double d2 = random.nextDouble() * 6.d / 16.d;
            double d3 = (axis == Direction.Axis.Z ? direction.getOffsetZ() * .52d : d0);
            world.addParticle(ParticleTypes.SMOKE, dx + d1, dy + d2, dz + d3, .0d, .0d, .0d);
            world.addParticle(ParticleTypes.FLAME, dx + d1, dy + d2, dz + d3, .0d, .0d, .0d);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (world.getBlockEntity(pos) instanceof StoveBlockEntity stoveBlockEntity) {
                ItemScatterer.spawn(world, pos, stoveBlockEntity.getInventory());
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    private void extinguish(BlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, state.with(LIT, false));
        double dx = pos.getX() + .5d;
        double dy = pos.getY();
        double dz = pos.getZ() + .5d;
        world.playSound(dx, dy, dz, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, .5f, 2.6f, false);
    }

}