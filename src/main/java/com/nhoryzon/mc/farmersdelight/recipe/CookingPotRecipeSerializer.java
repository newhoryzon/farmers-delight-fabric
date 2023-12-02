package com.nhoryzon.mc.farmersdelight.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeCodecs;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;

public class CookingPotRecipeSerializer implements RecipeSerializer<CookingPotRecipe> {

    private static final Codec<CookingPotRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codecs.createStrictOptionalFieldCodec(Codec.STRING, "group", "").forGetter(CookingPotRecipe::getGroup),
            Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf("ingredients").xmap(ingredients -> {
                DefaultedList<Ingredient> nonNullList = DefaultedList.of();
                nonNullList.addAll(ingredients);
                return nonNullList;
            }, ingredients -> ingredients).forGetter(CookingPotRecipe::getIngredients),
            RecipeCodecs.CRAFTING_RESULT.fieldOf("result").forGetter(r -> r.getResult(null)),
            Codecs.createStrictOptionalFieldCodec(RecipeCodecs.CRAFTING_RESULT, "container", ItemStack.EMPTY).forGetter(CookingPotRecipe::getContainer),
            Codecs.createStrictOptionalFieldCodec(Codec.FLOAT, "experience", 0.0F).forGetter(CookingPotRecipe::getExperience),
            Codecs.createStrictOptionalFieldCodec(Codec.INT, "cookingtime", 200).forGetter(CookingPotRecipe::getCookTime)
    ).apply(inst, CookingPotRecipe::new));

    @Override
    public Codec<CookingPotRecipe> codec() {
        return CODEC;
    }

    @Override
    public CookingPotRecipe read(PacketByteBuf buf) {
        String groupIn = buf.readString();
        int i = buf.readVarInt();
        DefaultedList<Ingredient> inputItemsIn = DefaultedList.ofSize(i, Ingredient.EMPTY);

        for (int j = 0; j < inputItemsIn.size(); ++j) {
            inputItemsIn.set(j, Ingredient.fromPacket(buf));
        }

        ItemStack outputIn = buf.readItemStack();
        ItemStack container = buf.readItemStack();
        float experienceIn = buf.readFloat();
        int cookTimeIn = buf.readVarInt();
        return new CookingPotRecipe(groupIn, inputItemsIn, outputIn, container, experienceIn, cookTimeIn);
    }

    @Override
    public void write(PacketByteBuf buf, CookingPotRecipe recipe) {
        buf.writeString(recipe.getGroup());
        buf.writeVarInt(recipe.getIngredients().size());

        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.write(buf);
        }

        buf.writeItemStack(recipe.getResult(null));
        buf.writeItemStack(recipe.getContainer());
        buf.writeFloat(recipe.getExperience());
        buf.writeVarInt(recipe.getCookTime());
    }
}