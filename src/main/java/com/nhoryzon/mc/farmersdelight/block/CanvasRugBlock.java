package com.nhoryzon.mc.farmersdelight.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class CanvasRugBlock extends Block {

    protected static final VoxelShape SHAPE = createCuboidShape(0.d, 0.d, 0.d, 16.d, 1.d, 16.d);

    public CanvasRugBlock() {
        super(FabricBlockSettings.copyOf(Blocks.WHITE_CARPET).strength(.2f).sounds(BlockSoundGroup.GRASS));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

}
