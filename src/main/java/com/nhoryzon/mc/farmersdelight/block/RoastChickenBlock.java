package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.registry.ItemsRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class RoastChickenBlock extends FeastBlock {
    protected static final VoxelShape PLATE_SHAPE = Block.createCuboidShape(1.d, .0d, 1.d, 15.d, 2.d, 15.d);
    protected static final VoxelShape ROAST_SHAPE = VoxelShapes.combine(PLATE_SHAPE,
            Block.createCuboidShape(4.d, 2.d, 4.d, 12.d, 9.d, 12.d), BooleanBiFunction.OR);

    public RoastChickenBlock() {
        super(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL), ItemsRegistry.ROAST_CHICKEN.get(), true);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(SERVINGS) == 0 ? PLATE_SHAPE : ROAST_SHAPE;
    }
}