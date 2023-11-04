package com.nhoryzon.mc.farmersdelight.mixin;

import com.nhoryzon.mc.farmersdelight.block.signs.ICanvasSign;
import com.nhoryzon.mc.farmersdelight.client.render.block.CanvasSignBlockEntityRenderer;
import com.nhoryzon.mc.farmersdelight.client.screen.CanvasSignEditScreen;
import com.nhoryzon.mc.farmersdelight.client.screen.HangingCanvasSignEditScreen;
import com.nhoryzon.mc.farmersdelight.entity.block.CanvasSignBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.HangingCanvasSignBlockEntity;
import com.nhoryzon.mc.farmersdelight.mixin.accessors.AbstractSignEditScreenAccessorMixin;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class CanvasSignEditScreenMixin {

    @Shadow @Final protected MinecraftClient client;

    @Inject(at = @At("HEAD"), method = "openEditSignScreen", cancellable = true)
    private void openScreen(SignBlockEntity sign, boolean front, CallbackInfo ci) {
        if (sign instanceof CanvasSignBlockEntity) {
            client.setScreen(new CanvasSignEditScreen(sign, front, client.shouldFilterText()));
            ci.cancel();
        }
        if (sign instanceof HangingCanvasSignBlockEntity) {
            client.setScreen(new HangingCanvasSignEditScreen(sign, front, client.shouldFilterText()));
            ci.cancel();
        }
    }
}
