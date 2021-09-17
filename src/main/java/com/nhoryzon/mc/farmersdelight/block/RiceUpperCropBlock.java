package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import com.nhoryzon.mc.farmersdelight.registry.ItemsRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemConvertible;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class RiceUpperCropBlock extends CropBlock implements Fertilizable {
    public static final IntProperty RICE_AGE = Properties.AGE_3;
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.createCuboidShape(3.d, .0d, 3.d, 13.d, 8.d, 13.d),
            Block.createCuboidShape(3.d, .0d, 3.d, 13.d, 10.d, 13.d),
            Block.createCuboidShape(2.d, .0d, 2.d, 14.d, 12.d, 14.d),
            Block.createCuboidShape(1.d, .0d, 1.d, 15.d, 16.d, 15.d)
    };

    public RiceUpperCropBlock() {
        super(FabricBlockSettings.copyOf(Blocks.WHEAT));
    }

    @Override
    public IntProperty getAgeProperty() {
        return RICE_AGE;
    }

    @Override
    public int getMaxAge() {
        return 3;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(RICE_AGE);
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return ItemsRegistry.RICE.get();
    }

    @Override
    protected int getGrowthAmount(World world) {
        return MathHelper.nextInt(world.getRandom(), 1, 4);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return (world.getLightLevel(pos, 0) >= 8 || world.isSkyVisible(pos)) && world.getBlockState(pos.down()).isOf(BlocksRegistry.RICE_CROP.get());
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(BlocksRegistry.RICE_CROP.get());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE_BY_AGE[state.get(getAgeProperty())];
    }
}