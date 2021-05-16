package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import com.nhoryzon.mc.farmersdelight.tag.Tags;
import com.nhoryzon.mc.farmersdelight.util.BlockStateUtils;
import com.nhoryzon.mc.farmersdelight.util.MathUtils;
import com.nhoryzon.mc.farmersdelight.util.WorldEventUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.TallFlowerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Random;

public class RichSoilBlock extends Block {
    public static final int COLONY_FORMING_LIGHT_LEVEL = 12;

    public RichSoilBlock() {
        super(FabricBlockSettings.copyOf(Blocks.DIRT).breakByTool(FabricToolTags.SHOVELS).ticksRandomly());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.getItem().isIn(FabricToolTags.HOES) && state.getBlock() == BlocksRegistry.RICH_SOIL.get() &&
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
            if (Tags.UNAFFECTED_BY_RICH_SOIL.contains(aboveBlock) || aboveBlock instanceof TallFlowerBlock) {
                return;
            }

            // Convert mushrooms to colonies if it's dark enough
            if (aboveBlock == Blocks.BROWN_MUSHROOM) {
                if (world.getLightLevel(pos.up(), 0) <= COLONY_FORMING_LIGHT_LEVEL) {
                    world.setBlockState(pos.up(), BlocksRegistry.BROWN_MUSHROOM_COLONY.get().getDefaultState());
                }
                return;
            }
            if (aboveBlock == Blocks.RED_MUSHROOM) {
                if (world.getLightLevel(pos.up(), 0) <= COLONY_FORMING_LIGHT_LEVEL) {
                    world.setBlockState(pos.up(), BlocksRegistry.RED_MUSHROOM_COLONY.get().getDefaultState());
                }
                return;
            }

            // If all else fails, and it's a plant, give it a growth boost now and then!
            if (aboveBlock instanceof Fertilizable && MathUtils.RAND.nextFloat() <= .2f) {
                Fertilizable growable = (Fertilizable) aboveBlock;
                if (growable.isFertilizable(world, pos.up(), aboveState, false)) {
                    growable.grow(world, world.getRandom(), pos.up(), aboveState);
                    world.syncWorldEvent(WorldEventUtils.BONEMEAL_PARTICLES, pos.up(), 0);
                }
            }
        }
    }
}