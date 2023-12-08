package com.nhoryzon.mc.farmersdelight.integration.rei.cutting;

import com.google.common.collect.ImmutableList;
import com.nhoryzon.mc.farmersdelight.integration.rei.ChanceArrayIngredient;
import com.nhoryzon.mc.farmersdelight.integration.rei.FarmersDelightModREI;
import com.nhoryzon.mc.farmersdelight.recipe.CuttingBoardRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.RecipeEntry;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CuttingRecipeDisplay extends BasicDisplay {

    private final EntryIngredient toolInput;
    protected final List<ChanceArrayIngredient> chanceOutputs;
    protected final List<EntryIngredient> mandatoryOutputs;

    public CuttingRecipeDisplay(RecipeEntry<CuttingBoardRecipe> recipe) {
        super(EntryIngredients.ofIngredients(recipe.value().getIngredients()), recipe.value().getResultList().stream().map(EntryIngredients::of).toList());
        mandatoryOutputs = recipe.value().getMandatoryResult().stream().map(EntryIngredients::of).toList();
        chanceOutputs = recipe.value().getVariableResult().stream().map(ChanceArrayIngredient::new).toList();
        toolInput = EntryIngredients.ofIngredient(recipe.value().getTool());
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return FarmersDelightModREI.CUTTING;
    }

    @Override
    public List<EntryIngredient> getRequiredEntries() {
        List<EntryIngredient> requiredEntries = new ArrayList<>(super.getRequiredEntries());
        requiredEntries.add(getToolInput());

        return ImmutableList.copyOf(requiredEntries);
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        List<EntryIngredient> inputEntryList = new ArrayList<>(super.getInputEntries());
        inputEntryList.add(getToolInput());

        return ImmutableList.copyOf(inputEntryList);
    }

    public List<EntryIngredient> getIngredientEntries() {
        return super.getInputEntries();
    }

    public EntryIngredient getToolInput() {
        return toolInput;
    }

    public List<EntryIngredient> getMandatoryOutputs() {
        return mandatoryOutputs;
    }

    public List<ChanceArrayIngredient> getChanceOutputs() {
        return chanceOutputs;
    }

}
