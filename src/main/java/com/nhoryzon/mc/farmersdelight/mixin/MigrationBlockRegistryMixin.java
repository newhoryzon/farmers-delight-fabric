package com.nhoryzon.mc.farmersdelight.mixin;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @deprecated Will be removed in the next minor release that aim the next minor Minecraft version (1.20).
 */
@Deprecated(forRemoval = true, since = "1.4.0")
@Mixin(DefaultedRegistry.class)
public abstract class MigrationBlockRegistryMixin {

    private static final Map<String, Supplier<String>> MIGRATION_BLOCK = Map.of(
            "farmersdelight:oak_pantry", BlocksRegistry.OAK_CABINET::getId,
            "farmersdelight:acacia_pantry", BlocksRegistry.ACACIA_CABINET::getId,
            "farmersdelight:birch_pantry", BlocksRegistry.BIRCH_CABINET::getId,
            "farmersdelight:crimson_pantry", BlocksRegistry.CRIMSON_CABINET::getId,
            "farmersdelight:jungle_pantry", BlocksRegistry.JUNGLE_CABINET::getId,
            "farmersdelight:dark_oak_pantry", BlocksRegistry.DARK_OAK_CABINET::getId,
            "farmersdelight:spruce_pantry", BlocksRegistry.SPRUCE_CABINET::getId,
            "farmersdelight:warped_pantry", BlocksRegistry.WARPED_CABINET::getId
    );

    @ModifyVariable(method = "get(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;", at = @At(value = "HEAD"), argsOnly = true)
    private Identifier migrateGet(Identifier value) {
        if ((Object)this == Registry.BLOCK) {
            String fullId = value.toString();
            if (MIGRATION_BLOCK.containsKey(fullId)) {
                String newFullId = MIGRATION_BLOCK.get(fullId).get();
                FarmersDelightMod.LOGGER.info("Block identifier successfully migrated from '{}' to '{}'.", fullId, newFullId);

                return new Identifier(newFullId);
            }
        }

        return value;
    }

}
