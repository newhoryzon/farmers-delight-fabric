package com.nhoryzon.mc.farmersdelight.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.registry.EffectsRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class ComfortHealthOverlay {

    public static ComfortHealthOverlay INSTANCE;

    private static final Identifier MOD_ICONS_TEXTURE = new Identifier(FarmersDelightMod.MOD_ID, "textures/gui/fd_icons.png");

    public static int healthIconsOffset = 39;

    public static void init() {
        INSTANCE = new ComfortHealthOverlay();
    }

    public void onRender(MatrixStack matrixStack) {
        MinecraftClient mc = MinecraftClient.getInstance();
        boolean isMounted = mc.player != null && mc.player.getVehicle() instanceof LivingEntity;
        if (!isMounted && !mc.options.hudHidden) {
            renderComfortOverlay(matrixStack);
        }
    }

    private void renderComfortOverlay(MatrixStack matrixStack) {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        if (player == null) {
            return;
        }

        HungerManager stats = player.getHungerManager();
        int top = mc.getWindow().getScaledHeight() - healthIconsOffset;
        int left = mc.getWindow().getScaledWidth() / 2 - 91;

        boolean isPlayerEligibleForComfort = stats.getSaturationLevel() == .0f && player.canFoodHeal()
                && !player.hasStatusEffect(StatusEffects.REGENERATION);

        if (player.getStatusEffect(EffectsRegistry.COMFORT.get()) != null && isPlayerEligibleForComfort) {
            drawComfortOverlay(player, mc, matrixStack, left, top);
        }
    }

    private void drawComfortOverlay(PlayerEntity player, MinecraftClient mc, MatrixStack matrixStack, int left, int top) {
        int ticks = mc.inGameHud.getTicks();
        Random rand = new Random();
        rand.setSeed(ticks * 312871L);

        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, MOD_ICONS_TEXTURE);

        int health = MathHelper.ceil(player.getHealth());
        float absorb = MathHelper.ceil(player.getAbsorptionAmount());
        EntityAttributeInstance attrMaxHealth = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (attrMaxHealth != null) {
            float healthMax = (float) attrMaxHealth.getValue();

            int regen = -1;
            if (player.hasStatusEffect(StatusEffects.REGENERATION)) regen = ticks % 25;

            int healthRows = MathHelper.ceil((healthMax + absorb) / 2.0F / 10.0F);
            int rowHeight = Math.max(10 - (healthRows - 2), 3);

            int comfortSheen = ticks % 50;
            int comfortHeartFrame = comfortSheen % 2;
            int[] textureWidth = {5, 9};

            RenderSystem.setShaderTexture(0, MOD_ICONS_TEXTURE);
            RenderSystem.enableBlend();

            int healthMaxSingleRow = MathHelper.ceil(Math.min(healthMax, 20) / 2.0F);
            int leftHeightOffset = ((healthRows - 1) * rowHeight); // This keeps the overlay on the bottommost row of hearts

            for (int i = 0; i < healthMaxSingleRow; ++i) {
                int column = i % 10;
                int x = left + column * 8;
                int y = top + leftHeightOffset;

                if (health <= 4) y += rand.nextInt(2);
                if (i == regen) y -= 2;

                if (column == comfortSheen / 2) {
                    mc.inGameHud.drawTexture(matrixStack, x, y, 0, 9, textureWidth[comfortHeartFrame], 9);
                }
                if (column == (comfortSheen / 2) - 1 && comfortHeartFrame == 0) {
                    mc.inGameHud.drawTexture(matrixStack, x + 5, y, 5, 9, 4, 9);
                }
            }
        }

        RenderSystem.disableBlend();
        RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
    }

}
