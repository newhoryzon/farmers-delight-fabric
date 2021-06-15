package com.nhoryzon.mc.farmersdelight.mixin.accessors;

import net.minecraft.entity.player.HungerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HungerManager.class)
public interface PlayerExhaustionAccessorMixin {
    @Accessor
    float getExhaustion();
}