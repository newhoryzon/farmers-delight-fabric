package com.nhoryzon.mc.farmersdelight.mixin;

import com.nhoryzon.mc.farmersdelight.block.signs.ICanvasSign;
import com.nhoryzon.mc.farmersdelight.client.render.block.CanvasSignBlockEntityRenderer;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.util.SpriteIdentifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SignEditScreen.class)
public class CanvasSignEditScreenMixin {

    @Shadow
    @Final
    private SignBlockEntity sign;

    @ModifyVariable(at = @At(value = "STORE"), method = "render", name = "spriteIdentifier")
    public SpriteIdentifier useCanvasSignSprite(SpriteIdentifier signSprite) {
        if (sign.getCachedState().getBlock() instanceof ICanvasSign canvasSign) {
            return CanvasSignBlockEntityRenderer.getCanvasSignSpriteTexture(canvasSign.getBackgroundColor());
        }

        return signSprite;
    }

}
