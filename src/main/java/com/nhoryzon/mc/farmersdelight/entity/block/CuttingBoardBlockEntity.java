package com.nhoryzon.mc.farmersdelight.entity.block;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.advancement.CuttingBoardTrigger;
import com.nhoryzon.mc.farmersdelight.block.CuttingBoardBlock;
import com.nhoryzon.mc.farmersdelight.item.inventory.ItemHandler;
import com.nhoryzon.mc.farmersdelight.item.inventory.ItemStackHandler;
import com.nhoryzon.mc.farmersdelight.item.inventory.RecipeWrapper;
import com.nhoryzon.mc.farmersdelight.recipe.CuttingBoardRecipe;
import com.nhoryzon.mc.farmersdelight.registry.AdvancementsRegistry;
import com.nhoryzon.mc.farmersdelight.registry.BlockEntityTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.RecipeTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.SoundsRegistry;
import com.nhoryzon.mc.farmersdelight.tag.Tags;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Objects;

public class CuttingBoardBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    private boolean isItemCarvingBoard;
    private final ItemStackHandler itemHandler = new ItemStackHandler() {
        @Override
        public int getMaxCountForSlot(int slot) {
            return 1;
        }

        @Override
        protected void onInventorySlotChanged(int slot) {
            inventoryChanged();
        }
    };
    protected final RecipeType<? extends CuttingBoardRecipe> recipeType;

    protected CuttingBoardBlockEntity(BlockEntityType<?> blockEntityType, RecipeType<? extends CuttingBoardRecipe> recipeType) {
        super(blockEntityType);
        this.recipeType = recipeType;
        this.isItemCarvingBoard = false;
    }

    public CuttingBoardBlockEntity() {
        this(BlockEntityTypesRegistry.CUTTING_BOARD.get(), RecipeTypesRegistry.CUTTING_RECIPE_SERIALIZER.type());
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        return writeNbt(tag);
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        fromTag(getCachedState(), tag);
    }

    @Override
    public void fromTag(BlockState state, NbtCompound tag) {
        super.fromTag(state, tag);
        isItemCarvingBoard = tag.getBoolean("IsItemCarved");
        itemHandler.fromTag(tag.getCompound("Inventory"));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.put("Inventory", itemHandler.toTag());
        tag.putBoolean("IsItemCarved", isItemCarvingBoard);

        return tag;
    }

    /**
     * Attempts to apply a recipe to the Cutting Board's stored item, using the given tool.
     *
     * @param tool The item stack used to process the item
     * @param player player who trying to process item with the tool
     * @return Whether the process succeeded or failed.
     */
    public boolean processItemUsingTool(ItemStack tool, PlayerEntity player) {
        List<? extends CuttingBoardRecipe> recipeList = Objects.requireNonNull(world).getRecipeManager()
                .getAllMatches(recipeType, new RecipeWrapper(itemHandler), world);
        CuttingBoardRecipe recipe = recipeList.stream().filter(cuttingRecipe -> cuttingRecipe.getTool().test(tool))
                .findAny().orElse(null);

        if (player != null) {
            if (recipeList.isEmpty()) {
                player.sendMessage(FarmersDelightMod.i18n("block.cutting_board.invalid_item"), true);
            } else if (recipe == null) {
                player.sendMessage(FarmersDelightMod.i18n("block.cutting_board.invalid_tool"), true);
            }
        }

        if (recipe != null) {
            DefaultedList<ItemStack> results = recipe.getResultList();
            for (ItemStack result : results) {
                Direction direction = getCachedState().get(CuttingBoardBlock.FACING).rotateYCounterclockwise();
                ItemEntity entity = new ItemEntity(world, pos.getX() + .5 + (direction.getOffsetX() * .2), pos.getY() + .2, pos.getZ() + .5 + (direction.getOffsetZ() * .2), result.copy());
                entity.setVelocity(direction.getOffsetX() * .2f, .0f, direction.getOffsetZ() * .2f);
                world.spawnEntity(entity);
            }
            if (player != null) {
                tool.damage(1, player, (user) -> user.sendToolBreakStatus(Hand.MAIN_HAND));
            } else {
                if (tool.damage(1, world.getRandom(), null)) {
                    tool.setCount(0);
                }
            }
            playProcessingSound(recipe.getSoundEvent(), tool.getItem(), getStoredItem().getItem());
            removeItem();
            inventoryChanged();
            if (player instanceof ServerPlayerEntity) {
                ((CuttingBoardTrigger) AdvancementsRegistry.CUTTING_BOARD.get()).trigger((ServerPlayerEntity) player);
            }
            return true;
        }

        return false;
    }

    public void playProcessingSound(String soundEventID, Item tool, Item boardItem) {
        SoundEvent sound = Registry.SOUND_EVENT.get(new Identifier(soundEventID));

        if (sound != null) {
            playSound(sound, 1.f, 1.f);
        } else if (tool.isIn(FabricToolTags.SHEARS)) {
            playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.f, 1.f);
        } else if (tool.isIn(Tags.KNIVES)) {
            playSound(SoundsRegistry.BLOCK_CUTTING_BOARD_KNIFE.get(), .8f, 1.f);
        } else if (boardItem instanceof BlockItem) {
            Block block = ((BlockItem) boardItem).getBlock();
            BlockSoundGroup soundType = block.getDefaultState().getSoundGroup();
            playSound(soundType.getBreakSound(), 1.f, .8f);
        } else {
            playSound(SoundEvents.BLOCK_WOOD_BREAK, 1.f, .8f);
        }
    }

    public void playSound(SoundEvent sound, float volume, float pitch) {
        Objects.requireNonNull(world).playSound(null, pos.getX() + .5f, pos.getY() + .5f, pos.getZ() + .5f, sound, SoundCategory.BLOCKS, volume, pitch);
    }

    /**
     * Places the given stack on the board, but carved into it instead of laying on top.
     * This is purely for decoration purposes; the item can still be processed.
     * Ideally, the caller checks if the item is a damageable tool first.
     *
     * @param tool the tool used to try carving item placed on the board
     * @return true if the tool in parameter can carve item placed on the board, false otherwise.
     */
    public boolean carveToolOnBoard(ItemStack tool) {
        if (addItem(tool)) {
            isItemCarvingBoard = true;

            return true;
        }

        return false;
    }

    public boolean getIsItemCarvingBoard() {
        return isItemCarvingBoard;
    }

    public ItemHandler getInventory() {
        return itemHandler;
    }

    public boolean isEmpty() {
        return itemHandler.getStack(0).isEmpty();
    }

    public ItemStack getStoredItem() {
        return itemHandler.getStack(0);
    }

    public boolean addItem(ItemStack itemStack) {
        if (isEmpty() && !itemStack.isEmpty()) {
            itemHandler.setStack(0, itemStack.split(1));
            isItemCarvingBoard = false;
            inventoryChanged();

            return true;
        }

        return false;
    }

    public ItemStack removeItem() {
        if (!isEmpty()) {
            isItemCarvingBoard = false;
            ItemStack item = getStoredItem().split(1);
            inventoryChanged();

            return item;
        }

        return ItemStack.EMPTY;
    }

    private void inventoryChanged() {
        markDirty();
        Objects.requireNonNull(world).updateListeners(getPos(), getCachedState(), getCachedState(), 3);
    }
}