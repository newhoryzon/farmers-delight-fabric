package com.nhoryzon.mc.farmersdelight.item;

import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RopeItem extends ModBlockItem {
    public RopeItem() {
        super(BlocksRegistry.ROPE.get());
    }

    @Nullable
    @Override
    public ItemPlacementContext getPlacementContext(ItemPlacementContext context) {
        BlockPos blockPos = context.getBlockPos();
        World world = context.getWorld();
        BlockState blockState = world.getBlockState(blockPos);
        Block block = getBlock();

        if (blockState.getBlock() != block) {
            return context;
        }

        Direction direction;
        if (context.shouldCancelInteraction()) {
            direction = context.getSide();
        } else {
            direction = Direction.DOWN;
        }

        int yOffset = 0;
        BlockPos.Mutable mutablePos = new BlockPos.Mutable(blockPos.getX(), blockPos.getY(), blockPos.getZ()).move(direction);

        while (yOffset < 256) {
            blockState = world.getBlockState(mutablePos);
            if (blockState.getBlock() != getBlock()) {
                if (blockState.canReplace(context)) {
                    return ItemPlacementContext.offset(context, mutablePos, direction);
                }
                break;
            }

            if (direction != Direction.DOWN) {
                return context;
            }

            mutablePos.move(direction);
            ++yOffset;
        }

        return null;
    }

    @Override
    protected boolean checkStatePlacement() {
        return false;
    }
}