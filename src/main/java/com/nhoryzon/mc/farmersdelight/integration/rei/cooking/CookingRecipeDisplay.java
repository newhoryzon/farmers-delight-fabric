package com.nhoryzon.mc.farmersdelight.integration.rei.cooking;

import com.google.common.collect.ImmutableList;
import com.nhoryzon.mc.farmersdelight.integration.rei.BasicRecipeDisplay;
import com.nhoryzon.mc.farmersdelight.integration.rei.FarmersDelightModREI;
import com.nhoryzon.mc.farmersdelight.recipe.CookingPotRecipe;
import me.shedaniel.rei.api.EntryStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CookingRecipeDisplay extends BasicRecipeDisplay {

    private final EntryStack containerOutput;
    private final int cookTime;

    public CookingRecipeDisplay(CookingPotRecipe recipe) {
        super(EntryStack.ofIngredients(recipe.getIngredients()),
                Collections.singletonList(Collections.singletonList(EntryStack.create(recipe.getOutput()))), recipe.getId());
        containerOutput = EntryStack.create(recipe.getContainer());
        cookTime = recipe.getCookTime();
    }

    @Override
    public @NotNull List<List<EntryStack>> getInputEntries() {
        List<List<EntryStack>> inputEntryList = new ArrayList<>(super.getInputEntries());
        inputEntryList.add(getContainerOutput());

        return ImmutableList.copyOf(inputEntryList);
    }

    @Override
    public @NotNull Identifier getRecipeCategory() {
        return FarmersDelightModREI.COOKING;
    }

    public List<List<EntryStack>> getIngredientEntries() {
        return super.getInputEntries();
    }

    public List<EntryStack> getContainerOutput() {
        return Collections.singletonList(containerOutput);
    }

    public int getCookTime() {
        return cookTime;
    }

}
