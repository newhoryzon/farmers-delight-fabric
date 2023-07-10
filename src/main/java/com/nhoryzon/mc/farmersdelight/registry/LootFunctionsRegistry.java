package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.loot.function.CopyMealFunctionSerializer;
import com.nhoryzon.mc.farmersdelight.loot.function.CopySkilletFunctionSerializer;
import com.nhoryzon.mc.farmersdelight.loot.function.SmokerCookFunctionSerializer;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public enum LootFunctionsRegistry {
    COPY_MEAL("copy_meal", CopyMealFunctionSerializer::new),
    SMOKER_COOK("smoker_cook", SmokerCookFunctionSerializer::new),

    COPY_SKILLET("copy_skillet", CopySkilletFunctionSerializer::new);

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
            Registry.register(Registries.LOOT_FUNCTION_TYPE, new Identifier(FarmersDelightMod.MOD_ID, value.pathName), value.type());
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