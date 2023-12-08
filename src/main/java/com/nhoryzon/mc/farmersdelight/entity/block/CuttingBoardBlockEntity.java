package com.nhoryzon.mc.farmersdelight.entity.block;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.advancement.CuttingBoardTrigger;
import com.nhoryzon.mc.farmersdelight.block.CuttingBoardBlock;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.ItemStackInventory;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.RecipeWrapper;
import com.nhoryzon.mc.farmersdelight.mixin.accessors.RecipeManagerAccessorMixin;
import com.nhoryzon.mc.farmersdelight.recipe.CuttingBoardRecipe;
import com.nhoryzon.mc.farmersdelight.registry.AdvancementsRegistry;
import com.nhoryzon.mc.farmersdelight.registry.BlockEntityTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.RecipeTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.SoundsRegistry;
import com.nhoryzon.mc.farmersdelight.registry.TagsRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CuttingBoardBlockEntity extends SyncedBlockEntity implements ItemStackInventory {

    public static final String TAG_KEY_IS_ITEM_CARVED = "IsItemCarved";

    private boolean isItemCarvingBoard;
    private final DefaultedList<ItemStack> inventory;

    private Identifier lastRecipeID;

    public CuttingBoardBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityTypesRegistry.CUTTING_BOARD.get(), blockPos, blockState);
        this.isItemCarvingBoard = false;
        this.inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        isItemCarvingBoard = tag.getBoolean(TAG_KEY_IS_ITEM_CARVED);
        readInventoryNbt(tag);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        writeInventoryNbt(tag);
        tag.putBoolean(TAG_KEY_IS_ITEM_CARVED, isItemCarvingBoard);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt);

        return nbt;
    }

    /**
     * Attempts to apply a recipe to the Cutting Board's stored item, using the given tool.
     *
     * @param tool The item stack used to process the item
     * @param player player who trying to process item with the tool
     * @return Whether the process succeeded or failed.
     */
    public boolean processItemUsingTool(ItemStack tool, PlayerEntity player) {
        if (world == null) {
            return false;
        }

        Optional<RecipeEntry<CuttingBoardRecipe>> matchingRecipe = getMatchingRecipe(new RecipeWrapper(this), tool, player);

        matchingRecipe.ifPresent(recipe -> {
            List<ItemStack> results = recipe.value().getRolledResults(world.getRandom(), EnchantmentHelper.getLevel(Enchantments.FORTUNE, tool));
            for (ItemStack result : results) {
                Direction direction = getCachedState().get(CuttingBoardBlock.FACING).rotateYCounterclockwise();
                ItemEntity entity = new ItemEntity(world,
                        pos.getX() + .5 + (direction.getOffsetX() * .2),
                        pos.getY() + .2,
                        pos.getZ() + .5 + (direction.getOffsetZ() * .2), result.copy());
                entity.setVelocity(direction.getOffsetX() * .2f, .0f, direction.getOffsetZ() * .2f);
                world.spawnEntity(entity);
            }
            if (player != null) {
                tool.damage(1, player, user -> user.sendToolBreakStatus(Hand.MAIN_HAND));
            } else {
                if (tool.damage(1, world.getRandom(), null)) {
                    tool.setCount(0);
                }
            }
            playProcessingSound(recipe.value().getSoundEvent(), tool.getItem(), getStoredItem().getItem());
            removeItem();
            if (player instanceof ServerPlayerEntity serverPlayer) {
                ((CuttingBoardTrigger) AdvancementsRegistry.CUTTING_BOARD.get()).trigger(serverPlayer);
            }
        });

        return matchingRecipe.isPresent();
    }

    private Optional<RecipeEntry<CuttingBoardRecipe>> getMatchingRecipe(RecipeWrapper recipeWrapper, ItemStack toolStack, @Nullable PlayerEntity player) {
        if (world == null) {
            return Optional.empty();
        }

        if (lastRecipeID != null) {
            RecipeEntry<Recipe<Inventory>> recipe = ((RecipeManagerAccessorMixin) world.getRecipeManager())
                    .getAllForType(RecipeTypesRegistry.CUTTING_RECIPE_SERIALIZER.type())
                    .get(lastRecipeID);
            if (recipe.value() instanceof CuttingBoardRecipe && recipe.value().matches(recipeWrapper, world) && ((CuttingBoardRecipe) recipe.value()).getTool().test(toolStack)) {
                return Optional.of(new RecipeEntry<>(recipe.id(), (CuttingBoardRecipe)recipe.value()));
            }
        }

        List<RecipeEntry<CuttingBoardRecipe>> recipeList = world.getRecipeManager().getAllMatches(RecipeTypesRegistry.CUTTING_RECIPE_SERIALIZER.type(), recipeWrapper, world);
        if (recipeList.isEmpty()) {
            if (player != null) {
                player.sendMessage(FarmersDelightMod.i18n("block.cutting_board.invalid_item"), true);
            }
            return Optional.empty();
        }

        Optional<RecipeEntry<CuttingBoardRecipe>> recipe = recipeList.stream().filter(cuttingRecipe -> cuttingRecipe.value().getTool().test(toolStack)).findFirst();
        if (recipe.isEmpty()) {
            if (player != null) {
                player.sendMessage(FarmersDelightMod.i18n("block.cutting_board.invalid_tool"), true);
            }

            return Optional.empty();
        }
        lastRecipeID = recipe.get().id();

        return recipe;
    }

    public void playProcessingSound(Optional<SoundEvent> soundEventID, Item tool, Item boardItem) {
        if (soundEventID.isPresent()) {
            playSound(soundEventID.get(), 1.f, 1.f);
        } else if (tool instanceof ShearsItem) {
            playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.f, 1.f);
        } else if (tool.getDefaultStack().isIn(TagsRegistry.KNIVES)) {
            playSound(SoundsRegistry.BLOCK_CUTTING_BOARD_KNIFE.get(), .8f, 1.f);
        } else if (boardItem instanceof BlockItem boardBlockItem) {
            Block block = boardBlockItem.getBlock();
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

    public boolean isEmpty() {
        return getStack(0).isEmpty();
    }

    public ItemStack getStoredItem() {
        return getStack(0);
    }

    public boolean addItem(ItemStack itemStack) {
        if (isEmpty() && !itemStack.isEmpty()) {
            setStack(0, itemStack.split(1));
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

    @Override
    public void onContentsChanged(int slot) {
        inventoryChanged();
    }

    @Override
    public int getMaxCountForSlot(int slot) {
        return 1;
    }

}
