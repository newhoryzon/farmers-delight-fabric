package com.nhoryzon.mc.farmersdelight.event;

import com.nhoryzon.mc.farmersdelight.entity.block.CuttingBoardBlockEntity;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.TridentItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CuttingBoardEventListener implements UseBlockCallback {

    public static final CuttingBoardEventListener INSTANCE = new CuttingBoardEventListener();

    private CuttingBoardEventListener() {
        // Non-instantiable listener
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        ItemStack heldItem = player.getStackInHand(hand);
        if (player.isSneaking() && blockEntity instanceof CuttingBoardBlockEntity cuttingBoardBlockEntity && !heldItem.isEmpty()
                && (heldItem.getItem() instanceof ToolItem
                || heldItem.getItem() instanceof TridentItem
                || heldItem.getItem() instanceof ShearsItem)) {
            boolean success = cuttingBoardBlockEntity.carveToolOnBoard(player.getAbilities().creativeMode ? heldItem.copy() : heldItem);

            if (success) {
                world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.f,
                        .8f);

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

}
