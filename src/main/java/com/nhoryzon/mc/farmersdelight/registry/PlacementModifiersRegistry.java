package com.nhoryzon.mc.farmersdelight.registry;

import com.mojang.serialization.Codec;
import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.world.placement.BiomeIsOverworldPlacementModifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public enum PlacementModifiersRegistry {

    BIOME_IS_OVERWORLD("biome_is_overworld", BiomeIsOverworldPlacementModifier.CODEC);

    private final String pathName;
    private final Codec<? extends PlacementModifier> codec;

    private PlacementModifierType<? extends PlacementModifier> type;

    PlacementModifiersRegistry(String pathName, Codec<? extends PlacementModifier> codec) {
        this.pathName = pathName;
        this.codec = codec;
    }

    public static void registerAll() {
        for (PlacementModifiersRegistry value : values()) {
            value.type = Registry.register(Registries.PLACEMENT_MODIFIER_TYPE, new Identifier(FarmersDelightMod.MOD_ID, value.pathName),
                    typeConvert(value.codec));
        }
    }

    private static <P extends PlacementModifier> PlacementModifierType<P> typeConvert(Codec<P> codec) {
        return () -> codec;
    }

    public PlacementModifierType<? extends PlacementModifier> type() {
        return type;
    }

}
