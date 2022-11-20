package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.registry.ItemsRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.List;

public class RiceRollMedleyBlock extends FeastBlock {

    public static final int MAX_SERVING = 8;
    public static final IntProperty ROLL_SERVINGS = IntProperty.of("servings", 0, MAX_SERVING);

    protected static final VoxelShape PLATE_SHAPE = Block.createCuboidShape(1.d, .0d, 1.d, 15.d, 2.d, 15.d);
    protected static final VoxelShape FOOD_SHAPE = VoxelShapes.combine(
            PLATE_SHAPE, Block.createCuboidShape(2.d, 2.d, 2.d, 14.d, 4.d, 14.d), BooleanBiFunction.OR);

    public final List<ItemsRegistry> riceRollServings = List.of(
            ItemsRegistry.COD_ROLL,
            ItemsRegistry.COD_ROLL,
            ItemsRegistry.SALMON_ROLL,
            ItemsRegistry.SALMON_ROLL,
            ItemsRegistry.SALMON_ROLL,
            ItemsRegistry.KELP_ROLL_SLICE,
            ItemsRegistry.KELP_ROLL_SLICE,
            ItemsRegistry.KELP_ROLL_SLICE
    );

    public RiceRollMedleyBlock() {
        super(Settings.copy(Blocks.CAKE), ItemsRegistry.SALMON_ROLL.get(), true);
    }

    @Override
    public IntProperty getServingsProperty() {
        return ROLL_SERVINGS;
    }

    @Override
    public int getMaxServings() {
        return MAX_SERVING;
    }

    @Override
    public ItemStack getServingStack(BlockState state) {
        return new ItemStack(riceRollServings.get(state.get(getServingsProperty()) - 1).get());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(getServingsProperty()) == 0 ? PLATE_SHAPE : FOOD_SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, ROLL_SERVINGS);
    }

}
