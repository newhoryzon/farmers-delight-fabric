package com.nhoryzon.mc.farmersdelight.mixin.accessors;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(DispenserBlock.class)
public interface DispenserBehaviorsAccessorMixin {
    @Accessor("BEHAVIORS")
    static Map<Item, DispenserBehavior> getBehaviors() {
        throw new RuntimeException("Mixin did not apply");
    }
}
