package com.nhoryzon.mc.farmersdelight.item.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class SlotItemHandler extends Slot {
    private static final Inventory EMPTY_INVENTORY = new SimpleInventory(0);
    private final ItemHandler itemHandler;
    private final int index;

    public SlotItemHandler(ItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(EMPTY_INVENTORY, index, xPosition, yPosition);
        this.itemHandler = itemHandler;
        this.index = index;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        return itemHandler.isValid(index, stack);
    }

    @Override
    public ItemStack getStack() {
        return getItemHandler().getStack(index);
    }

    @Override
    public void setStack(ItemStack stack) {
        getItemHandler().setStack(index, stack);
        markDirty();
    }

    @Override
    public int getMaxItemCount() {
        return itemHandler.getMaxCountForSlot(this.index);
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        ItemStack maxAdd = stack.copy();
        int maxInput = stack.getMaxCount();
        maxAdd.setCount(maxInput);

        ItemHandler handler = this.getItemHandler();
        ItemStack currentStack = handler.getStack(index);

        handler.setStack(index, ItemStack.EMPTY);
        ItemStack remainder = handler.insertItemStack(index, maxAdd, true);
        handler.setStack(index, currentStack);

        return maxInput - remainder.getCount();
    }

    @Override
    public boolean canTakeItems(PlayerEntity playerIn) {
        return !getItemHandler().extractItemStack(index, 1, true).isEmpty();
    }

    @Override
    public ItemStack takeStack(int amount) {
        return this.getItemHandler().extractItemStack(index, amount, false);
    }

    public ItemHandler getItemHandler() {
        return itemHandler;
    }
}