package com.nhoryzon.mc.farmersdelight.entity.block.inventory;

import com.nhoryzon.mc.farmersdelight.entity.block.CookingPotBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class CookingPotResultSlot extends Slot {

    public final CookingPotBlockEntity blockEntity;

    private final PlayerEntity player;
    private int removeCount;

    public CookingPotResultSlot(PlayerEntity player, CookingPotBlockEntity blockEntity, Inventory inventory, int index, int xPosition, int yPosition) {
        super(inventory, index, xPosition, yPosition);
        this.blockEntity = blockEntity;
        this.player = player;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack takeStack(int amount) {
        if (hasStack()) {
            removeCount += Math.min(amount, getStack().getCount());
        }
        return super.takeStack(amount);
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        onCrafted(stack);
        super.onTakeItem(player, stack);
    }

    @Override
    protected void onCrafted(ItemStack stack, int amount) {
        removeCount += amount;
        onCrafted(stack);
    }

    @Override
    protected void onCrafted(ItemStack stack) {
        stack.onCraft(player.world, player, removeCount);

        if (!player.world.isClient()) {
            blockEntity.clearUsedRecipes(player);
        }

        removeCount = 0;
    }

}