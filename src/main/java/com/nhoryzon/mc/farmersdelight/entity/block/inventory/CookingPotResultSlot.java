package com.nhoryzon.mc.farmersdelight.entity.block.inventory;

import com.nhoryzon.mc.farmersdelight.item.inventory.ItemHandler;
import com.nhoryzon.mc.farmersdelight.item.inventory.SlotItemHandler;
import net.minecraft.item.ItemStack;

public class CookingPotResultSlot extends SlotItemHandler {
    public CookingPotResultSlot(ItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }
}