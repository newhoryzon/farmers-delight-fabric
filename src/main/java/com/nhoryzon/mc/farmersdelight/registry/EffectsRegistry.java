package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.effect.ComfortEffect;
import com.nhoryzon.mc.farmersdelight.effect.NourishmentEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public enum EffectsRegistry {
    NOURISHMENT("nourishment", new NourishmentEffect()),
    COMFORT("comfort", new ComfortEffect());

    private final String pathName;
    private final StatusEffect effect;

    EffectsRegistry(String pathName, StatusEffect effect) {
        this.pathName = pathName;
        this.effect = effect;
    }

    public static void registerAll() {
        for (EffectsRegistry value : values()) {
            Registry.register(Registries.STATUS_EFFECT, new Identifier(FarmersDelightMod.MOD_ID, value.pathName), value.effect);
        }
    }

    public StatusEffect get() {
        return effect;
    }
}