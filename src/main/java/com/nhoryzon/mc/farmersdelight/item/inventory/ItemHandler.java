package com.nhoryzon.mc.farmersdelight.item.inventory;

import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;

public interface ItemHandler extends SidedInventory {
    int size();

    ItemStack insertItemStack(int slot, ItemStack itemStack, boolean simulate);

    ItemStack extractItemStack(int slot, int amount, boolean simulate);

    int getMaxCountForSlot(int slot);
}