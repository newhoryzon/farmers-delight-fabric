package com.nhoryzon.mc.farmersdelight.recipe;

import com.nhoryzon.mc.farmersdelight.recipe.ingredient.ChanceResult;
import com.nhoryzon.mc.farmersdelight.registry.RecipeTypesRegistry;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CuttingBoardRecipe implements Recipe<Inventory> {

    public static final int MAX_INPUT_COUNT = 1;
    public static final int MAX_RESULT_COUNT = 4;

    private final Identifier id;
    private final String group;
    private final Ingredient input;
    private final Ingredient tool;
    private final DefaultedList<ChanceResult> resultList;
    private final String soundEvent;

    public CuttingBoardRecipe(Identifier id, String group, Ingredient input, Ingredient tool, DefaultedList<ChanceResult> resultList, String soundEvent) {
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
        return resultList.get(0).stack().copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= MAX_INPUT_COUNT;
    }

    @Override
    public ItemStack getOutput() {
        return resultList.get(0).stack();
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

    public DefaultedList<Ingredient> getIngredientsAndTool() {
        DefaultedList<Ingredient> ingredient = DefaultedList.of();
        ingredient.add(input);
        ingredient.add(tool);

        return ingredient;
    }

    public List<ItemStack> getResultList() {
        return getRollableResults().stream().map(ChanceResult::stack).collect(Collectors.toList());
    }

    public List<ItemStack> getMandatoryResult() {
        return getRollableResults().stream().filter(chanceResult -> chanceResult.chance() == 1).map(ChanceResult::stack).toList();
    }

    public List<ChanceResult> getVariableResult() {
        return getRollableResults().stream().filter(chanceResult -> chanceResult.chance() != 1).toList();
    }

    public DefaultedList<ChanceResult> getRollableResults() {
        return resultList;
    }

    public List<ItemStack> getRolledResults(Random rand, int fortuneLevel) {
        List<ItemStack> results = new ArrayList<>();
        getRollableResults().forEach(chanceResult -> {
            ItemStack stack = chanceResult.rollOutput(rand, fortuneLevel);
            if (!stack.isEmpty()) {
                results.add(stack);
            }
        });

        return results;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    public Ingredient getTool() {
        return tool;
    }

    public String getSoundEvent() {
        return soundEvent;
    }

}