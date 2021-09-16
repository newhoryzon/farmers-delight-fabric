package com.nhoryzon.mc.farmersdelight.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

public class CookingPotRecipeSerializer implements RecipeSerializer<CookingPotRecipe> {
    private static DefaultedList<Ingredient> readIngredients(JsonArray ingredientArray) {
        DefaultedList<Ingredient> ingredientList = DefaultedList.of();

        for (int i = 0; i < ingredientArray.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(ingredientArray.get(i));
            if (ingredient.getMatchingStacks() != null && ingredient.getMatchingStacks().length > 0) {
                ingredientList.add(ingredient);
            }
        }

        return ingredientList;
    }

    @Override
    public CookingPotRecipe read(Identifier id, JsonObject json) {
        final String groupIn = JsonHelper.getString(json, "group", "");
        final DefaultedList<Ingredient> inputItemsIn = readIngredients(JsonHelper.getArray(json, "ingredients"));
        if (inputItemsIn.isEmpty()) {
            throw new JsonParseException("No ingredients for cooking recipe");
        } else if (inputItemsIn.size() > CookingPotRecipe.INPUT_SLOTS) {
            throw new JsonParseException("Too many ingredients for cooking recipe! The max is " + CookingPotRecipe.INPUT_SLOTS);
        } else {
            final JsonObject jsonResult = JsonHelper.getObject(json, "result");
            final ItemStack outputIn = new ItemStack(JsonHelper.getItem(jsonResult, "item"), JsonHelper.getInt(jsonResult, "count", 1));
            ItemStack container = ItemStack.EMPTY;
            if (JsonHelper.hasElement(json, "container")) {
                final JsonObject jsonContainer = JsonHelper.getObject(json, "container");
                container = new ItemStack(JsonHelper.getItem(jsonContainer, "item"), JsonHelper.getInt(jsonContainer, "count", 1));
            }
            final float experienceIn = JsonHelper.getFloat(json, "experience", .0f);
            final int cookTimeIn = JsonHelper.getInt(json, "cookingtime", 200);

            return new CookingPotRecipe(id, groupIn, inputItemsIn, outputIn, container, experienceIn, cookTimeIn);
        }
    }

    @Override
    public CookingPotRecipe read(Identifier id, PacketByteBuf buf) {
        String groupIn = buf.readString(32767);

        int ingredientSize = buf.readVarInt();
        DefaultedList<Ingredient> ingredientList = DefaultedList.ofSize(ingredientSize, Ingredient.EMPTY);
        for (int j = 0; j < ingredientList.size(); ++j) {
            ingredientList.set(j, Ingredient.fromPacket(buf));
        }

        ItemStack outputIn = buf.readItemStack();
        ItemStack container = buf.readItemStack();
        float experienceIn = buf.readFloat();
        int cookTimeIn = buf.readVarInt();

        return new CookingPotRecipe(id, groupIn, ingredientList, outputIn, container, experienceIn, cookTimeIn);
    }

    @Override
    public void write(PacketByteBuf buf, CookingPotRecipe recipe) {
        buf.writeString(recipe.getGroup());
        buf.writeVarInt(recipe.getIngredients().size());

        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.write(buf);
        }

        buf.writeItemStack(recipe.getOutput());
        buf.writeItemStack(recipe.getContainer());
        buf.writeFloat(recipe.getExperience());
        buf.writeVarInt(recipe.getCookTime());
    }
}