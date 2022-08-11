package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.client.particle.StarParticle;
import com.nhoryzon.mc.farmersdelight.client.particle.SteamParticle;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public enum ParticleTypesRegistry {
    STAR("star", StarParticle.Factory::new),
    STEAM("steam", SteamParticle.Factory::new);

    private final String pathName;
    private final ParticleFactoryRegistry.PendingParticleFactory<DefaultParticleType> pendingParticleFactory;
    private DefaultParticleType particleType;

    ParticleTypesRegistry(String pathName, ParticleFactoryRegistry.PendingParticleFactory<DefaultParticleType> pendingParticleFactory) {
        this.pathName = pathName;
        this.pendingParticleFactory = pendingParticleFactory;
    }

    public static void registerAll() {
        for (ParticleTypesRegistry value : values()) {
            value.particleType = Registry.register(Registry.PARTICLE_TYPE, new Identifier(FarmersDelightMod.MOD_ID, value.pathName), FabricParticleTypes.simple());
        }
    }

    public static void registerAllClient() {
        for (ParticleTypesRegistry value : values()) {
            ParticleFactoryRegistry.getInstance().register(value.get(), value.pendingParticleFactory);
        }
    }

    public DefaultParticleType get() {
        return particleType;
    }
}