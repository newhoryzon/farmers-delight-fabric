package com.nhoryzon.mc.farmersdelight.entity.block.inventory;

import com.nhoryzon.mc.farmersdelight.item.inventory.ItemHandler;
import com.nhoryzon.mc.farmersdelight.item.inventory.SlotItemHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class CookingPotMealSlot extends SlotItemHandler {
    
    public CookingPotMealSlot(ItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canTakeItems(PlayerEntity playerIn) {
        return false;
    }

}