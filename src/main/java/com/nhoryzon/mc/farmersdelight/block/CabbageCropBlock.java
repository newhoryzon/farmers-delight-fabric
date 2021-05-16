package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.registry.ItemsRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class CabbageCropBlock extends CropBlock {
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[] {
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 2.d, 16.d),
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 3.d, 16.d),
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 5.d, 16.d),
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 7.d, 16.d),
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 8.d, 16.d),
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 9.d, 16.d),
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 9.d, 16.d),
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 10.d, 16.d)
    };

    public CabbageCropBlock() {
        super(FabricBlockSettings.copyOf(Blocks.WHEAT));
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return ItemsRegistry.CABBAGE_SEEDS.get();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE_BY_AGE[state.get(getAgeProperty())];
    }
}