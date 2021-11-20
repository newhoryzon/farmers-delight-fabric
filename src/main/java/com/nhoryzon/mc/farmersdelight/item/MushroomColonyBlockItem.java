package com.nhoryzon.mc.farmersdelight.item;

import com.nhoryzon.mc.farmersdelight.block.MushroomColonyBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import org.jetbrains.annotations.Nullable;

public class MushroomColonyBlockItem extends ModBlockItem {
    public MushroomColonyBlockItem(Block block) {
        super(block);
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(ItemPlacementContext context) {
        BlockState blockState = getBlock().getPlacementState(context);
        if (blockState != null) {
            BlockState matureState = blockState.with(MushroomColonyBlock.AGE, 3);
            return canPlace(context, matureState) ? matureState : null;
        }

        return null;
    }
}