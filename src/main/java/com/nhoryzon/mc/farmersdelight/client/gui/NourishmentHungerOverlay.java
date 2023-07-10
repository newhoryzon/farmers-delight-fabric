package com.nhoryzon.mc.farmersdelight.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.registry.EffectsRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
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

    public void onRender(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        boolean isMounted = mc.player != null && mc.player.getVehicle() instanceof LivingEntity;
        if (!isMounted && !mc.options.hudHidden) {
            renderNourishmentOverlay(context);
        }
    }

    private void renderNourishmentOverlay(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        if (player == null) {
            return;
        }

        HungerManager stats = player.getHungerManager();
        int top = mc.getWindow().getScaledHeight() - foodIconsOffset;
        int left = mc.getWindow().getScaledWidth() / 2 + 91;

        boolean isPlayerHealingWithSaturation = player.getWorld().getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)
                && player.canFoodHeal() && stats.getFoodLevel() >= 18;

        if (player.getStatusEffect(EffectsRegistry.NOURISHMENT.get()) != null) {
            drawNourishmentOverlay(stats, mc, context, left, top, isPlayerHealingWithSaturation);
        }
    }

    private void drawNourishmentOverlay(HungerManager stats, MinecraftClient mc, DrawContext context, int left, int top, boolean naturalHealing) {
        float saturation = stats.getSaturationLevel();
        int foodLevel = stats.getFoodLevel();
        int ticks = mc.inGameHud.getTicks();
        Random rand = new Random();
        rand.setSeed(ticks * 312871L);

        RenderSystem.enableBlend();

        for (int j = 0; j < 10; ++j) {
            int x = left - j * 8 - 9;
            int y = top;

            if (saturation <= 0.0F && ticks % (foodLevel * 3 + 1) == 0) {
                y = top + (rand.nextInt(3) - 1);
            }
            // Raised mod compat
            if (FabricLoader.getInstance().getObjectShare().get("raised:distance") instanceof Integer distance) {
                y -= distance;
            }

            // Background texture
            context.drawTexture(MOD_ICONS_TEXTURE, x, y, 0, 0, 9, 9);

            float effectiveHungerOfBar = (stats.getFoodLevel()) / 2.0F - j;
            int naturalHealingOffset = naturalHealing ? 18 : 0;

            // Gilded hunger icons
            if (effectiveHungerOfBar >= 1)
                context.drawTexture(MOD_ICONS_TEXTURE, x, y, 18 + naturalHealingOffset, 0, 9, 9);
            else if (effectiveHungerOfBar >= .5)
                context.drawTexture(MOD_ICONS_TEXTURE, x, y, 9 + naturalHealingOffset, 0, 9, 9);
        }

        RenderSystem.disableBlend();
    }

}
