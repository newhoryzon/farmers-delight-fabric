package com.nhoryzon.mc.farmersdelight.mixin;

import com.nhoryzon.mc.farmersdelight.block.signs.ICanvasSign;
import com.nhoryzon.mc.farmersdelight.client.render.block.CanvasSignBlockEntityRenderer;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.SignType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SignEditScreen.class)
public class CanvasSignEditScreenMixin {

    @Shadow
    @Final
    private SignBlockEntity sign;

    @Redirect(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/TexturedRenderLayers;getSignTextureId(Lnet/minecraft/util/SignType;)Lnet/minecraft/client/util/SpriteIdentifier;"))
    public SpriteIdentifier useCanvasSignSprite(SignType signType) {
        if (sign.getCachedState().getBlock() instanceof ICanvasSign canvasSign) {
            return CanvasSignBlockEntityRenderer.getCanvasSignSpriteTexture(canvasSign.getBackgroundColor());
        }

        return TexturedRenderLayers.getSignTextureId(signType);
    }

}
