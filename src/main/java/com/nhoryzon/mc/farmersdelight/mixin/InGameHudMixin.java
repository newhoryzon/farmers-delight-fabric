package com.nhoryzon.mc.farmersdelight.mixin;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.client.gui.ComfortHealthOverlay;
import com.nhoryzon.mc.farmersdelight.client.gui.NourishmentHungerOverlay;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=food")), at = @At(value = "com.nhoryzon.mc.farmersdelight.mixin.util.BeforeInc", args = "intValue=-10", ordinal = 0), method = "renderStatusBars(Lnet/minecraft/client/util/math/MatrixStack;)V")
    private void renderFoodPost(MatrixStack stack, CallbackInfo info) {
        if (NourishmentHungerOverlay.INSTANCE != null && FarmersDelightMod.CONFIG.isNourishedHungerOverlay()) {
            NourishmentHungerOverlay.INSTANCE.onRender(stack);
        }
        if (ComfortHealthOverlay.INSTANCE != null && FarmersDelightMod.CONFIG.isComfortHealthOverlay()) {
            ComfortHealthOverlay.INSTANCE.onRender(stack);
        }
    }

}
