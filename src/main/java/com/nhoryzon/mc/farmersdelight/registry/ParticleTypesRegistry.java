package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public enum ParticleTypesRegistry {
    STAR("star");

    private final String pathName;
    private DefaultParticleType particleType;

    ParticleTypesRegistry(String pathName) {
        this.pathName = pathName;
    }

    public static void registerAll() {
        for (ParticleTypesRegistry value : values()) {
            value.particleType = Registry.register(Registry.PARTICLE_TYPE, new Identifier(FarmersDelightMod.MOD_ID, value.pathName), FabricParticleTypes.simple());
        }
    }

    public DefaultParticleType get() {
        return particleType;
    }
}