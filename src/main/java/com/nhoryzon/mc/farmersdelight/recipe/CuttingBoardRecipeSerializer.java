package com.nhoryzon.mc.farmersdelight.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

public class CuttingBoardRecipeSerializer implements RecipeSerializer<CuttingBoardRecipe> {
    private static DefaultedList<Ingredient> readIngredients(JsonArray ingredientArray) {
        DefaultedList<Ingredient> ingredientList = DefaultedList.of();

        for (JsonElement ingredientJson : ingredientArray) {
            Ingredient ingredient = Ingredient.fromJson(ingredientJson);
            if (ingredient.getMatchingStacks() != null && ingredient.getMatchingStacks().length > 0) {
                ingredientList.add(ingredient);
            }
        }

        return ingredientList;
    }

    private static DefaultedList<ItemStack> readResultList(JsonArray resultArray) {
        DefaultedList<ItemStack> resultList = DefaultedList.of();

        for (JsonElement resultJson : resultArray) {
            final JsonObject jsonResult = resultJson.getAsJsonObject();
            resultList.add(new ItemStack(JsonHelper.getItem(jsonResult, "item"), JsonHelper.getInt(jsonResult, "count", 1)));
        }

        return resultList;
    }

    @Override
    public CuttingBoardRecipe read(Identifier id, JsonObject json) {
        final String groupIn = JsonHelper.getString(json, "group", "");
        final DefaultedList<Ingredient> inputItemsIn = readIngredients(JsonHelper.getArray(json, "ingredients"));
        final JsonObject toolObject = JsonHelper.getObject(json, "tool");
        final Ingredient tool = Ingredient.fromJson(toolObject);
        if (inputItemsIn.isEmpty()) {
            throw new JsonParseException("No ingredients for cooking recipe");
        } else if (tool.getMatchingStacks() == null || tool.getMatchingStacks().length == 0) {
            throw new JsonParseException("No tool for cutting recipe");
        } else if (inputItemsIn.size() > 1) {
            throw new JsonParseException("Too many ingredients for cooking recipe! Please define only one ingredient");
        } else {
            final DefaultedList<ItemStack> resultList = readResultList(JsonHelper.getArray(json, "result"));
            final String soundId = JsonHelper.getString(json, "sound", "");
            return new CuttingBoardRecipe(id, groupIn, inputItemsIn.get(0), tool, resultList, soundId);
        }
    }

    @Override
    public CuttingBoardRecipe read(Identifier id, PacketByteBuf buf) {
        String groupIn = buf.readString(32767);
        Ingredient input = Ingredient.fromPacket(buf);
        Ingredient tool = Ingredient.fromPacket(buf);

        int resultSize = buf.readVarInt();
        DefaultedList<ItemStack> resultList = DefaultedList.ofSize(resultSize, ItemStack.EMPTY);
        for (int j = 0; j < resultList.size(); ++j) {
            resultList.set(j, buf.readItemStack());
        }
        String soundEvent = buf.readString();

        return new CuttingBoardRecipe(id, groupIn, input, tool, resultList, soundEvent);
    }

    @Override
    public void write(PacketByteBuf buf, CuttingBoardRecipe recipe) {
        buf.writeString(recipe.getGroup());
        recipe.getIngredients().get(0).write(buf);
        recipe.getTool().write(buf);

        buf.writeVarInt(recipe.getResultList().size());
        for (ItemStack result : recipe.getResultList()) {
            buf.writeItemStack(result);
        }

        buf.writeString(recipe.getSoundEvent());
    }
}