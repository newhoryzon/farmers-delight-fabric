package com.nhoryzon.mc.farmersdelight.mixin;

import com.google.common.collect.Sets;
import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(CampfireBlock.class)
public class CampfireBaleMixin {

    @Inject(at = @At("HEAD"), method = "doesBlockCauseSignalFire", cancellable = true)
    public void doesBlockCauseSignalFire(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        Set<Block> hayBales = Sets.newHashSet(BlocksRegistry.STRAW_BALE.get(), BlocksRegistry.RICE_BALE.get());
        if (hayBales.contains(state.getBlock())) {
            cir.setReturnValue(true);
        }
    }

}
