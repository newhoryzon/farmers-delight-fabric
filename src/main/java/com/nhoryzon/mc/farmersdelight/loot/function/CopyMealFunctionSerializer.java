package com.nhoryzon.mc.farmersdelight.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.function.ConditionalLootFunction;

public class CopyMealFunctionSerializer extends ConditionalLootFunction.Serializer<CopyMealFunction> {
    @Override
    public CopyMealFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
        return new CopyMealFunction(conditions);
    }
}