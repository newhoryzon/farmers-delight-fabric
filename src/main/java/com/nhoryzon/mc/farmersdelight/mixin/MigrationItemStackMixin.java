package com.nhoryzon.mc.farmersdelight.mixin;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.registry.ItemsRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @deprecated Will be removed in the next minor release that aim the next minor Minecraft version (1.20).
 */
@Deprecated(forRemoval = true, since = "1.4.0")
@Mixin(ItemStack.class)
public class MigrationItemStackMixin {

    private static final Map<String, Supplier<String>> MIGRATION_ITEM = Map.of(
            "farmersdelight:oak_pantry", ItemsRegistry.OAK_CABINET::getId,
            "farmersdelight:acacia_pantry", ItemsRegistry.ACACIA_CABINET::getId,
            "farmersdelight:birch_pantry", ItemsRegistry.BIRCH_CABINET::getId,
            "farmersdelight:crimson_pantry", ItemsRegistry.CRIMSON_CABINET::getId,
            "farmersdelight:jungle_pantry", ItemsRegistry.JUNGLE_CABINET::getId,
            "farmersdelight:dark_oak_pantry", ItemsRegistry.DARK_OAK_CABINET::getId,
            "farmersdelight:spruce_pantry", ItemsRegistry.SPRUCE_CABINET::getId,
            "farmersdelight:warped_pantry", ItemsRegistry.WARPED_CABINET::getId
    );

    @Inject(method = "fromNbt", at = @At("HEAD"))
    private static void fromNbtMigration(NbtCompound nbt, CallbackInfoReturnable<ItemStack> cir) {
        if (nbt.contains("id", NbtElement.STRING_TYPE)) {
            String id = nbt.getString("id");
            if (MIGRATION_ITEM.containsKey(id)) {
                String newId = MIGRATION_ITEM.get(id).get();
                nbt.putString("id", newId);
                FarmersDelightMod.LOGGER.info("ItemStack successfully migrated from '{}' to '{}'.", id, newId);
            }
        }
    }

}
