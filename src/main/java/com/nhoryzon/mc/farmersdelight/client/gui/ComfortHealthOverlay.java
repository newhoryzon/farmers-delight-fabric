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
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class ComfortHealthOverlay {

    public static ComfortHealthOverlay INSTANCE;

    private static final Identifier MOD_ICONS_TEXTURE = new Identifier(FarmersDelightMod.MOD_ID, "textures/gui/fd_icons.png");

    public static int healthIconsOffset = 39;

    public static void init() {
        INSTANCE = new ComfortHealthOverlay();
    }

    public void onRender(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        boolean isMounted = mc.player != null && mc.player.getVehicle() instanceof LivingEntity;
        if (!isMounted && !mc.options.hudHidden) {
            renderComfortOverlay(context);
        }
    }

    private void renderComfortOverlay(DrawContext context) {
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
            drawComfortOverlay(player, mc, context, left, top);
        }
    }

    private void drawComfortOverlay(PlayerEntity player, MinecraftClient mc, DrawContext context, int left, int top) {
        int ticks = mc.inGameHud.getTicks();
        Random rand = new Random();
        rand.setSeed(ticks * 312871L);

        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, MOD_ICONS_TEXTURE);

        int health = MathHelper.ceil(player.getHealth());
        EntityAttributeInstance attrMaxHealth = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (attrMaxHealth != null) {
            float healthMax = (float) attrMaxHealth.getValue();

            int regen = -1;
            if (player.hasStatusEffect(StatusEffects.REGENERATION)) regen = ticks % 25;

            int comfortSheen = ticks % 50;
            int comfortHeartFrame = comfortSheen % 2;
            int[] textureWidth = {5, 9};

            RenderSystem.setShaderTexture(0, MOD_ICONS_TEXTURE);
            RenderSystem.enableBlend();

            int healthMaxSingleRow = MathHelper.ceil(Math.min(healthMax, 20) / 2.0F);

            for (int i = 0; i < healthMaxSingleRow; ++i) {
                int column = i % 10;
                int x = left + column * 8;
                int y = top;
                // Raised mod compat
                if (FabricLoader.getInstance().getObjectShare().get("raised:distance") instanceof Integer distance) {
                    y -= distance;
                }

                if (health <= 4) y += rand.nextInt(2);
                if (i == regen) y -= 2;

                if (column == comfortSheen / 2) {
                    context.drawTexture(MOD_ICONS_TEXTURE, x, y, 0, 9, textureWidth[comfortHeartFrame], 9);
                }
                if (column == (comfortSheen / 2) - 1 && comfortHeartFrame == 0) {
                    context.drawTexture(MOD_ICONS_TEXTURE, x + 5, y, 5, 9, 4, 9);
                }
            }
        }

        RenderSystem.disableBlend();
    }

}
