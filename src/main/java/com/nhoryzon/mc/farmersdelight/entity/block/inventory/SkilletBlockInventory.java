package com.nhoryzon.mc.farmersdelight.entity.block.inventory;

import com.nhoryzon.mc.farmersdelight.entity.block.CookingPotBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.SkilletBlockEntity;

public class SkilletBlockInventory extends ItemStackHandler {

    private final SkilletBlockEntity skilletBlockEntity;

    public SkilletBlockInventory(SkilletBlockEntity skilletBlockEntity) {
        this.skilletBlockEntity = skilletBlockEntity;
    }

    @Override
    protected void onInventorySlotChanged(int slot) {
        if (slot >= 0 && slot < CookingPotBlockEntity.MEAL_DISPLAY_SLOT) {
            skilletBlockEntity.inventoryChanged();
        }
    }

}
