package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.registry.ConfiguredFeaturesRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SandyShrubBlock extends PlantBlock implements Fertilizable {

    protected static final VoxelShape SHAPE = Block.createCuboidShape(2.d, .0d, 2.d, 14.d, 13.d, 14.d);

    public SandyShrubBlock() {
        super(Settings.copy(Blocks.TALL_GRASS));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(Blocks.SAND);
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        ConfiguredFeaturesRegistry.PATCH_SANDY_SHRUB_BONEMEAL.config().generate(world, world.getChunkManager().getChunkGenerator(), random, pos.up());
    }

}
