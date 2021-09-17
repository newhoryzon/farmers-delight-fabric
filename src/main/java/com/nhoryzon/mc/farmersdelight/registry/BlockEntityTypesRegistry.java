package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.entity.block.BasketBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.CookingPotBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.CuttingBoardBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.PantryBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.StoveBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.function.Supplier;

public enum BlockEntityTypesRegistry {
    STOVE("stove", StoveBlockEntity.class, StoveBlockEntity::new, BlocksRegistry.STOVE),
    COOKING_POT("cooking_pot", CookingPotBlockEntity.class, CookingPotBlockEntity::new, BlocksRegistry.COOKING_POT),
    BASKET("basket", BasketBlockEntity.class, BasketBlockEntity::new, BlocksRegistry.BASKET),
    CUTTING_BOARD("cutting_board", CuttingBoardBlockEntity.class, CuttingBoardBlockEntity::new, BlocksRegistry.CUTTING_BOARD),
    PANTRY("pantry", PantryBlockEntity.class, PantryBlockEntity::new, BlocksRegistry.OAK_PANTRY, BlocksRegistry.BIRCH_PANTRY,
            BlocksRegistry.SPRUCE_PANTRY, BlocksRegistry.JUNGLE_PANTRY, BlocksRegistry.ACACIA_PANTRY, BlocksRegistry.DARK_OAK_PANTRY);

    private final String pathName;
    private final Class<? extends BlockEntity> blockEntityClass;
    private final Supplier<BlockEntityType<? extends BlockEntity>> blockEntityTypeSupplier;
    private BlockEntityType<? extends BlockEntity> blockEntityType;

    BlockEntityTypesRegistry(String pathName, Class<? extends BlockEntity> blockEntityClass,
            FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity> blockEntitySupplier, BlocksRegistry... blockRegistryArray) {
        this.pathName = pathName;
        this.blockEntityClass = blockEntityClass;
        this.blockEntityTypeSupplier = () -> FabricBlockEntityTypeBuilder.create(blockEntitySupplier, Arrays.stream(blockRegistryArray)
                .map(BlocksRegistry::get).toArray(Block[]::new)).build(null);
    }

    public static void registerAll() {
        for (BlockEntityTypesRegistry value : values()) {
            Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(FarmersDelightMod.MOD_ID, value.pathName), value.get());
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityType<T> get() {
        return (BlockEntityType<T>) get(blockEntityClass);
    }

    @SuppressWarnings({"unchecked","unused"})
    private <T extends BlockEntity> BlockEntityType<T> get(Class<T> clazz) {
        if (blockEntityType == null) {
            blockEntityType = blockEntityTypeSupplier.get();
        }

        return (BlockEntityType<T>) blockEntityType;
    }
}