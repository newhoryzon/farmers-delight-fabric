package com.nhoryzon.mc.farmersdelight.event;

import com.nhoryzon.mc.farmersdelight.registry.ItemsRegistry;
import com.nhoryzon.mc.farmersdelight.tag.Tags;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KnivesEventListener implements PlayerBlockBreakEvents.After, UseBlockCallback {

    public static final KnivesEventListener INSTANCE = new KnivesEventListener();

    private KnivesEventListener() {
        // Non-instantiable listener
    }

    @Override
    public void afterBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        ItemStack heldItem = player.getMainHandStack();
        if (heldItem.isIn(Tags.KNIVES) && state.getBlock() instanceof CakeBlock) {
            ItemScatterer.spawn(world, pos,
                    DefaultedList.ofSize(1, new ItemStack(ItemsRegistry.CAKE_SLICE.get(), 7 - state.get(CakeBlock.BITES))));
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        ItemStack heldItem = player.getStackInHand(hand);

        if (state.getBlock() instanceof CakeBlock && heldItem.isIn(Tags.KNIVES)) {
            int bites = state.get(CakeBlock.BITES);
            if (bites < 6) {
                world.setBlockState(pos, state.with(CakeBlock.BITES, bites + 1), 3);
            } else {
                world.removeBlock(pos, false);
            }
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemsRegistry.CAKE_SLICE.get()));
            world.playSound(null, pos, SoundEvents.BLOCK_WOOL_BREAK, SoundCategory.PLAYERS, .8f, .8f);

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

}
