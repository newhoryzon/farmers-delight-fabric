package com.nhoryzon.mc.farmersdelight.item.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class RecipeWrapper implements Inventory {

    protected final ItemHandler inventory;

    public RecipeWrapper(ItemHandler inventory) {
        this.inventory = inventory;
    }

    @Override
    public int size() {
        return inventory.size();
    }

    /**
     * Returns the stack in this slot.  This stack should be a modifiable reference, not a copy of a stack in your inventory.
     */
    @Override
    public ItemStack getStack(int slot) {
        return inventory.getStack(slot);
    }

    /**
     * Removes the stack contained in this slot from the underlying handler, and returns it.
     */
    @Override
    public ItemStack removeStack(int slot) {
        ItemStack itemStack = getStack(slot);
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        setStack(slot, ItemStack.EMPTY);

        return itemStack;
    }

    /**
     * Attempts to remove n items from the specified slot.  Returns the split stack that was removed.  Modifies the inventory.
     */
    @Override
    public ItemStack removeStack(int slot, int count) {
        ItemStack itemStack = inventory.getStack(slot);

        return itemStack.isEmpty() ? ItemStack.EMPTY : itemStack.split(count);
    }

    /**
     * Sets the contents of this slot to the provided stack.
     */
    @Override
    public void setStack(int slot, ItemStack itemStack) {
        inventory.setStack(slot, itemStack);
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < inventory.size(); i++) {
            if (!inventory.getStack(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public boolean isValid(int slot, ItemStack itemStack) {
        return inventory.isValid(slot, itemStack);
    }

    @Override
    public void clear() {
        for (int i = 0; i < inventory.size(); i++) {
            inventory.setStack(i, ItemStack.EMPTY);
        }
    }

    @Override
    public int getMaxCountPerStack() { return 0; }

    @Override
    public boolean canPlayerUse(PlayerEntity player) { return false; }

    @Override
    public void markDirty() {
        //The following methods are never used by vanilla in crafting.  They are defunct as mods need not override them.
    }

    @Override
    public void onOpen(PlayerEntity player) {
        //The following methods are never used by vanilla in crafting.  They are defunct as mods need not override them.
    }

    @Override
    public void onClose(PlayerEntity player) {
        //The following methods are never used by vanilla in crafting.  They are defunct as mods need not override them.
    }

}