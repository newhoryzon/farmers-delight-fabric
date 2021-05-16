package com.nhoryzon.mc.farmersdelight.mixin;

import com.nhoryzon.mc.farmersdelight.client.gui.NourishedHungerOverlay;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin
{
    @Inject(at = @At(value = "CONSTANT", args = "stringValue=food", shift = At.Shift.BY, by = 2), method = "renderStatusBars(Lnet/minecraft/client/util/math/MatrixStack;)V")
    private void renderFoodPre(MatrixStack stack, CallbackInfo info)
    {
        NourishedHungerOverlay.onPreRender(stack);
    }

    @Inject(slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=food")), at = @At(value = "FD_IINC", args = "intValue=-10", ordinal = 0), method = "renderStatusBars(Lnet/minecraft/client/util/math/MatrixStack;)V")
    private void renderFoodPost(MatrixStack stack, CallbackInfo info)
    {
        NourishedHungerOverlay.onRender(stack);
    }
}