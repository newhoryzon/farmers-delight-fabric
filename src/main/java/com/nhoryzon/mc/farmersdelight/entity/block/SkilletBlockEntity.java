package com.nhoryzon.mc.farmersdelight.entity.block;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.block.SkilletBlock;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.ItemStackInventory;
import com.nhoryzon.mc.farmersdelight.mixin.accessors.RecipeManagerAccessorMixin;
import com.nhoryzon.mc.farmersdelight.registry.BlockEntityTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.ParticleTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.SoundsRegistry;
import com.nhoryzon.mc.farmersdelight.util.CompoundTagUtils;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class SkilletBlockEntity extends SyncedBlockEntity implements ItemStackInventory, HeatableBlockEntity {

    public static final String TAG_KEY_SKILLET_STACK = "Skillet";

    private int cookTime;
    private int cookTimeTotal;
    private final DefaultedList<ItemStack> inventory;
    private Identifier lastRecipeID;
    private ItemStack skilletStack;
    private int fireAspectLevel;

    public SkilletBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityTypesRegistry.SKILLET.get(), blockPos, blockState);
        skilletStack = ItemStack.EMPTY;
        inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    }

    public static void cookingTick(World world, BlockPos pos, BlockState state, SkilletBlockEntity skillet) {
        boolean isHeated = skillet.isHeated(world, pos);
        if (isHeated) {
            ItemStack cookingStack = skillet.getStoredStack();
            if (cookingStack.isEmpty()) {
                skillet.cookTime = 0;
            } else {
                skillet.cookAndOutputItems(cookingStack);
            }
        } else if (skillet.cookTime > 0 ) {
            skillet.cookTime = MathHelper.clamp(skillet.cookTime - 2, 0, skillet.cookTimeTotal);
        }
    }

    public static void animationTick(World world, BlockPos pos, BlockState state, SkilletBlockEntity skillet) {
        if (skillet.isHeated(world, pos) && skillet.hasStoredStack()) {
            Random random = world.getRandom();
            if (random.nextFloat() < .2f) {
                double x = (double) pos.getX() + .5d + (random.nextDouble() * .4d - .2d);
                double y = (double) pos.getY() + .1d;
                double z = (double) pos.getZ() + .5d + (random.nextDouble() * .4d - .2d);
                double motionY = random.nextBoolean() ? .015d : .005d;
                world.addParticle(ParticleTypesRegistry.STEAM.get(), x, y, z, .0d, motionY, .0d);
            }
            if (skillet.fireAspectLevel > 0 && random.nextFloat() < skillet.fireAspectLevel * .05f) {
                double x = (double) pos.getX() + .5d + (random.nextDouble() * .4d - 0.2d);
                double y = (double) pos.getY() + .1d;
                double z = (double) pos.getZ() + .5d + (random.nextDouble() * .4d - 0.2d);
                double motionX = world.random.nextFloat() - .5f;
                double motionY = world.random.nextFloat() * .5f + .2f;
                double motionZ = world.random.nextFloat() - .5f;
                world.addParticle(ParticleTypes.ENCHANTED_HIT, x, y, z, motionX, motionY, motionZ);
            }
        }
    }

    private void cookAndOutputItems(ItemStack cookingStack) {
        if (world == null) {
            return;
        }

        ++cookTime;
        if (cookTime >= cookTimeTotal) {
            SimpleInventory wrapper = new SimpleInventory(cookingStack);
            Optional<RecipeEntry<CampfireCookingRecipe>> recipe = getMatchingRecipe(wrapper);
            if (recipe.isPresent()) {
                ItemStack resultStack = recipe.get().value().craft(wrapper, world.getRegistryManager());
                Direction direction = getCachedState().get(SkilletBlock.FACING).rotateYClockwise();
                ItemEntity entity = new ItemEntity(world, pos.getX() + .5, pos.getY() + .3, pos.getZ() + .5, resultStack.copy());
                entity.setVelocity(direction.getOffsetX() *.08f, .25f, direction.getOffsetZ() * .08f);
                world.spawnEntity(entity);
                cookTime = 0;
                removeStack(0, 1);
            }
        }
    }

    public boolean isCooking() {
        return isHeated() && hasStoredStack();
    }

    public boolean isHeated() {
        if (world != null) {
            return isHeated(world, pos);
        }

        return false;
    }

    private Optional<RecipeEntry<CampfireCookingRecipe>> getMatchingRecipe(Inventory inventory) {
        if (world == null) {
            return Optional.empty();
        }

        if (lastRecipeID != null) {
            RecipeEntry<CampfireCookingRecipe> recipe = ((RecipeManagerAccessorMixin) world.getRecipeManager())
                    .getAllForType(RecipeType.CAMPFIRE_COOKING)
                    .get(lastRecipeID);
            return Optional.of(recipe);
        }

        Optional<RecipeEntry<CampfireCookingRecipe>> recipe = world.getRecipeManager().getFirstMatch(RecipeType.CAMPFIRE_COOKING, inventory, world);
        if (recipe.isPresent()) {
            lastRecipeID = recipe.get().id();

            return recipe;
        }

        return Optional.empty();
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        fromTag(nbt);
    }

    private void fromTag(NbtCompound nbt) {
        readInventoryNbt(nbt);
        cookTime = nbt.getInt(CompoundTagUtils.TAG_KEY_COOK_TIME);
        cookTimeTotal = nbt.getInt(CompoundTagUtils.TAG_KEY_COOK_TIME_TOTAL);
        skilletStack = ItemStack.fromNbt(nbt.getCompound(TAG_KEY_SKILLET_STACK));
        fireAspectLevel = EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, skilletStack);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        writeInventoryNbt(nbt);
        nbt.putInt(CompoundTagUtils.TAG_KEY_COOK_TIME, cookTime);
        nbt.putInt(CompoundTagUtils.TAG_KEY_COOK_TIME_TOTAL, cookTimeTotal);
        nbt.put(TAG_KEY_SKILLET_STACK, skilletStack.writeNbt(new NbtCompound()));
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        writeNbt(nbtCompound);

        return nbtCompound;
    }

    public NbtCompound writeSkilletItem(NbtCompound nbt) {
        nbt.put(TAG_KEY_SKILLET_STACK, skilletStack.writeNbt(new NbtCompound()));
        return nbt;
    }

    public void setSkilletItem(ItemStack stack) {
        skilletStack = stack.copy();
        fireAspectLevel = EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, stack);
        inventoryChanged();
    }

    public ItemStack addItemToCook(ItemStack addedStack, PlayerEntity player) {
        Optional<RecipeEntry<CampfireCookingRecipe>> recipe = getMatchingRecipe(new SimpleInventory(addedStack));
        if (recipe.isPresent()) {
            cookTimeTotal = SkilletBlock.getSkilletCookingTime(recipe.get().value().getCookingTime(), fireAspectLevel);
            boolean wasEmpty = getStoredStack().isEmpty();
            ItemStack remainderStack = insertStack(0, addedStack.copy(), false);
            if (!ItemStack.areEqual(remainderStack, addedStack)) {
                lastRecipeID = recipe.get().id();
                cookTime = 0;
                if (wasEmpty && world != null && isHeated(world, pos)) {
                    world.playSound(null, pos.getX() + .5f, pos.getY() + .5f, pos.getZ() + .5f,
                            SoundsRegistry.BLOCK_SKILLET_ADD_FOOD.get(), SoundCategory.BLOCKS, .8f, 1.f);
                }

                return remainderStack;
            }
        } else if (player != null) {
            player.sendMessage(FarmersDelightMod.i18n("block.skillet.invalid_item"), true);
        }

        return addedStack;
    }

    public ItemStack removeItem() {
        return removeStack(0, getStoredStack().getMaxCount());
    }

    public ItemStack getStoredStack() {
        return getStack(0);
    }

    public boolean hasStoredStack() {
        return !getStoredStack().isEmpty();
    }

    @Override
    public void inventoryChanged() {
        markDirty();
        Objects.requireNonNull(world).updateListeners(getPos(), getCachedState(), getCachedState(), 3);
    }

}
