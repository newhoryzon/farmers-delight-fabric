package com.nhoryzon.mc.farmersdelight.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.registry.EffectsRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class NourishmentHungerOverlay {

    public static NourishmentHungerOverlay INSTANCE;

    private static final Identifier MOD_ICONS_TEXTURE = new Identifier(FarmersDelightMod.MOD_ID, "textures/gui/fd_icons.png");

    public static int foodIconsOffset = 39;

    public static void init() {
        INSTANCE = new NourishmentHungerOverlay();
    }

    public void onRender(MatrixStack matrixStack) {
        MinecraftClient mc = MinecraftClient.getInstance();
        boolean isMounted = mc.player != null && mc.player.getVehicle() instanceof LivingEntity;
        if (!isMounted && !mc.options.hudHidden) {
            renderNourishmentOverlay(matrixStack);
        }
    }

    private void renderNourishmentOverlay(MatrixStack matrixStack) {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        if (player == null) {
            return;
        }

        HungerManager stats = player.getHungerManager();
        int top = mc.getWindow().getScaledHeight() - foodIconsOffset;
        int left = mc.getWindow().getScaledWidth() / 2 + 91;

        boolean isPlayerHealingWithSaturation = player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)
                && player.canFoodHeal() && stats.getFoodLevel() >= 18;

        if (player.getStatusEffect(EffectsRegistry.NOURISHMENT.get()) != null) {
            drawNourishmentOverlay(stats, mc, matrixStack, left, top, isPlayerHealingWithSaturation);
        }
    }

    private void drawNourishmentOverlay(HungerManager stats, MinecraftClient mc, MatrixStack matrixStack, int left, int top, boolean naturalHealing) {
        float saturation = stats.getSaturationLevel();
        int foodLevel = stats.getFoodLevel();
        int ticks = mc.inGameHud.getTicks();
        Random rand = new Random();
        rand.setSeed(ticks * 312871L);

        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, MOD_ICONS_TEXTURE);

        for (int j = 0; j < 10; ++j) {
            int x = left - j * 8 - 9;
            int y = top;

            if (saturation <= 0.0F && ticks % (foodLevel * 3 + 1) == 0) {
                y = top + (rand.nextInt(3) - 1);
            }

            // Background texture
            mc.inGameHud.drawTexture(matrixStack, x, y, 0, 0, 9, 9);

            float effectiveHungerOfBar = (stats.getFoodLevel()) / 2.0F - j;
            int naturalHealingOffset = naturalHealing ? 18 : 0;

            // Gilded hunger icons
            if (effectiveHungerOfBar >= 1)
                mc.inGameHud.drawTexture(matrixStack, x, y, 18 + naturalHealingOffset, 0, 9, 9);
            else if (effectiveHungerOfBar >= .5)
                mc.inGameHud.drawTexture(matrixStack, x, y, 9 + naturalHealingOffset, 0, 9, 9);
        }

        RenderSystem.disableBlend();
        RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
    }

}
