package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.entity.RottenTomatoEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityTypesRegistry {

    public static EntityType<RottenTomatoEntity> ROTTEN_TOMATO;

    public static void registerAll() {
        ROTTEN_TOMATO = Registry.register(
                Registry.ENTITY_TYPE, new Identifier(FarmersDelightMod.MOD_ID, "rotten_tomato"),
                FabricEntityTypeBuilder.<RottenTomatoEntity>create(SpawnGroup.MISC, RottenTomatoEntity::new)
                        .dimensions(EntityDimensions.fixed(.25f, .25f))
                        .trackRangeBlocks(4).trackedUpdateRate(10).build());
    }


}