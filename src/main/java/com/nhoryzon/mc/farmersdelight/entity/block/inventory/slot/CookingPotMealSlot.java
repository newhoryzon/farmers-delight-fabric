package com.nhoryzon.mc.farmersdelight.entity.block.inventory.slot;

import com.nhoryzon.mc.farmersdelight.entity.block.inventory.ItemHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class CookingPotMealSlot extends SlotItemHandler {
    
    public CookingPotMealSlot(ItemHandler inventory, int index, int xPosition, int yPosition) {
        super(inventory, index, xPosition, yPosition);
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