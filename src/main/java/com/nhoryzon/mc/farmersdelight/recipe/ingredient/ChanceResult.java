package com.nhoryzon.mc.farmersdelight.recipe.ingredient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;

/**
 * Credits to the Create team for the implementation of results with chances!
 */
public record ChanceResult(ItemStack stack, float chance) {

    public static final ChanceResult EMPTY = new ChanceResult(ItemStack.EMPTY, 1);
    public static final Codec<ChanceResult> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ((MapCodec.MapCodecCodec<ItemStack>) RecipeCodecs.CRAFTING_RESULT).codec().forGetter(ChanceResult::stack),
            Codecs.createStrictOptionalFieldCodec(Codec.FLOAT, "chance", 1.0f).forGetter(ChanceResult::chance)
    ).apply(inst, ChanceResult::new));

    public ItemStack rollOutput(Random rand, int fortuneLevel) {
        int outputAmount = stack.getCount();
        double fortuneBonus = FarmersDelightMod.CONFIG.getCuttingBoardFortuneBonus() * fortuneLevel;
        for (int roll = 0; roll < stack.getCount(); roll++) {
            if (rand.nextFloat() > chance + fortuneBonus) {
                outputAmount--;
            }
        }
        if (outputAmount == 0) {
            return ItemStack.EMPTY;
        }
        ItemStack out = stack.copy();
        out.setCount(outputAmount);
        return out;
    }

    public void write(PacketByteBuf buf) {
        buf.writeItemStack(stack());
        buf.writeFloat(chance());
    }

    public static ChanceResult read(PacketByteBuf buf) {
        return new ChanceResult(buf.readItemStack(), buf.readFloat());
    }

}
