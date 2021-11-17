package com.nhoryzon.mc.farmersdelight.integration.rei;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public abstract class BasicRecipeDisplay implements RecipeDisplay {

    protected List<List<EntryStack>> inputs;
    protected List<List<EntryStack>> outputs;
    protected Identifier location;

    public BasicRecipeDisplay(List<List<EntryStack>> inputs, List<List<EntryStack>> outputs) {
        this(inputs, outputs, null);
    }

    public BasicRecipeDisplay(List<List<EntryStack>> inputs, List<List<EntryStack>> outputs, Identifier location) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.location = location;
    }

    @Override
    public @NotNull List<List<EntryStack>> getInputEntries() {
        return inputs;
    }

    @Override
    public @NotNull List<List<EntryStack>> getResultingEntries() {
        return outputs;
    }

    @Override
    public @NotNull Optional<Identifier> getRecipeLocation() {
        return Optional.ofNullable(location);
    }

}
