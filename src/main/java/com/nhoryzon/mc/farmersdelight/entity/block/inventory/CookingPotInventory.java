package com.nhoryzon.mc.farmersdelight.entity.block.inventory;

import com.nhoryzon.mc.farmersdelight.entity.block.CookingPotBlockEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public interface CookingPotInventory extends ItemStackInventory, SidedInventory {

    @Override
    default int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return new int[]{CookingPotBlockEntity.OUTPUT_SLOT};
        }

        if (side == Direction.UP) {
            return IntStream.range(0, CookingPotBlockEntity.MEAL_DISPLAY_SLOT).toArray();
        }

        return new int[]{CookingPotBlockEntity.CONTAINER_SLOT};
    }

    @Override
    default boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (dir == null || dir.equals(Direction.UP)) {
            return slot < CookingPotBlockEntity.MEAL_DISPLAY_SLOT;
        } else {
            return slot == CookingPotBlockEntity.CONTAINER_SLOT;
        }
    }

    @Override
    default boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (dir == null || dir.equals(Direction.UP)) {
            return slot < CookingPotBlockEntity.MEAL_DISPLAY_SLOT;
        } else {
            return slot == CookingPotBlockEntity.OUTPUT_SLOT;
        }
    }

}
