package com.nhoryzon.mc.farmersdelight.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.registry.EffectsRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

import java.util.Random;

/**
 * Credits to squeek502 (AppleSkin) for the implementation reference!
 * https://www.curseforge.com/minecraft/mc-mods/appleskin
 */

@Environment(EnvType.CLIENT)
public class NourishedHungerOverlay
{
	protected static int foodIconsOffset;
	public static int FOOD_BAR_HEIGHT = 39;
	private static final Identifier MOD_ICONS_TEXTURE = new Identifier(FarmersDelightMod.MOD_ID, "textures/gui/nourished.png");

	public static void onPreRender(MatrixStack matrixStack) {
		foodIconsOffset = FOOD_BAR_HEIGHT;
	}

	public static void onRender(MatrixStack matrixStack) {
		MinecraftClient mc = MinecraftClient.getInstance();
		PlayerEntity player = mc.player;
		HungerManager stats = player.getHungerManager();

		int left = mc.getWindow().getScaledWidth() / 2 + 91;
		int top = mc.getWindow().getScaledHeight() - foodIconsOffset;

		boolean isPlayerHealingWithSaturation =
				player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)
						&& player.canFoodHeal()
						&& stats.getSaturationLevel() > 0.0F
						&& stats.getFoodLevel() >= 20;

		if (player.getStatusEffect(EffectsRegistry.NOURISHED.get()) != null) {
			drawNourishedOverlay(stats, mc, matrixStack, left, top, isPlayerHealingWithSaturation);
		}
	}

	public static void drawNourishedOverlay(HungerManager stats, MinecraftClient mc, MatrixStack matrixStack, int left, int top, boolean naturalHealing) {
		matrixStack.push();
		matrixStack.translate(0, 0, 0.01);

		float saturation = stats.getSaturationLevel();
		int foodLevel = stats.getFoodLevel();
		int ticks = mc.inGameHud.getTicks();
		Random rand = new Random();
		rand.setSeed((long)(ticks * 312871));

		mc.getTextureManager().bindTexture(MOD_ICONS_TEXTURE);
		RenderSystem.enableBlend();

		for (int j = 0; j < 10; ++j) {
			int x = left - j * 8 - 9;
			int y = top;

			if (saturation <= 0.0F && ticks % (foodLevel * 3 + 1) == 0) {
				y = top + (rand.nextInt(3) - 1);
			}

			// Background texture
			mc.inGameHud.drawTexture(matrixStack, x, y, 0, 0, 11, 11);

			float effectiveHungerOfBar = (stats.getFoodLevel()) / 2.0F - j;
			int naturalHealingOffset = naturalHealing ? 18 : 0;

			// Gilded hunger icons
			if (effectiveHungerOfBar >= 1)
				mc.inGameHud.drawTexture(matrixStack, x, y, 18 + naturalHealingOffset, 0, 9, 9);
			else if (effectiveHungerOfBar >= .5)
				mc.inGameHud.drawTexture(matrixStack, x, y, 9 + naturalHealingOffset, 0, 9, 9);
		}

		RenderSystem.disableBlend();
		matrixStack.pop();
		mc.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
	}
}