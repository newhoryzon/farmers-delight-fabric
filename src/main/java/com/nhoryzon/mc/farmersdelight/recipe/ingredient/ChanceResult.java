package com.nhoryzon.mc.farmersdelight.recipe.ingredient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;

/**
 * Credits to the Create team for the implementation of results with chances!
 */
public class ChanceResult {

    public static final ChanceResult EMPTY = new ChanceResult(ItemStack.EMPTY, 1);

    private static final float DEFAULT_CUTTING_BOARD_FORTUNE_BONUS = .2f;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final ItemStack stack;
    private final float chance;

    public ChanceResult(ItemStack stack, float chance) {
        this.stack = stack;
        this.chance = chance;
    }

    public ItemStack getStack() {
        return stack;
    }

    public float getChance() {
        return chance;
    }

    public ItemStack rollOutput(Random rand, int fortuneLevel) {
        int outputAmount = stack.getCount();
        double fortuneBonus = DEFAULT_CUTTING_BOARD_FORTUNE_BONUS * fortuneLevel;
        for (int roll = 0; roll < stack.getCount(); roll++)
            if (rand.nextFloat() > chance + fortuneBonus)
                outputAmount--;
        if (outputAmount == 0)
            return ItemStack.EMPTY;
        ItemStack out = stack.copy();
        out.setCount(outputAmount);
        return out;
    }

    public JsonElement serialize() {
        JsonObject json = new JsonObject();

        Identifier identifier = Registry.ITEM.getId(stack.getItem());
        json.addProperty("item", identifier.toString());

        int count = stack.getCount();
        if (count != 1)
            json.addProperty("count", count);
        if (stack.hasNbt())
            json.add("nbt", new JsonParser().parse(stack.getNbt().toString()));
        if (chance != 1)
            json.addProperty("chance", chance);

        return json;
    }

    public static ChanceResult deserialize(JsonElement je) {
        if (!je.isJsonObject())
            throw new JsonSyntaxException("Must be a json object");

        JsonObject json = je.getAsJsonObject();
        String itemId = JsonHelper.getString(json, "item");
        int count = JsonHelper.getInt(json, "count", 1);
        float chance = JsonHelper.getFloat(json, "chance", 1);
        ItemStack itemstack = new ItemStack(Registry.ITEM.get(new Identifier(itemId)), count);

        if (JsonHelper.hasPrimitive(json, "nbt")) {
            try {
                JsonElement element = json.get("nbt");
                itemstack.setNbt(NbtHelper.fromNbtProviderString(
                        element.isJsonObject() ? GSON.toJson(element) : JsonHelper.asString(element, "nbt")));
            }
            catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        }

        return new ChanceResult(itemstack, chance);
    }

    public void write(PacketByteBuf buf) {
        buf.writeItemStack(getStack());
        buf.writeFloat(getChance());
    }

    public static ChanceResult read(PacketByteBuf buf) {
        return new ChanceResult(buf.readItemStack(), buf.readFloat());
    }

}
