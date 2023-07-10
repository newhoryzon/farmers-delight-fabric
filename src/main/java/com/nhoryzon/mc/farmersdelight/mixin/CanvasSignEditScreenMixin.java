package com.nhoryzon.mc.farmersdelight.mixin;

import com.nhoryzon.mc.farmersdelight.block.signs.ICanvasSign;
import com.nhoryzon.mc.farmersdelight.client.render.block.CanvasSignBlockEntityRenderer;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SignEditScreen.class)
public abstract class CanvasSignEditScreenMixin extends AbstractSignEditScreen {

    public CanvasSignEditScreenMixin(SignBlockEntity blockEntity, boolean filtered) {
        super(blockEntity, filtered);
    }

    @Redirect(method = "renderSignBackground", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/TexturedRenderLayers;getSignTextureId(Lnet/minecraft/block/WoodType;)Lnet/minecraft/client/util/SpriteIdentifier;"))
    public SpriteIdentifier useCanvasSignSprite(WoodType woodType) {
        if (blockEntity.getCachedState().getBlock() instanceof ICanvasSign canvasSign) {
            return CanvasSignBlockEntityRenderer.getCanvasSignSpriteTexture(canvasSign.getBackgroundColor());
        }

        return TexturedRenderLayers.getSignTextureId(woodType);
    }

}
