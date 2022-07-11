package com.nhoryzon.mc.farmersdelight.mixin;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.registry.BlockEntityTypesRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;
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
@Mixin(BlockEntity.class)
public class MigrationBlockEntityMixin {

    private static final Map<String, Supplier<String>> MIGRATION_BLOCK_ENTITY = Map.of(
            "farmersdelight:pantry", BlockEntityTypesRegistry.CABINET::getId
    );

    @Inject(method = "createFromNbt", at = @At("HEAD"))
    private static void fromNbtMigration(BlockPos pos, BlockState state, NbtCompound nbt, CallbackInfoReturnable<BlockEntity> cir) {
        if (nbt.contains("id", NbtElement.STRING_TYPE)) {
            String id = nbt.getString("id");
            if (MIGRATION_BLOCK_ENTITY.containsKey(id)) {
                String newId = MIGRATION_BLOCK_ENTITY.get(id).get();
                nbt.putString("id", newId);
                FarmersDelightMod.LOGGER.info("BlockEntity successfully migrated from '{}' to '{}'.", id, newId);
            }
        }
    }

}
