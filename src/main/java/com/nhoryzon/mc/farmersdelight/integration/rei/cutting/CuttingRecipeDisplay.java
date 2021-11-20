package com.nhoryzon.mc.farmersdelight.integration.rei.cutting;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.nhoryzon.mc.farmersdelight.integration.rei.BasicRecipeDisplay;
import com.nhoryzon.mc.farmersdelight.integration.rei.FarmersDelightModREI;
import com.nhoryzon.mc.farmersdelight.recipe.CuttingBoardRecipe;
import me.shedaniel.rei.api.EntryStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class CuttingRecipeDisplay extends BasicRecipeDisplay {

    private final List<EntryStack> toolInput;

    public CuttingRecipeDisplay(CuttingBoardRecipe recipe) {
        super(EntryStack.ofIngredients(recipe.getIngredients()),
                recipe.getResultList().stream().map(itemStack -> Lists.newArrayList(EntryStack.create(itemStack))).collect(Collectors.toList()));
        toolInput = EntryStack.ofIngredient(recipe.getTool());
    }

    @Override
    public @NotNull Identifier getRecipeCategory() {
        return FarmersDelightModREI.CUTTING;
    }

    @Override
    public @NotNull List<List<EntryStack>> getInputEntries() {
        List<List<EntryStack>> inputEntryList = new ArrayList<>(super.getInputEntries());
        inputEntryList.add(getToolInput());

        return ImmutableList.copyOf(inputEntryList);
    }

    @Override
    public @NotNull List<List<EntryStack>> getRequiredEntries() {
        List<List<EntryStack>> requiredEntries = new ArrayList<>(super.getRequiredEntries());
        requiredEntries.add(getToolInput());

        return ImmutableList.copyOf(requiredEntries);
    }

    public List<List<EntryStack>> getIngredientEntries() {
        return super.getInputEntries();
    }

    public List<EntryStack> getToolInput() {
        return toolInput;
    }

}
