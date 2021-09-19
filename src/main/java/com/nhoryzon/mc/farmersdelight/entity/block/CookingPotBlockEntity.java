package com.nhoryzon.mc.farmersdelight.entity.block;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.block.CookingPotBlock;
import com.nhoryzon.mc.farmersdelight.entity.block.screen.CookingPotScreenHandler;
import com.nhoryzon.mc.farmersdelight.item.inventory.ItemStackHandler;
import com.nhoryzon.mc.farmersdelight.item.inventory.RecipeWrapper;
import com.nhoryzon.mc.farmersdelight.recipe.CookingPotRecipe;
import com.nhoryzon.mc.farmersdelight.registry.BlockEntityTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.RecipeTypesRegistry;
import com.nhoryzon.mc.farmersdelight.tag.Tags;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

public class CookingPotBlockEntity extends BlockEntity implements BlockEntityClientSerializable, ExtendedScreenHandlerFactory, Tickable, Nameable {
    public static final int MEAL_DISPLAY_SLOT = 6;
    public static final int CONTAINER_SLOT = 7;
    public static final int OUTPUT_SLOT = 8;
    public static final int INVENTORY_SIZE = 9;

    private final ItemStackHandler itemHandler = new ItemStackHandler(INVENTORY_SIZE) {

        @Override
        public int[] getAvailableSlots(Direction side) {
            if (side == Direction.DOWN) {
                return new int[]{OUTPUT_SLOT};
            }

            if (side == Direction.UP) {
                return IntStream.range(0, MEAL_DISPLAY_SLOT).toArray();
            }

            return new int[]{CONTAINER_SLOT};
        }

        @Override
        public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
            if (dir == null || dir.equals(Direction.UP)) {
                return slot < MEAL_DISPLAY_SLOT;
            } else {
                return slot == CONTAINER_SLOT;
            }
        }

        @Override
        public boolean canExtract(int slot, ItemStack stack, Direction dir) {
            if (dir == null || dir.equals(Direction.UP)) {
                return slot < MEAL_DISPLAY_SLOT;
            } else {
                return slot == OUTPUT_SLOT;
            }
        }

        @Override
        protected void onInventoryLoaded() {

        }

        @Override
        protected void onInventorySlotChanged(int slot) {
            if (slot >= 0 && slot < MEAL_DISPLAY_SLOT) {
                cookTimeTotal = getCookTime();
                inventoryChanged();
            }
        }
    };

    private Text customName;

    private int cookTime;
    private int cookTimeTotal;
    private ItemStack container;
    protected final PropertyDelegate cookingPotData = new CookingPotSyncedData();
    protected final RecipeType<? extends CookingPotRecipe> recipeType;

    public CookingPotBlockEntity(BlockEntityType<?> blockEntityType, RecipeType<? extends CookingPotRecipe> recipeType) {
        super(blockEntityType);
        this.recipeType = recipeType;
        this.container = ItemStack.EMPTY;
    }

    public CookingPotBlockEntity() {
        this(BlockEntityTypesRegistry.COOKING_POT.get(), RecipeTypesRegistry.COOKING_RECIPE_SERIALIZER.type());
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        return writeItems(tag);
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        fromTag(getCachedState(), tag);
    }

    @Override
    public void fromTag(BlockState state, NbtCompound tag) {
        super.fromTag(state, tag);
        fromTag(tag);
    }

    private void fromTag(NbtCompound tag) {
        itemHandler.fromTag(tag.getCompound("Inventory"));
        cookTime = tag.getInt("CookTime");
        cookTimeTotal = tag.getInt("CookTimeTotal");
        container = ItemStack.fromNbt(tag.getCompound("Container"));
        if (tag.contains("CustomName", 8)) {
            customName = Text.Serializer.fromJson(tag.getString("CustomName"));
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt("CookTime", cookTime);
        tag.putInt("CookTimeTotal", cookTimeTotal);
        tag.put("Container", container.writeNbt(new NbtCompound()));
        if (customName != null) {
            tag.putString("CustomName", Text.Serializer.toJson(customName));
        }
        tag.put("Inventory", itemHandler.toTag());

        return tag;
    }

    public NbtCompound writeItems(NbtCompound tag) {
        super.writeNbt(tag);
        tag.put("Container", container.writeNbt(new NbtCompound()));
        tag.put("Inventory", itemHandler.toTag());

        return tag;
    }

    public NbtCompound writeMeal(NbtCompound tag) {
        if (getMeal().isEmpty()) {
            return tag;
        }

        ItemStackHandler drops = new ItemStackHandler(INVENTORY_SIZE) {
            @Override
            protected void onInventoryLoaded() {
            }

            @Override
            protected void onInventorySlotChanged(int slot) {
            }
        };
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            drops.setStack(i, i == MEAL_DISPLAY_SLOT ? itemHandler.getStack(i) : ItemStack.EMPTY);
        }
        if (customName != null) {
            tag.putString("CustomName", Text.Serializer.toJson(customName));
        }
        tag.put("Container", container.writeNbt(new NbtCompound()));
        tag.put("Inventory", drops.toTag());

        return tag;
    }

    @Override
    public Text getName() {
        return customName != null ? customName : FarmersDelightMod.i18n("container.cooking_pot");
    }

    @Override
    public Text getDisplayName() {
        return getName();
    }

    public void setCustomName(Text customName) {
        this.customName = customName;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new CookingPotScreenHandler(syncId, inv, this, cookingPotData);
    }

    @Override
    public void tick() {
        boolean isHeated = isAboveLitHeatSource();
        boolean dirty = false;

        if (!Objects.requireNonNull(world).isClient()) {
            if (isHeated && hasInput()) {
                CookingPotRecipe recipe = world.getRecipeManager().getFirstMatch(recipeType, new RecipeWrapper(itemHandler), world).orElse(
                        null);
                if (canCook(recipe)) {
                    ++cookTime;
                    if (cookTime == cookTimeTotal) {
                        cookTime = 0;
                        cookTimeTotal = getCookTime();
                        cook(recipe);
                        dirty = true;
                    }
                } else {
                    cookTime = 0;
                }
            } else if (cookTime > 0) {
                cookTime = MathHelper.clamp(cookTime - 2, 0, cookTimeTotal);
            }

            ItemStack meal = getMeal();
            if (!meal.isEmpty()) {
                if (!doesMealHaveContainer(meal)) {
                    moveMealToOutput();
                    dirty = true;
                } else if (!itemHandler.getStack(CONTAINER_SLOT).isEmpty()) {
                    useStoredContainersOnMeal();
                    dirty = true;
                }
            }

        } else {
            if (isHeated) {
                animate();
            }
        }

        if (dirty) {
            inventoryChanged();
        }
    }

    @Nullable
    @Override
    public Text getCustomName() {
        return customName;
    }

    protected int getCookTime() {
        return Objects.requireNonNull(world).getRecipeManager().getFirstMatch(recipeType, new RecipeWrapper(itemHandler), world).map(
                CookingPotRecipe::getCookTime).orElse(200);
    }

    protected ItemStack getRecipeContainer() {
        return Objects.requireNonNull(world).getRecipeManager().getFirstMatch(recipeType, new RecipeWrapper(itemHandler), world).map(
                CookingPotRecipe::getContainer).orElse(ItemStack.EMPTY);
    }

    public ItemStack getContainer() {
        if (!container.isEmpty()) {
            return container;
        } else {
            return new ItemStack(getMeal().getItem().getRecipeRemainder());
        }
    }

    private boolean hasInput() {
        for (int i = 0; i < MEAL_DISPLAY_SLOT; ++i) {
            if (!itemHandler.getStack(i).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    protected boolean canCook(Recipe<?> recipeIn) {
        if (hasInput() && recipeIn != null) {
            ItemStack recipeOutput = recipeIn.getOutput();
            if (recipeOutput.isEmpty()) {
                return false;
            } else {
                ItemStack currentOutput = itemHandler.getStack(MEAL_DISPLAY_SLOT);
                if (currentOutput.isEmpty()) {
                    return true;
                } else if (!currentOutput.isItemEqual(recipeOutput)) {
                    return false;
                } else if (currentOutput.getCount() + recipeOutput.getCount() <= itemHandler.getMaxCountForSlot(MEAL_DISPLAY_SLOT)) {
                    return true;
                } else {
                    return currentOutput.getCount() + recipeOutput.getCount() <= recipeOutput.getMaxCount();
                }
            }
        } else {
            return false;
        }
    }

    private void cook(Recipe<?> recipe) {
        if (recipe != null && canCook(recipe)) {
            container = getRecipeContainer();
            ItemStack recipeOutput = recipe.getOutput();
            ItemStack currentOutput = itemHandler.getStack(MEAL_DISPLAY_SLOT);
            if (currentOutput.isEmpty()) {
                itemHandler.setStack(MEAL_DISPLAY_SLOT, recipeOutput.copy());
            } else if (currentOutput.getItem() == recipeOutput.getItem()) {
                currentOutput.increment(recipeOutput.getCount());
            }
        }
        for (int i = 0; i < MEAL_DISPLAY_SLOT; ++i) {
            ItemStack itemStack = itemHandler.getStack(i);
            if (itemStack.getItem().hasRecipeRemainder()) {
                Direction direction = getCachedState().get(CookingPotBlock.FACING).rotateYCounterclockwise();
                double dropX = pos.getX() + .5d + (direction.getOffsetX() * .25d);
                double dropY = pos.getY() + .7d;
                double dropZ = pos.getZ() + .5d + (direction.getOffsetZ() * .25d);
                ItemEntity entity = new ItemEntity(world, dropX, dropY, dropZ, new ItemStack(itemHandler.getStack(i).getItem()
                        .getRecipeRemainder()));
                entity.setVelocity(direction.getOffsetX() * .08f, .25f, direction.getOffsetZ() * .08f);
                Objects.requireNonNull(world).spawnEntity(entity);
            }

            if (!itemHandler.getStack(i).isEmpty()) {
                itemHandler.getStack(i).decrement(1);
            }
        }
    }

    private void animate() {
        World world = getWorld();
        if (world != null) {
            BlockPos blockpos = getPos();
            Random random = world.random;
            if (random.nextFloat() < .2f) {
                double baseX = (double) blockpos.getX() + .5d + (random.nextDouble() * .6d - .3d);
                double baseY = (double) blockpos.getY() + .7d;
                double baseZ = (double) blockpos.getZ() + .5d + (random.nextDouble() * .6d - .3d);
                world.addParticle(ParticleTypes.BUBBLE_POP, baseX, baseY, baseZ, .0d, .0d, .0d);
            }
            if (random.nextFloat() < .05f) {
                double baseX = (double) blockpos.getX() + .5d + (random.nextDouble() * .4d - .2d);
                double baseY = (double) blockpos.getY() + .7d;
                double baseZ = (double) blockpos.getZ() + .5d + (random.nextDouble() * .4d - .2d);
                world.addParticle(ParticleTypes.EFFECT, baseX, baseY, baseZ, .0d, .0d, .0d);
            }
        }
    }

    public ItemStack getMeal() {
        return itemHandler.getStack(MEAL_DISPLAY_SLOT);
    }

    public boolean isAboveLitHeatSource() {
        if (world == null) {
            return false;
        }
        BlockState checkState = world.getBlockState(pos.down());
        if (Tags.HEAT_SOURCES.contains(checkState.getBlock())) {
            if (checkState.contains(Properties.LIT)) {
                return checkState.get(Properties.LIT);
            }

            return true;
        }

        return false;
    }

    /**
     * Returns every stored ItemStack in the pot, except for prepared meals.
     *
     * @return a list of item stack.
     */
    public DefaultedList<ItemStack> getDroppableInventory() {
        DefaultedList<ItemStack> drops = DefaultedList.of();
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            drops.add(i == MEAL_DISPLAY_SLOT ? ItemStack.EMPTY : itemHandler.getStack(i));
        }

        return drops;
    }

    /**
     * Attempts to move all stored meals to the final output. Does NOT check if the meal has a container; this is done on tick.
     */
    private void moveMealToOutput() {
        ItemStack mealDisplay = itemHandler.getStack(MEAL_DISPLAY_SLOT);
        ItemStack finalOutput = itemHandler.getStack(OUTPUT_SLOT);
        int mealCount = Math.min(mealDisplay.getCount(), mealDisplay.getMaxCount() - finalOutput.getCount());
        if (finalOutput.isEmpty()) {
            itemHandler.setStack(OUTPUT_SLOT, mealDisplay.split(mealCount));
        } else if (finalOutput.getItem() == mealDisplay.getItem()) {
            mealDisplay.decrement(mealCount);
            finalOutput.increment(mealCount);
        }
    }

    /**
     * Attempts to generate an ItemStack output using the meal and the inputted container together. If input and meal containers don't
     * match, nothing happens.
     */
    private void useStoredContainersOnMeal() {
        ItemStack mealDisplay = itemHandler.getStack(MEAL_DISPLAY_SLOT);
        ItemStack containerInput = itemHandler.getStack(CONTAINER_SLOT);
        ItemStack finalOutput = itemHandler.getStack(OUTPUT_SLOT);

        if (isContainerValid(containerInput) && finalOutput.getCount() < finalOutput.getMaxCount()) {
            int smallerStack = Math.min(mealDisplay.getCount(), containerInput.getCount());
            int mealCount = Math.min(smallerStack, mealDisplay.getMaxCount() - finalOutput.getCount());
            if (finalOutput.isEmpty()) {
                containerInput.decrement(mealCount);
                itemHandler.setStack(OUTPUT_SLOT, mealDisplay.split(mealCount));
            } else if (finalOutput.getItem() == mealDisplay.getItem()) {
                mealDisplay.decrement(mealCount);
                containerInput.decrement(mealCount);
                finalOutput.increment(mealCount);
            }
        }
    }

    /**
     * Checks if the given ItemStack is a container for the stored meal. If true, takes a serving and returns it.
     *
     * @param container item stack held by the player
     * @return the item stack (with 1 count) of stored meal.
     */
    public ItemStack useHeldItemOnMeal(ItemStack container) {
        if (isContainerValid(container) && !getMeal().isEmpty()) {
            container.decrement(1);
            return getMeal().split(1);
        }
        return ItemStack.EMPTY;
    }

    private boolean doesMealHaveContainer(ItemStack meal) {
        return !container.isEmpty() || meal.getItem().hasRecipeRemainder();
    }

    public boolean isContainerValid(ItemStack containerItem) {
        if (containerItem.isEmpty()) {
            return false;
        }
        if (!container.isEmpty()) {
            return container.isItemEqual(containerItem);
        } else {
            return new ItemStack(getMeal().getItem().getRecipeRemainder()).isItemEqual(containerItem);
        }
    }

    public ItemStackHandler getInventory() {
        return itemHandler;
    }

    private void inventoryChanged() {
        markDirty();
        Objects.requireNonNull(world).updateListeners(getPos(), getCachedState(), getCachedState(), 3);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(getPos());
    }

    private class CookingPotSyncedData implements PropertyDelegate {

        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return CookingPotBlockEntity.this.cookTime;
                case 1:
                    return CookingPotBlockEntity.this.cookTimeTotal;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                CookingPotBlockEntity.this.cookTime = value;
            } else if (index == 1) {
                CookingPotBlockEntity.this.cookTimeTotal = value;
            }
        }

        @Override
        public int size() {
            return 2;
        }

    }
}