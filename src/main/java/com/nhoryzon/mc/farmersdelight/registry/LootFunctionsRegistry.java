package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.loot.function.CopyMealFunction;
import com.nhoryzon.mc.farmersdelight.loot.function.CopySkilletFunction;
import com.nhoryzon.mc.farmersdelight.loot.function.SmokerCookFunction;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public enum LootFunctionsRegistry {
    COPY_MEAL("copy_meal", () -> new LootFunctionType(CopyMealFunction.CODEC)),
    SMOKER_COOK("smoker_cook", () -> new LootFunctionType(SmokerCookFunction.CODEC)),

    COPY_SKILLET("copy_skillet", () -> new LootFunctionType(CopySkilletFunction.CODEC));

    private final String pathName;
    private final Supplier<LootFunctionType> lootFunctionSerializerSupplier;

    LootFunctionsRegistry(String pathName,
            Supplier<LootFunctionType> lootFunctionSerializerSupplier) {
        this.pathName = pathName;
        this.lootFunctionSerializerSupplier = lootFunctionSerializerSupplier;
    }

    public static void registerAll() {
        for (LootFunctionsRegistry value : values()) {
            Registry.register(Registries.LOOT_FUNCTION_TYPE, new Identifier(FarmersDelightMod.MOD_ID, value.pathName), value.lootFunctionSerializerSupplier.get());
        }
    }

    public LootFunctionType type() {
        return lootFunctionSerializerSupplier.get();
    }
}