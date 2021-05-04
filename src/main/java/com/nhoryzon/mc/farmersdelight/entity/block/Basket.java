package com.nhoryzon.mc.farmersdelight.entity.block;

import net.minecraft.block.Block;
import net.minecraft.block.entity.Hopper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

public interface Basket extends Hopper {

    VoxelShape[] COLLECTION_AREA_SHAPES = {
            Block.createCuboidShape(.0d, -16.d, .0d, 16.d, 16.d, 16.d),    // down
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 32.d, 16.d),       // up
            Block.createCuboidShape(.0d, .0d, -16.d, 16.d, 16.d, 16.d),     // north
            Block.createCuboidShape(.0d, .0d, .0d, 16.d, 16.d, 32.d),       // south
            Block.createCuboidShape(-16.d, .0d, .0d, 16.d, 16.d, 16.d),     // west
            Block.createCuboidShape(.0d, .0d, .0d, 32.d, 16.d, 16.d)        // east
    };

    default VoxelShape getFacingCollectionArea(int facingIndex) {
        return COLLECTION_AREA_SHAPES[facingIndex];
    }

    /**
     * Returns the worldObj for this tileEntity.
     */
    @Override
    World getWorld();

    /**
     * Gets the world X position for this hopper entity.
     */
    @Override
    double getHopperX();

    /**
     * Gets the world Y position for this hopper entity.
     */
    @Override
    double getHopperY();

    /**
     * Gets the world Z position for this hopper entity.
     */
    @Override
    double getHopperZ();

}
