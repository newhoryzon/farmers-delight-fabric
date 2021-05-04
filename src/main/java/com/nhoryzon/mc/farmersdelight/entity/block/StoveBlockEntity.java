package com.nhoryzon.mc.farmersdelight.entity.block;

import com.nhoryzon.mc.farmersdelight.block.StoveBlock;
import com.nhoryzon.mc.farmersdelight.registry.BlockEntityTypesRegistry;
import com.nhoryzon.mc.farmersdelight.util.MathUtils;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Clearable;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.Random;

public class StoveBlockEntity extends BlockEntity implements BlockEntityClientSerializable, Clearable, Tickable {

    private static final VoxelShape GRILLING_AREA = Block.createCuboidShape(3.f, .0f, 3.f, 13.f, 1.f, 13.f);
    private static final int MAX_STACK_SIZE = 6;

    private final int[] cookingTimes = new int[MAX_STACK_SIZE];
    private final int[] cookingTotalTimes = new int[MAX_STACK_SIZE];

    protected final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(MAX_STACK_SIZE, ItemStack.EMPTY);

    private StoveBlockEntity(BlockEntityType<?> type) {
        super(type);
    }

    public StoveBlockEntity() {
        this(BlockEntityTypesRegistry.STOVE.get());
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        fromTag(tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag, inventory, true);
        tag.putIntArray("CookingTimes", cookingTimes);
        tag.putIntArray("CookingTotalTimes", cookingTotalTimes);

        return super.toTag(tag);
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        fromTag(tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return super.toTag(Inventories.toTag(tag, inventory, true));
    }

    private void fromTag(CompoundTag tag) {
        inventory.clear();
        Inventories.fromTag(tag, inventory);
        if (tag.contains("CookingTimes", 11)) {
            int[] cookingTimeRead = tag.getIntArray("CookingTimes");
            System.arraycopy(cookingTimeRead, 0, cookingTimes, 0, Math.min(cookingTotalTimes.length, cookingTimeRead.length));
        }
        if (tag.contains("CookingTotalTimes", 11)) {
            int[] cookingTotalTimeRead = tag.getIntArray("CookingTotalTimes");
            System.arraycopy(cookingTotalTimeRead, 0, cookingTotalTimes, 0, Math.min(cookingTotalTimes.length, cookingTotalTimeRead.length));
        }
    }

    @Override
    public void tick() {
        boolean isStoveLit = getCachedState().get(StoveBlock.LIT);
        boolean isStoveBlocked = isStoveBlockedAbove();

        if (world != null && world.isClient()) {
            if (isStoveLit) {
                addParticles();
            }
        } else {
            if (world != null && isStoveBlocked && !inventory.isEmpty()) {
                ItemScatterer.spawn(world, pos, inventory);
                inventoryChanged();
            }
            if (isStoveLit && !isStoveBlocked) {
                cookAndDrop();
            } else {
                for (int i = 0; i < inventory.size(); ++i) {
                    if (cookingTimes[i] > 0) {
                        cookingTimes[i] = MathHelper.clamp(cookingTimes[i] - 2, 0, cookingTotalTimes[i]);
                    }
                }
            }
        }
    }

    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    public Optional<CampfireCookingRecipe> findMatchingRecipe(ItemStack itemStack) {
        return world == null || inventory.stream().noneMatch(ItemStack::isEmpty) ? Optional.empty()
                : world.getRecipeManager().getFirstMatch(RecipeType.CAMPFIRE_COOKING, new SimpleInventory(itemStack), world);
    }

    public boolean isStoveBlockedAbove() {
        if (world != null) {
            BlockState above = world.getBlockState(pos.up());
            return VoxelShapes.matchesAnywhere(GRILLING_AREA, above.getOutlineShape(world, pos.up()), BooleanBiFunction.AND);
        }

        return false;
    }

    private void addParticles() {
        World world = getWorld();

        if (world != null) {
            BlockPos blockpos = getPos();
            Random random = world.random;

            for (int j = 0; j < inventory.size(); ++j) {
                if (!inventory.get(j).isEmpty() && random.nextFloat() < .2f) {
                    double d0 = (double) blockpos.getX() + .5d;
                    double d1 = (double) blockpos.getY() + 1.d;
                    double d2 = (double) blockpos.getZ() + .5d;
                    Vec2f v1 = getStoveItemOffset(j);

                    Direction direction = getCachedState().get(StoveBlock.FACING);
                    int directionIndex = direction.getHorizontal();
                    Vec2f offset = directionIndex % 2 == 0 ? v1 : new Vec2f(v1.y, v1.x);

                    double d5 = d0 - (direction.getOffsetX() * offset.x) + (direction.rotateYClockwise().getOffsetX() * offset.x);
                    double d7 = d2 - (direction.getOffsetZ() * offset.y) + (direction.rotateYClockwise().getOffsetZ() * offset.y);

                    for (int k = 0; k < 3; ++k) {
                        world.addParticle(ParticleTypes.SMOKE, d5, d1, d7, .0d, 5.e-4d, .0d);
                    }
                }
            }
        }
    }

    public Vec2f getStoveItemOffset(int index) {
        final float X_OFFSET = .3f;
        final float Y_OFFSET = .2f;
        final Vec2f[] OFFSETS = {new Vec2f(X_OFFSET, Y_OFFSET), new Vec2f(.0f, Y_OFFSET), new Vec2f(-X_OFFSET, Y_OFFSET), new Vec2f(
                X_OFFSET, -Y_OFFSET), new Vec2f(.0f, -Y_OFFSET), new Vec2f(-X_OFFSET, -Y_OFFSET),};

        return OFFSETS[index];
    }

    private void inventoryChanged() {
        markDirty();
        if (world != null) {
            world.updateListeners(getPos(), getCachedState(), getCachedState(), 3);
        }
    }

    private void cookAndDrop() {
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemstack = inventory.get(i);
            if (!itemstack.isEmpty()) {
                ++cookingTimes[i];
                if (cookingTimes[i] >= cookingTotalTimes[i]) {
                    if (world != null) {
                        Inventory inventory = new SimpleInventory(itemstack);
                        ItemStack result = world.getRecipeManager().getAllMatches(RecipeType.CAMPFIRE_COOKING, inventory, world).stream()
                                .map(recipe -> recipe.craft(inventory)).findAny().orElse(itemstack);
                        if (!result.isEmpty()) {
                            ItemEntity entity = new ItemEntity(world, pos.getX() + .5, pos.getY() + 1., pos.getZ() + .5, result.copy());
                            entity.setVelocity(MathUtils.RAND.nextGaussian() * (double) .01f, .1f,
                                    MathUtils.RAND.nextGaussian() * (double) .01f);
                            world.spawnEntity(entity);
                        }
                    }
                    inventory.set(i, ItemStack.EMPTY);
                    inventoryChanged();
                }
            }
        }
    }

    public boolean addItem(ItemStack itemStack, int cookTime) {
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemstack = inventory.get(i);
            if (itemstack.isEmpty()) {
                cookingTotalTimes[i] = cookTime;
                cookingTimes[i] = 0;
                inventory.set(i, itemStack.split(1));
                inventoryChanged();
                return true;
            }
        }

        return false;
    }

}
