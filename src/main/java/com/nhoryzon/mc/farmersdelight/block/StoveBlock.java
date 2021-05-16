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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

public class StoveBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = Properties.LIT;

    public StoveBlock() {
        super(FabricBlockSettings.copyOf(Blocks.BRICKS).luminance(state -> state.get(Properties.LIT) ? 13 : 0));
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(LIT, false));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return BlockEntityTypesRegistry.STOVE.get().instantiate();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        Item usedItem = itemStack.getItem();
        if (state.get(LIT)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof StoveBlockEntity) {
                StoveBlockEntity stoveBlockEntity = (StoveBlockEntity) blockEntity;
                Optional<CampfireCookingRecipe> optional = stoveBlockEntity.findMatchingRecipe(itemStack);
                if (optional.isPresent()) {
                    if (!world.isClient() && !stoveBlockEntity.isStoveBlockedAbove() && stoveBlockEntity.addItem(
                            player.abilities.creativeMode ? itemStack.copy() : itemStack, optional.get().getCookTime())) {
                        player.incrementStat(Stats.INTERACT_WITH_CAMPFIRE);

                        return ActionResult.SUCCESS;
                    }

                    return ActionResult.CONSUME;
                } else {
                    if (usedItem instanceof ShovelItem) {
                        extinguish(state, world, pos);

                        return ActionResult.SUCCESS;
                    } else if (usedItem == Items.WATER_BUCKET) {
                        extinguish(state, world, pos);
                        if (!player.isCreative()) {
                            player.setStackInHand(hand, new ItemStack(Items.BUCKET));
                        }

                        return ActionResult.SUCCESS;
                    }
                }
            }
        } else {
            if (usedItem instanceof FlintAndSteelItem) {
                world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.f,
                        MathUtils.RAND.nextFloat() * .4f + .8f);
                world.setBlockState(pos, state.with(LIT, Boolean.TRUE), BlockStateUtils.DEFAULT_AND_RERENDER);
                itemStack.damage(1, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
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
    public void onSteppedOn(World world, BlockPos pos, Entity entity) {
        boolean isLit = world.getBlockState(pos).get(LIT);
        if (isLit && !entity.isFireImmune() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entity)) {
            entity.damage(DamageSource.HOT_FLOOR, 1.f);
        }

        super.onSteppedOn(world, pos, entity);
    }

    @Override
    @Environment(value= EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(CampfireBlock.LIT)) {
            double dx = (double) pos.getX() + .5d;
            double dy = pos.getY();
            double dz = (double) pos.getZ() + .5d;
            if (random.nextInt(10) == 0) {
                world.playSound(dx, dy, dz, SoundsRegistry.BLOCK_STOVE_CRACKLE.get(), SoundCategory.BLOCKS, 1.f, 1.f, false);
            }

            Direction direction = state.get(FACING);
            Direction.Axis axis = direction.getAxis();
            double d0 = random.nextDouble() * .6d - .3d;
            double d1 = (axis == Direction.Axis.X ? (double) direction.getOffsetX() * .52d : d0);
            double d2 = random.nextDouble() * 6.d / 16.d;
            double d3 = (axis == Direction.Axis.Z ? (double) direction.getOffsetZ() * .52d : d0);
            world.addParticle(ParticleTypes.SMOKE, dx + d1, dy + d2, dz + d3, .0d, .0d, .0d);
            world.addParticle(ParticleTypes.FLAME, dx + d1, dy + d2, dz + d3, .0d, .0d, .0d);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof StoveBlockEntity) {
                ItemScatterer.spawn(world, pos, ((StoveBlockEntity) blockEntity).getInventory());
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    private void extinguish(BlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, state.with(LIT, false));
        double dx = (double) pos.getX() + .5d;
        double dy = pos.getY();
        double dz = (double) pos.getZ() + .5d;
        world.playSound(dx, dy, dz, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, .5f, 2.6f, false);
    }
}