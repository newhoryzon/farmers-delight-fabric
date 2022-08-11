package com.nhoryzon.mc.farmersdelight.entity.block;

import com.nhoryzon.mc.farmersdelight.registry.TagsRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface HeatableBlockEntity {

    default boolean isHeated(World world, BlockPos pos) {
        BlockState stateBelow = world.getBlockState(pos.down());

        if (stateBelow.isIn(TagsRegistry.HEAT_SOURCES)) {
            if (stateBelow.contains(Properties.LIT)) {
                return stateBelow.get(Properties.LIT);
            }
            return true;
        }

        if (!requiresDirectHeat() && stateBelow.isIn(TagsRegistry.HEAT_CONDUCTORS)) {
            BlockState stateFurtherBelow = world.getBlockState(pos.down(2));
            if (stateFurtherBelow.isIn(TagsRegistry.HEAT_SOURCES)) {
                if (stateFurtherBelow.contains(Properties.LIT)) {
                    return stateFurtherBelow.get(Properties.LIT);
                }
                return true;
            }
        }

        return false;
    }

    default boolean requiresDirectHeat() {
        return false;
    }

}
