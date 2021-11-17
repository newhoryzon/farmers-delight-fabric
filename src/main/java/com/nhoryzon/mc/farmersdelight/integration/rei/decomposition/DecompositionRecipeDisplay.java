package com.nhoryzon.mc.farmersdelight.integration.rei.decomposition;

import com.nhoryzon.mc.farmersdelight.integration.rei.FarmersDelightModREI;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;

import java.util.Collections;

public class DecompositionRecipeDisplay extends BasicDisplay {

    private final EntryIngredient modifier;

    public DecompositionRecipeDisplay(EntryIngredient base, EntryIngredient enriched, EntryIngredient modifier) {
        super(Collections.singletonList(base), Collections.singletonList(enriched));
        this.modifier = modifier;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return FarmersDelightModREI.DECOMPOSITION;
    }

    public EntryIngredient getModifier() {
        return modifier;
    }

}
