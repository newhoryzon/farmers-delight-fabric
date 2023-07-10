package com.nhoryzon.mc.farmersdelight.entity.block.inventory;

import com.nhoryzon.mc.farmersdelight.exception.SlotInvalidRangeException;
import com.nhoryzon.mc.farmersdelight.util.CompoundTagUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

public interface ItemStackInventory extends Inventory {

    DefaultedList<ItemStack> getItems();

    static ItemStackInventory of() {
        return of(1);
    }

    static ItemStackInventory of(int inventorySize) {
        return () -> DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
    }

    static boolean canBeStacked(ItemStack left, ItemStack right) {
        if (left.isEmpty() || !ItemStack.areItemsEqual(left, right) || left.hasNbt() != right.hasNbt()) {
            return false;
        }

        return (!left.hasNbt() || left.getNbt().equals(right.getNbt()));
    }

    static ItemStack copyStackWithNewSize(ItemStack itemStack, int newSize) {
        if (newSize == 0) {
            return ItemStack.EMPTY;
        }

        ItemStack copy = itemStack.copy();
        copy.setCount(newSize);

        return copy;
    }

    @Override
    default int size() {
        return getItems().size();
    }

    @Override
    default boolean isEmpty() {
        return getItems().stream().anyMatch(stack -> !stack.isEmpty());
    }

    @Override
    default ItemStack getStack(int slot) {
        validateSlotIndex(slot);
        return getItems().get(slot);
    }

    @Override
    default ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(getItems(), slot, amount);
        if (!result.isEmpty()) {
            markDirty();
            onContentsChanged(slot);
        }

        return result;
    }

    @Override
    default ItemStack removeStack(int slot) {
        ItemStack result = Inventories.removeStack(getItems(), slot);
        if (!result.isEmpty()) {
            markDirty();
            onContentsChanged(slot);
        }

        return result;
    }

    @Override
    default void setStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > getMaxCountForSlot(slot)) {
            stack.setCount(stack.getMaxCount());
        }
        onContentsChanged(slot);
    }

    @Override
    default void clear() {
        getItems().clear();
    }

    @Override
    default void markDirty() {
        // Override not needed, by default dirty inventory did nothing
    }

    @Override
    default boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    default void onContentsChanged(int slot) {
        // Override not need, by default on content changed in slot did nothing
    }

    default void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= size())
            throw new SlotInvalidRangeException(slot, size());
    }

    default ItemStack insertStack(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || !isValid(slot, stack)) {
            return stack;
        }

        ItemStack invItemStack = getStack(slot);
        int limit = getStackLimit(slot, invItemStack);

        if (!invItemStack.isEmpty()) {
            if (!canBeStacked(stack, invItemStack)) {
                return stack;
            }

            limit -= invItemStack.getCount();
        }

        if (limit <= 0) {
            return stack;
        }

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (invItemStack.isEmpty()) {
                setStack(slot, reachedLimit ? copyStackWithNewSize(stack, limit) : stack);
            } else {
                invItemStack.increment(reachedLimit ? limit : stack.getCount());
            }

            onContentsChanged(slot);
            markDirty();
        }

        return reachedLimit ? copyStackWithNewSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    default int getMaxCountForSlot(int slot) {
        return 64;
    }

    default int getStackLimit(int slot, ItemStack stack) {
        return Math.min(getMaxCountForSlot(slot), stack.getMaxCount());
    }

    default void readInventoryNbt(NbtCompound tag) {
        Inventories.readNbt(tag.getCompound(CompoundTagUtils.TAG_KEY_INVENTORY), getItems());
    }

    default void writeInventoryNbt(NbtCompound tag) {
        tag.put(CompoundTagUtils.TAG_KEY_INVENTORY, Inventories.writeNbt(new NbtCompound(), getItems()));
    }

}
