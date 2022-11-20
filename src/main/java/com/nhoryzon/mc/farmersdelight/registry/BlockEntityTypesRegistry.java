package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.entity.block.BasketBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.CanvasSignBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.CookingPotBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.CuttingBoardBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.CabinetBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.SkilletBlockEntity;
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
    SKILLET("skillet", SkilletBlockEntity.class, SkilletBlockEntity::new, BlocksRegistry.SKILLET),
    CABINET("cabinet", CabinetBlockEntity.class, CabinetBlockEntity::new, BlocksRegistry.OAK_CABINET, BlocksRegistry.BIRCH_CABINET,
            BlocksRegistry.SPRUCE_CABINET, BlocksRegistry.JUNGLE_CABINET, BlocksRegistry.ACACIA_CABINET, BlocksRegistry.DARK_OAK_CABINET,
            BlocksRegistry.MANGROVE_CABINET, BlocksRegistry.CRIMSON_CABINET, BlocksRegistry.WARPED_CABINET),
    CANVAS_SIGN("canvas_sign", CanvasSignBlockEntity.class, CanvasSignBlockEntity::new,
            BlocksRegistry.CANVAS_SIGN, BlocksRegistry.BLACK_CANVAS_SIGN, BlocksRegistry.BLUE_CANVAS_SIGN, BlocksRegistry.BROWN_CANVAS_SIGN,
            BlocksRegistry.CYAN_CANVAS_SIGN, BlocksRegistry.GRAY_CANVAS_SIGN, BlocksRegistry.GREEN_CANVAS_SIGN, BlocksRegistry.LIGHT_GRAY_CANVAS_SIGN,
            BlocksRegistry.LIGHT_BLUE_CANVAS_SIGN, BlocksRegistry.LIGHT_GRAY_CANVAS_SIGN, BlocksRegistry.LIME_CANVAS_SIGN, BlocksRegistry.MAGENTA_CANVAS_SIGN,
            BlocksRegistry.ORANGE_CANVAS_SIGN, BlocksRegistry.PINK_CANVAS_SIGN, BlocksRegistry.PURPLE_CANVAS_SIGN, BlocksRegistry.RED_CANVAS_SIGN,
            BlocksRegistry.WHITE_CANVAS_SIGN, BlocksRegistry.YELLOW_CANVAS_SIGN,
            BlocksRegistry.CANVAS_WALL_SIGN, BlocksRegistry.BLACK_CANVAS_WALL_SIGN, BlocksRegistry.BLUE_CANVAS_WALL_SIGN, BlocksRegistry.BROWN_CANVAS_WALL_SIGN,
            BlocksRegistry.CYAN_CANVAS_WALL_SIGN, BlocksRegistry.GRAY_CANVAS_WALL_SIGN, BlocksRegistry.GREEN_CANVAS_WALL_SIGN, BlocksRegistry.LIGHT_GRAY_CANVAS_WALL_SIGN,
            BlocksRegistry.LIGHT_BLUE_CANVAS_WALL_SIGN, BlocksRegistry.LIGHT_GRAY_CANVAS_WALL_SIGN, BlocksRegistry.LIME_CANVAS_WALL_SIGN, BlocksRegistry.MAGENTA_CANVAS_WALL_SIGN,
            BlocksRegistry.ORANGE_CANVAS_WALL_SIGN, BlocksRegistry.PINK_CANVAS_WALL_SIGN, BlocksRegistry.PURPLE_CANVAS_WALL_SIGN, BlocksRegistry.RED_CANVAS_WALL_SIGN,
            BlocksRegistry.WHITE_CANVAS_WALL_SIGN, BlocksRegistry.YELLOW_CANVAS_WALL_SIGN);

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

    public String getId() {
        return Registry.BLOCK_ENTITY_TYPE.getId(get()).toString();
    }

}