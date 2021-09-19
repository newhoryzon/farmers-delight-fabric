package com.nhoryzon.mc.farmersdelight.item.inventory;

import com.nhoryzon.mc.farmersdelight.exception.SlotInvalidRangeException;
import com.nhoryzon.mc.farmersdelight.util.CompoundTagUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class ItemStackHandler implements ItemHandler {
    protected DefaultedList<ItemStack> inventory;

    public ItemStackHandler() {
        this(1);
    }

    public ItemStackHandler(int inventorySize) {
        inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean canItemStacksStack(ItemStack left, ItemStack right) {
        if (left.isEmpty() || !left.isItemEqual(right) || left.hasNbt() != right.hasNbt()) {
            return false;
        }

        return (!left.hasNbt() || left.getNbt().equals(right.getNbt()));
    }

    public static ItemStack copyStackWithNewSize(ItemStack itemStack, int newSize) {
        if (newSize == 0) {
            return ItemStack.EMPTY;
        }

        ItemStack copy = itemStack.copy();
        copy.setCount(newSize);

        return copy;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[0];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        validateSlotIndex(slot);
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return extractItemStack(slot, amount, false);
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack = getStack(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return extractItemStack(slot, stack.getCount(), false);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        validateSlotIndex(slot);
        inventory.set(slot, stack);
        onInventorySlotChanged(slot);
    }

    @Override
    public void markDirty() {
        // Do nothing when the itemstack handler is marked as dirty
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    public ItemStack insertItemStack(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || !isValid(slot, stack)) {
            return stack;
        }

        validateSlotIndex(slot);

        ItemStack invItemStack = inventory.get(slot);
        int limit = getStackLimit(slot, invItemStack);

        if (!invItemStack.isEmpty()) {
            if (!canItemStacksStack(stack, invItemStack)) {
                return stack;
            }

            limit -= invItemStack.getCount();
        }

        if (limit <= 0) {
            return stack;
        }

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate && invItemStack.isEmpty()) {
            inventory.set(slot, reachedLimit ? copyStackWithNewSize(stack, limit) : stack);
        }

        return reachedLimit ? copyStackWithNewSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack extractItemStack(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        }

        validateSlotIndex(slot);

        ItemStack invItemStack = inventory.get(slot);

        if (invItemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int nbrToExtract = Math.min(amount, invItemStack.getMaxCount());

        if (invItemStack.getCount() <= nbrToExtract) {
            if (!simulate) {
                inventory.set(slot, ItemStack.EMPTY);
                onInventorySlotChanged(slot);

                return invItemStack;
            } else {
                return invItemStack.copy();
            }
        } else {
            if (!simulate) {
                inventory.set(slot, copyStackWithNewSize(invItemStack, invItemStack.getCount() - nbrToExtract));
                onInventorySlotChanged(slot);
            }

            return copyStackWithNewSize(invItemStack, nbrToExtract);
        }
    }

    @Override
    public int getMaxCountForSlot(int slot) {
        return 64;
    }

    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    protected void onInventoryLoaded() {
        // Do nothing on basic itemstack handler when inventory is loaded
    }

    protected void onInventorySlotChanged(int slot) {
        // Do nothing on basic itemstack handler when inventory slot is changed
    }

    public void setSize(int size) {
        inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= inventory.size())
            throw new SlotInvalidRangeException(slot, inventory.size());
    }

    protected int getStackLimit(int slot, ItemStack stack) {
        return Math.min(getMaxCountForSlot(slot), stack.getMaxCount());
    }

    public NbtCompound toTag() {
        NbtList itemListTag = new NbtList();
        for (int i = 0; i < inventory.size(); i++) {
            if (!inventory.get(i).isEmpty()) {
                NbtCompound itemTag = new NbtCompound();
                itemTag.putInt("Slot", i);
                inventory.get(i).writeNbt(itemTag);
                itemListTag.add(itemTag);
            }
        }

        NbtCompound inventoryTag = new NbtCompound();
        inventoryTag.put("Items", itemListTag);
        inventoryTag.putInt("Size", inventory.size());

        return inventoryTag;
    }

    public void fromTag(NbtCompound tag) {
        setSize(tag.contains("Size", CompoundTagUtils.TAG_INT) ? tag.getInt("Size") : inventory.size());
        NbtList itemListTag = tag.getList("Items", CompoundTagUtils.TAG_COMPOUND);
        for (int i = 0; i < itemListTag.size(); i++) {
            NbtCompound itemTag = itemListTag.getCompound(i);
            int slot = itemTag.getInt("Slot");
            if (slot >= 0 && slot <= inventory.size()) {
                inventory.set(slot, ItemStack.fromNbt(itemTag));
            }
        }

        onInventoryLoaded();
    }
}