package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.loot.function.CopyMealFunctionSerializer;
import com.nhoryzon.mc.farmersdelight.loot.function.SmokerCookFunctionSerializer;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public enum LootFunctionsRegistry {
    COPY_MEAL("copy_meal", CopyMealFunctionSerializer::new),
    SMOKER_COOK("smoker_cook", SmokerCookFunctionSerializer::new);

    private final String pathName;
    private final Supplier<ConditionalLootFunction.Serializer<? extends LootFunction>> lootFunctionSerializerSupplier;
    private ConditionalLootFunction.Serializer<? extends LootFunction> serializer;
    private LootFunctionType type;

    LootFunctionsRegistry(String pathName,
            Supplier<ConditionalLootFunction.Serializer<? extends LootFunction>> lootFunctionSerializerSupplier) {
        this.pathName = pathName;
        this.lootFunctionSerializerSupplier = lootFunctionSerializerSupplier;
    }

    public static void registerAll() {
        for (LootFunctionsRegistry value : values()) {
            Registry.register(Registry.LOOT_FUNCTION_TYPE, new Identifier(FarmersDelightMod.MOD_ID, value.pathName), value.type());
        }
    }

    public LootFunctionType type() {
        if (type == null) {
            type = new LootFunctionType(serializer());
        }
        return type;
    }

    public ConditionalLootFunction.Serializer<? extends LootFunction> serializer() {
        if (serializer == null) {
            serializer = lootFunctionSerializerSupplier.get();
        }

        return serializer;
    }
}