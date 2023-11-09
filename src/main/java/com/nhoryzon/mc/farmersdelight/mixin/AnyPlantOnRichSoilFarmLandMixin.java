package com.nhoryzon.mc.farmersdelight.mixin;

import com.nhoryzon.mc.farmersdelight.registry.TagsRegistry;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PitcherCropBlock.class, CropBlock.class, AttachedStemBlock.class, StemBlock.class})
public class AnyPlantOnRichSoilFarmLandMixin {

    @Inject(at = @At("TAIL"), method = "canPlantOnTop", cancellable = true)
    private void pitcherCropBlockCanPlantOnTopOfRichSoil(BlockState floor, BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || floor.isIn(TagsRegistry.FARMLAND));
    }

}
