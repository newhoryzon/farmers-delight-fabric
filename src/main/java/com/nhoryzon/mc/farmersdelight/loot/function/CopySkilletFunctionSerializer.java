package com.nhoryzon.mc.farmersdelight.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.function.ConditionalLootFunction;

public class CopySkilletFunctionSerializer extends ConditionalLootFunction.Serializer<CopySkilletFunction> {
    @Override
    public CopySkilletFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
        return new CopySkilletFunction(conditions);
    }
}