package com.nhoryzon.mc.farmersdelight.integration.rei.cooking;

import com.google.common.collect.ImmutableList;
import com.nhoryzon.mc.farmersdelight.integration.rei.FarmersDelightModREI;
import com.nhoryzon.mc.farmersdelight.recipe.CookingPotRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.RecipeEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class CookingRecipeDisplay extends BasicDisplay {

    private final EntryIngredient containerOutput;
    private final int cookTime;

    public CookingRecipeDisplay(RecipeEntry<CookingPotRecipe> recipe) {
        super(EntryIngredients.ofIngredients(recipe.value().getIngredients()),
                Collections.singletonList(EntryIngredients.of(recipe.value().getResult(null))),
                Optional.ofNullable(recipe.id()));
        containerOutput = EntryIngredients.of(recipe.value().getContainer());
        cookTime = recipe.value().getCookTime();
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return FarmersDelightModREI.COOKING;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        List<EntryIngredient> inputEntryList = new ArrayList<>(super.getInputEntries());
        inputEntryList.add(getContainerOutput());

        return ImmutableList.copyOf(inputEntryList);
    }

    public List<EntryIngredient> getIngredientEntries() {
        return super.getInputEntries();
    }

    public EntryIngredient getContainerOutput() {
        return containerOutput;
    }

    public int getCookTime() {
        return cookTime;
    }

}
