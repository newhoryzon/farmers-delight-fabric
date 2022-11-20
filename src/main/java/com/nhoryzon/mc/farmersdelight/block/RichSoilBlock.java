package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import com.nhoryzon.mc.farmersdelight.registry.TagsRegistry;
import com.nhoryzon.mc.farmersdelight.util.BlockStateUtils;
import com.nhoryzon.mc.farmersdelight.util.MathUtils;
import com.nhoryzon.mc.farmersdelight.util.WorldEventUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.TallFlowerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Set;

public class RichSoilBlock extends Block {

    public RichSoilBlock() {
        super(FabricBlockSettings.copyOf(Blocks.DIRT).ticksRandomly().sounds(BlockSoundGroup.ROOTED_DIRT));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.getItem() instanceof HoeItem && state.getBlock() == BlocksRegistry.RICH_SOIL.get() &&
                hit.getSide() != Direction.DOWN && world.getBlockState(pos.up()).isAir()) {
            world.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.f, 1.f);
            if (!world.isClient()) {
                world.setBlockState(pos, BlocksRegistry.RICH_SOIL_FARMLAND.get().getDefaultState(), BlockStateUtils.DEFAULT_AND_RERENDER);
                itemStack.damage(1, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.isClient()) {
            BlockPos abovePos = pos.up();
            BlockState aboveState = world.getBlockState(abovePos);
            Block aboveBlock = aboveState.getBlock();

            // Do nothing if the plant is unaffected by rich soil
            if (aboveState.isIn(TagsRegistry.UNAFFECTED_BY_RICH_SOIL) || aboveBlock instanceof TallFlowerBlock) {
                return;
            }

            // Try to convert mushrooms to colonies if it's dark enough or if is a plant, then give it growth boost
            if (!tryConvertToColonies(world, pos, aboveBlock) && (aboveBlock instanceof Fertilizable growable
                    && FarmersDelightMod.CONFIG.getRichSoilBoostChance() > 0.0
                    && MathUtils.RAND.nextFloat() <= FarmersDelightMod.CONFIG.getRichSoilBoostChance()
                    && growable.isFertilizable(world, pos.up(), aboveState, false))) {
                growable.grow(world, world.getRandom(), pos.up(), aboveState);
                world.syncWorldEvent(WorldEventUtils.BONEMEAL_PARTICLES, pos.up(), 0);
            }
        }
    }

    private boolean tryConvertToColonies(World world, BlockPos pos, Block block) {
        if (!Set.of(Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM).contains(block)) {
            return false;
        }

        BlocksRegistry colonyBlock = (block == Blocks.BROWN_MUSHROOM) ? BlocksRegistry.BROWN_MUSHROOM_COLONY : BlocksRegistry.RED_MUSHROOM_COLONY;
        world.setBlockState(pos.up(), colonyBlock.get().getDefaultState());

        return true;
    }

}
