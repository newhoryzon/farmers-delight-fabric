package com.nhoryzon.mc.farmersdelight.entity.block;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.block.CookingPotBlock;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.CookingPotInventory;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.ItemHandler;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.ItemStackHandler;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.RecipeWrapper;
import com.nhoryzon.mc.farmersdelight.entity.block.screen.CookingPotScreenHandler;
import com.nhoryzon.mc.farmersdelight.mixin.accessors.RecipeManagerAccessorMixin;
import com.nhoryzon.mc.farmersdelight.recipe.CookingPotRecipe;
import com.nhoryzon.mc.farmersdelight.registry.BlockEntityTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.ParticleTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.RecipeTypesRegistry;
import com.nhoryzon.mc.farmersdelight.util.CompoundTagUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CookingPotBlockEntity extends SyncedBlockEntity implements HeatableBlockEntity, ExtendedScreenHandlerFactory, Nameable {

    public static final String TAG_KEY_COOK_RECIPES_USED = "RecipesUsed";

    public static final int MEAL_DISPLAY_SLOT = 6;
    public static final int CONTAINER_SLOT = 7;
    public static final int OUTPUT_SLOT = 8;
    public static final int INVENTORY_SIZE = OUTPUT_SLOT + 1;

    private final CookingPotInventory inventory;
    private Text customName;

    private int cookTime;
    private int cookTimeTotal;
    private ItemStack mealContainer;
    protected final PropertyDelegate cookingPotData;
    private final Object2IntOpenHashMap<Identifier> experienceTracker;

    private Identifier lastRecipeID;
    private boolean checkNewRecipe;

    public CookingPotBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityTypesRegistry.COOKING_POT.get(), blockPos, blockState);
        inventory = new CookingPotInventory(this);
        mealContainer = ItemStack.EMPTY;
        cookingPotData = new CookingPotSyncedData();
        experienceTracker = new Object2IntOpenHashMap<>();
        checkNewRecipe = true;
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt);

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        inventory.readNbt(tag.getCompound(CompoundTagUtils.TAG_KEY_INVENTORY));
        cookTime = tag.getInt(CompoundTagUtils.TAG_KEY_COOK_TIME);
        cookTimeTotal = tag.getInt(CompoundTagUtils.TAG_KEY_COOK_TIME_TOTAL);
        mealContainer = ItemStack.fromNbt(tag.getCompound(CompoundTagUtils.TAG_KEY_CONTAINER));
        if (tag.contains(CompoundTagUtils.TAG_KEY_CUSTOM_NAME, 8)) {
            customName = Text.Serializer.fromJson(tag.getString(CompoundTagUtils.TAG_KEY_CUSTOM_NAME));
        }
        NbtCompound compoundRecipes = tag.getCompound(TAG_KEY_COOK_RECIPES_USED);
        for (String key : compoundRecipes.getKeys()) {
            experienceTracker.put(new Identifier(key), compoundRecipes.getInt(key));
        }
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt(CompoundTagUtils.TAG_KEY_COOK_TIME, cookTime);
        tag.putInt(CompoundTagUtils.TAG_KEY_COOK_TIME_TOTAL, cookTimeTotal);

        if (customName != null) {
            tag.putString(CompoundTagUtils.TAG_KEY_CUSTOM_NAME, Text.Serializer.toJson(customName));
        }
        tag.put(CompoundTagUtils.TAG_KEY_CONTAINER, mealContainer.writeNbt(new NbtCompound()));
        tag.put(CompoundTagUtils.TAG_KEY_INVENTORY, inventory.writeNbt(new NbtCompound()));

        NbtCompound compoundRecipes = new NbtCompound();
        experienceTracker.forEach((identifier, craftedAmount) -> compoundRecipes.putInt(identifier.toString(), craftedAmount));
        tag.put(TAG_KEY_COOK_RECIPES_USED, compoundRecipes);
    }

    public NbtCompound writeMeal(NbtCompound tag) {
        if (getMeal().isEmpty()) {
            return tag;
        }

        if (customName != null) {
            tag.putString(CompoundTagUtils.TAG_KEY_CUSTOM_NAME, Text.Serializer.toJson(customName));
        }
        tag.put(CompoundTagUtils.TAG_KEY_CONTAINER, mealContainer.writeNbt(new NbtCompound()));

        ItemStackHandler drops = new ItemStackHandler(INVENTORY_SIZE);
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            drops.setStack(i, i == MEAL_DISPLAY_SLOT ? inventory.getStack(i) : ItemStack.EMPTY);
        }
        tag.put(CompoundTagUtils.TAG_KEY_INVENTORY, drops.writeNbt(new NbtCompound()));

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

    public static void cookingTick(World world, BlockPos pos, BlockState state, CookingPotBlockEntity cookingPot) {
        boolean isHeated = cookingPot.isHeated(world, pos);
        boolean dirty = false;

        if (isHeated && cookingPot.hasInput()) {
            Optional<CookingPotRecipe> recipe = cookingPot.getMatchingRecipe(new RecipeWrapper(cookingPot.inventory));
            if (recipe.isPresent() && cookingPot.canCook(recipe.get())) {
                dirty = cookingPot.processCooking(recipe.get());
            } else {
                cookingPot.cookTime = 0;
            }
        } else if (cookingPot.cookTime > 0) {
            cookingPot.cookTime = MathHelper.clamp(cookingPot.cookTime - 2, 0, cookingPot.cookTimeTotal);
        }

        ItemStack meal = cookingPot.getMeal();
        if (!meal.isEmpty()) {
            if (!cookingPot.doesMealHaveContainer(meal)) {
                cookingPot.moveMealToOutput();
                dirty = true;
            } else if (!cookingPot.getInventory().getStack(CONTAINER_SLOT).isEmpty()) {
                cookingPot.useStoredContainersOnMeal();
                dirty = true;
            }
        }

        if (dirty) {
            cookingPot.inventoryChanged();
        }
    }

    @Nullable
    @Override
    public Text getCustomName() {
        return customName;
    }

    private Optional<CookingPotRecipe> getMatchingRecipe(RecipeWrapper inventory) {
        if (world == null) {
            return Optional.empty();
        }

        if (lastRecipeID != null) {
            Recipe<Inventory> recipe = ((RecipeManagerAccessorMixin) world.getRecipeManager())
                    .getAllForType(RecipeTypesRegistry.COOKING_RECIPE_SERIALIZER.type())
                    .get(lastRecipeID);
            if (recipe instanceof CookingPotRecipe) {
                if (recipe.matches(inventory, world)) {
                    return Optional.of((CookingPotRecipe) recipe);
                }

                if (recipe.getOutput().isItemEqual(getMeal())) {
                    return Optional.empty();
                }
            }
        }

        if (checkNewRecipe) {
            Optional<CookingPotRecipe> recipe = world.getRecipeManager().getFirstMatch(RecipeTypesRegistry.COOKING_RECIPE_SERIALIZER.type(), inventory, world);
            if (recipe.isPresent()) {
                lastRecipeID = recipe.get().getId();

                return recipe;
            }
        }

        checkNewRecipe = false;

        return Optional.empty();
    }

    public ItemStack getMealContainer() {
        if (!mealContainer.isEmpty()) {
            return mealContainer;
        } else {
            return new ItemStack(getMeal().getItem().getRecipeRemainder());
        }
    }

    private boolean hasInput() {
        for (int i = 0; i < MEAL_DISPLAY_SLOT; ++i) {
            if (!inventory.getStack(i).isEmpty()) {
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
                ItemStack currentOutput = inventory.getStack(MEAL_DISPLAY_SLOT);
                if (currentOutput.isEmpty()) {
                    return true;
                } else if (!currentOutput.isItemEqual(recipeOutput)) {
                    return false;
                } else if (currentOutput.getCount() + recipeOutput.getCount() <= inventory.getMaxCountPerStack()) {
                    return true;
                } else {
                    return currentOutput.getCount() + recipeOutput.getCount() <= recipeOutput.getMaxCount();
                }
            }
        } else {
            return false;
        }
    }

    private boolean processCooking(CookingPotRecipe recipe) {
        if (world == null || recipe == null) return false;

        ++cookTime;
        cookTimeTotal = recipe.getCookTime();
        if (cookTime < cookTimeTotal) {
            return false;
        }

        cookTime = 0;
        mealContainer = recipe.getContainer();
        ItemStack recipeOutput = recipe.getOutput();
        ItemStack currentOutput = inventory.getStack(MEAL_DISPLAY_SLOT);
        if (currentOutput.isEmpty()) {
            inventory.setStack(MEAL_DISPLAY_SLOT, recipeOutput.copy());
        } else if (currentOutput.getItem() == recipeOutput.getItem()) {
            currentOutput.increment(recipeOutput.getCount());
        }
        trackRecipeExperience(recipe);

        for (int i = 0; i < MEAL_DISPLAY_SLOT; ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.getItem().hasRecipeRemainder() && world != null) {
                Direction direction = getCachedState().get(CookingPotBlock.FACING).rotateYCounterclockwise();
                double dropX = pos.getX() + .5d + (direction.getOffsetX() * .25d);
                double dropY = pos.getY() + .7d;
                double dropZ = pos.getZ() + .5d + (direction.getOffsetZ() * .25d);
                ItemEntity entity = new ItemEntity(world, dropX, dropY, dropZ, new ItemStack(inventory.getStack(i).getItem()
                        .getRecipeRemainder()));
                entity.setVelocity(direction.getOffsetX() * .08f, .25f, direction.getOffsetZ() * .08f);
                world.spawnEntity(entity);
            }

            if (!inventory.getStack(i).isEmpty()) {
                inventory.getStack(i).decrement(1);
            }
        }

        return true;
    }

    public void trackRecipeExperience(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            Identifier recipeID = recipe.getId();
            experienceTracker.addTo(recipeID, 1);
        }
    }

    public void clearUsedRecipes(PlayerEntity player) {
        grantStoredRecipeExperience(player.world, player.getPos());
        experienceTracker.clear();
    }

    public void grantStoredRecipeExperience(World world, Vec3d pos) {
        for (Object2IntMap.Entry<Identifier> entry : experienceTracker.object2IntEntrySet()) {
            world.getRecipeManager().get(entry.getKey()).ifPresent(recipe -> splitAndSpawnExperience(world, pos, entry.getIntValue(), ((CookingPotRecipe) recipe).getExperience()));
        }
    }

    private static void splitAndSpawnExperience(World world, Vec3d pos, int craftedAmount, float experience) {
        int expTotal = MathHelper.floor((float) craftedAmount * experience);
        float expFraction = MathHelper.fractionalPart((float) craftedAmount * experience);
        if (expFraction != 0.f && Math.random() < expFraction) {
            ++expTotal;
        }

        while (expTotal > 0) {
            int expValue = ExperienceOrbEntity.roundToOrbSize(expTotal);
            expTotal -= expValue;
            world.spawnEntity(new ExperienceOrbEntity(world, pos.x, pos.y, pos.z, expValue));
        }
    }

    public static void animationTick(World world, BlockPos pos, BlockState state, CookingPotBlockEntity cookingPot) {
        if (world != null && cookingPot.isHeated(world, pos)) {
            Random random = world.random;
            if (random.nextFloat() < .2f) {
                double baseX = pos.getX() + .5d + (random.nextDouble() * .6d - .3d);
                double baseY = pos.getY() + .7d;
                double baseZ = pos.getZ() + .5d + (random.nextDouble() * .6d - .3d);
                world.addParticle(ParticleTypes.BUBBLE_POP, baseX, baseY, baseZ, .0d, .0d, .0d);
            }
            if (random.nextFloat() < .05f) {
                double baseX = pos.getX() + .5d + (random.nextDouble() * .4d - .2d);
                double baseY = pos.getY() + .5d;
                double baseZ = pos.getZ() + .5d + (random.nextDouble() * .4d - .2d);
                double motionY = random.nextBoolean() ? .015d : .005d;
                world.addParticle(ParticleTypesRegistry.STEAM.get(), baseX, baseY, baseZ, .0d, motionY, .0d);
            }
        }
    }

    public ItemStack getMeal() {
        return inventory.getStack(MEAL_DISPLAY_SLOT);
    }

    public boolean isHeated() {
        if (world == null) {
            return false;
        }

        return isHeated(world, pos);
    }

    /**
     * Returns every stored ItemStack in the pot, except for prepared meals.
     *
     * @return a list of item stack.
     */
    public DefaultedList<ItemStack> getDroppableInventory() {
        DefaultedList<ItemStack> drops = DefaultedList.of();
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            drops.add(i == MEAL_DISPLAY_SLOT ? ItemStack.EMPTY : inventory.getStack(i));
        }

        return drops;
    }

    /**
     * Attempts to move all stored meals to the final output. Does NOT check if the meal has a container; this is done on tick.
     */
    private void moveMealToOutput() {
        ItemStack mealDisplay = inventory.getStack(MEAL_DISPLAY_SLOT);
        ItemStack finalOutput = inventory.getStack(OUTPUT_SLOT);
        int mealCount = Math.min(mealDisplay.getCount(), mealDisplay.getMaxCount() - finalOutput.getCount());
        if (finalOutput.isEmpty()) {
            inventory.setStack(OUTPUT_SLOT, mealDisplay.split(mealCount));
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
        ItemStack mealDisplay = inventory.getStack(MEAL_DISPLAY_SLOT);
        ItemStack containerInput = inventory.getStack(CONTAINER_SLOT);
        ItemStack finalOutput = inventory.getStack(OUTPUT_SLOT);

        if (isContainerValid(containerInput) && finalOutput.getCount() < finalOutput.getMaxCount()) {
            int smallerStack = Math.min(mealDisplay.getCount(), containerInput.getCount());
            int mealCount = Math.min(smallerStack, mealDisplay.getMaxCount() - finalOutput.getCount());
            if (finalOutput.isEmpty()) {
                containerInput.decrement(mealCount);
                inventory.setStack(OUTPUT_SLOT, mealDisplay.split(mealCount));
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
        return !mealContainer.isEmpty() || meal.getItem().hasRecipeRemainder();
    }

    public boolean isContainerValid(ItemStack containerItem) {
        if (containerItem.isEmpty()) {
            return false;
        }
        if (!mealContainer.isEmpty()) {
            return mealContainer.isItemEqual(containerItem);
        } else {
            return new ItemStack(getMeal().getItem().getRecipeRemainder()).isItemEqual(containerItem);
        }
    }

    public ItemHandler getInventory() {
        return inventory;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public void setCheckNewRecipe(boolean checkNewRecipe) {
        this.checkNewRecipe = checkNewRecipe;
    }

    private class CookingPotSyncedData implements PropertyDelegate {

        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> CookingPotBlockEntity.this.cookTime;
                case 1 -> CookingPotBlockEntity.this.cookTimeTotal;
                default -> 0;
            };
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