package com.nhoryzon.mc.farmersdelight.mixin;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.client.gui.ComfortHealthOverlay;
import com.nhoryzon.mc.farmersdelight.client.gui.NourishmentHungerOverlay;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Inject(slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=food")), at = @At(value = "com.nhoryzon.mc.farmersdelight.mixin.util.BeforeInc", args = "intValue=-10", ordinal = 0), method = "renderStatusBars")
    private void renderFoodPost(DrawContext context, CallbackInfo ci) {
        if (NourishmentHungerOverlay.INSTANCE != null && FarmersDelightMod.CONFIG.isNourishedHungerOverlay()) {
            NourishmentHungerOverlay.INSTANCE.onRender(context);
        }
        if (ComfortHealthOverlay.INSTANCE != null && FarmersDelightMod.CONFIG.isComfortHealthOverlay()) {
            ComfortHealthOverlay.INSTANCE.onRender(context);
        }
    }

}
