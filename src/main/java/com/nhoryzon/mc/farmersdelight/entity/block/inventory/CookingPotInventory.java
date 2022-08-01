package com.nhoryzon.mc.farmersdelight.entity.block.inventory;

import com.nhoryzon.mc.farmersdelight.entity.block.CookingPotBlockEntity;
import com.nhoryzon.mc.farmersdelight.util.CompoundTagUtils;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class CookingPotInventory extends SimpleInventory implements SidedInventory {

    private static final int SLOTS_INPUT = CookingPotBlockEntity.MEAL_DISPLAY_SLOT;
    private static final int SLOT_CONTAINER_INPUT = CookingPotBlockEntity.CONTAINER_SLOT;
    private static final int SLOT_MEAL_OUTPUT = CookingPotBlockEntity.OUTPUT_SLOT;

    public CookingPotInventory() {
        super(CookingPotBlockEntity.INVENTORY_SIZE);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return new int[]{SLOT_MEAL_OUTPUT};
        }

        if (side == Direction.UP) {
            return IntStream.range(0, SLOTS_INPUT).toArray();
        }

        return new int[]{SLOT_CONTAINER_INPUT};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (dir == null || dir.equals(Direction.UP)) {
            return slot < SLOTS_INPUT;
        } else {
            return slot == SLOT_CONTAINER_INPUT;
        }
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (dir == null || dir.equals(Direction.UP)) {
            return slot < SLOTS_INPUT;
        } else {
            return slot == SLOT_MEAL_OUTPUT;
        }
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList nbtList = new NbtList();
        for(int i = 0; i < this.size(); ++i) {
            nbtList.add(getStack(i).writeNbt(new NbtCompound()));
        }

        nbt.put("Items", nbtList);
        return nbt;
    }

    public void readNbt(NbtCompound tag) {
        clear();

        NbtList nbtList = tag.getList("Items", CompoundTagUtils.TAG_COMPOUND);
        for(int i = 0; i < size(); ++i) {
            addStack(ItemStack.fromNbt(nbtList.getCompound(i)));
        }
    }

}
