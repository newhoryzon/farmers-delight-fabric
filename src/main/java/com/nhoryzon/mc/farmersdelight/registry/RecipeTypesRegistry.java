package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.recipe.CookingPotRecipe;
import com.nhoryzon.mc.farmersdelight.recipe.CookingPotRecipeSerializer;
import com.nhoryzon.mc.farmersdelight.recipe.CuttingBoardRecipe;
import com.nhoryzon.mc.farmersdelight.recipe.CuttingBoardRecipeSerializer;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public enum RecipeTypesRegistry {
    COOKING_RECIPE_SERIALIZER("cooking", CookingPotRecipe.class, CookingPotRecipeSerializer::new),
    CUTTING_RECIPE_SERIALIZER("cutting", CuttingBoardRecipe.class, CuttingBoardRecipeSerializer::new);

    private final String pathName;
    private final Class<? extends Recipe<? extends Inventory>> recipeClass;
    private final Supplier<RecipeSerializer<? extends Recipe<? extends Inventory>>> recipeSerializerSupplier;
    private RecipeSerializer<? extends Recipe<? extends Inventory>> serializer;
    private RecipeType<? extends Recipe<? extends Inventory>> type;

    RecipeTypesRegistry(String pathName, Class<? extends Recipe<? extends Inventory>> recipeClass,
            Supplier<RecipeSerializer<? extends Recipe<? extends Inventory>>> recipeSerializerSupplier) {
        this.pathName = pathName;
        this.recipeClass = recipeClass;
        this.recipeSerializerSupplier = recipeSerializerSupplier;
    }

    public static void registerAll() {
        for (RecipeTypesRegistry value : values()) {
            Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(FarmersDelightMod.MOD_ID, value.pathName), value.serializer());
            value.type = RecipeType.register(new Identifier(FarmersDelightMod.MOD_ID, value.pathName).toString());
        }
    }

    public RecipeSerializer<? extends Recipe<? extends Inventory>> serializer() {
        if (serializer == null) {
            serializer = recipeSerializerSupplier.get();
        }

        return serializer;
    }

    @SuppressWarnings("unchecked")
    public <T extends Recipe<? extends Inventory>> RecipeType<T> type() {
        return (RecipeType<T>) type(recipeClass);
    }

    @SuppressWarnings({"unchecked","unused"})
    private <T extends Recipe<? extends Inventory>> RecipeType<T> type(Class<T> clazz) {
        if (type == null) {
            type = RecipeType.register(new Identifier(FarmersDelightMod.MOD_ID, pathName).toString());
        }
        return (RecipeType<T>) type;
    }
}