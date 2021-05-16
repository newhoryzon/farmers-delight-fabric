package com.nhoryzon.mc.farmersdelight.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.function.ConditionalLootFunction;

public class SmokerCookFunctionSerializer extends ConditionalLootFunction.Serializer<SmokerCookFunction> {
    @Override
    public SmokerCookFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
        return new SmokerCookFunction(conditions);
    }
}