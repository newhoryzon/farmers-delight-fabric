package com.nhoryzon.mc.farmersdelight.entity.block.inventory;

import com.nhoryzon.mc.farmersdelight.entity.block.CookingPotBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class CookingPotInventory extends ItemStackHandler {

    private final CookingPotBlockEntity cookingPotBlockEntity;

    public CookingPotInventory(CookingPotBlockEntity cookingPotBlockEntity) {
        super(CookingPotBlockEntity.INVENTORY_SIZE);
        this.cookingPotBlockEntity = cookingPotBlockEntity;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return new int[]{CookingPotBlockEntity.OUTPUT_SLOT};
        }

        if (side == Direction.UP) {
            return IntStream.range(0, CookingPotBlockEntity.MEAL_DISPLAY_SLOT).toArray();
        }

        return new int[]{CookingPotBlockEntity.CONTAINER_SLOT};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (dir == null || dir.equals(Direction.UP)) {
            return slot < CookingPotBlockEntity.MEAL_DISPLAY_SLOT;
        } else {
            return slot == CookingPotBlockEntity.CONTAINER_SLOT;
        }
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (dir == null || dir.equals(Direction.UP)) {
            return slot < CookingPotBlockEntity.MEAL_DISPLAY_SLOT;
        } else {
            return slot == CookingPotBlockEntity.OUTPUT_SLOT;
        }
    }

    @Override
    protected void onInventorySlotChanged(int slot) {
        if (slot >= 0 && slot < CookingPotBlockEntity.MEAL_DISPLAY_SLOT) {
            cookingPotBlockEntity.setCheckNewRecipe(true);
        }
        cookingPotBlockEntity.inventoryChanged();
    }

}
