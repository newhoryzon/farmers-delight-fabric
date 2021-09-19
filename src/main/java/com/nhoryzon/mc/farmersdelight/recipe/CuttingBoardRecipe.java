package com.nhoryzon.mc.farmersdelight.recipe;

import com.nhoryzon.mc.farmersdelight.registry.RecipeTypesRegistry;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class CuttingBoardRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final String group;
    private final Ingredient input;
    private final Ingredient tool;
    private final DefaultedList<ItemStack> resultList;
    private final String soundEvent;

    public CuttingBoardRecipe(Identifier id, String group, Ingredient input, Ingredient tool, DefaultedList<ItemStack> resultList, String soundEvent) {
        this.id = id;
        this.group = group;
        this.input = input;
        this.tool = tool;
        this.resultList = resultList;
        this.soundEvent = soundEvent;
    }

    @Override
    public boolean matches(Inventory inv, World world) {
        if (inv.isEmpty()) {
            return false;
        } else {
            return input.test(inv.getStack(0));
        }
    }

    @Override
    public ItemStack craft(Inventory inv) {
        return resultList.get(0).copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public ItemStack getOutput() {
        return resultList.get(0);
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeTypesRegistry.CUTTING_RECIPE_SERIALIZER.serializer();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeTypesRegistry.CUTTING_RECIPE_SERIALIZER.type();
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> ingredient = DefaultedList.of();
        ingredient.add(input);

        return ingredient;
    }

    public DefaultedList<ItemStack> getResultList() {
        return resultList;
    }

    @Override
    public String getGroup() {
        return group;
    }

    public Ingredient getTool() {
        return tool;
    }

    public String getSoundEvent() {
        return soundEvent;
    }
}