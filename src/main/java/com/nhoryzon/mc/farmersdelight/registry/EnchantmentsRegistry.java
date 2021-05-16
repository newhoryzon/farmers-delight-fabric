package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.enchantment.BackstabbingEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public enum EnchantmentsRegistry {
    BACKSTABBING("backstabbing", BackstabbingEnchantment::new);

    private final String pathName;
    private final Supplier<? extends Enchantment> enchantmentSupplier;
    private Enchantment enchantment;

    EnchantmentsRegistry(String pathName, Supplier<? extends Enchantment> enchantmentSupplier) {
        this.pathName = pathName;
        this.enchantmentSupplier = enchantmentSupplier;
    }

    public static void registerAll() {
        for (EnchantmentsRegistry value : values()) {
            Registry.register(Registry.ENCHANTMENT, new Identifier(FarmersDelightMod.MOD_ID, value.pathName), value.get());
        }
    }

    public Enchantment get() {
        if (enchantment == null) {
            enchantment = enchantmentSupplier.get();
        }

        return enchantment;
    }
}