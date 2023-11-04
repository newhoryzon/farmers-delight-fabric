package com.nhoryzon.mc.farmersdelight.entity.block;

import com.nhoryzon.mc.farmersdelight.registry.BlockEntityTypesRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HangingSignBlockEntity;
import net.minecraft.util.math.BlockPos;

public class HangingCanvasSignBlockEntity extends HangingSignBlockEntity {
    public HangingCanvasSignBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
    }

    @Override
    public BlockEntityType<?> getType() {
        return BlockEntityTypesRegistry.HANGING_CANVAS_SIGN.get();
    }

    @Override
    public int getTextLineHeight() {
        return 9;
    }

    @Override
    public int getMaxTextWidth() {
        return 60;
    }
}
