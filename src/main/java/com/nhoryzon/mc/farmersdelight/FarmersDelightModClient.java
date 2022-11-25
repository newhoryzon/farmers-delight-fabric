package com.nhoryzon.mc.farmersdelight;

import com.nhoryzon.mc.farmersdelight.client.gui.ComfortHealthOverlay;
import com.nhoryzon.mc.farmersdelight.client.gui.NourishmentHungerOverlay;
import com.nhoryzon.mc.farmersdelight.client.particle.StarParticle;
import com.nhoryzon.mc.farmersdelight.client.particle.SteamParticle;
import com.nhoryzon.mc.farmersdelight.client.render.block.CanvasSignBlockEntityRenderer;
import com.nhoryzon.mc.farmersdelight.client.render.block.CuttingBoardBlockEntityRenderer;
import com.nhoryzon.mc.farmersdelight.client.render.block.SkilletBlockEntityRenderer;
import com.nhoryzon.mc.farmersdelight.client.render.block.StoveBlockEntityRenderer;
import com.nhoryzon.mc.farmersdelight.client.screen.CookingPotScreen;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.slot.CookingPotBowlSlot;
import com.nhoryzon.mc.farmersdelight.registry.BlockEntityTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import com.nhoryzon.mc.farmersdelight.registry.EffectsRegistry;
import com.nhoryzon.mc.farmersdelight.registry.EntityTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.ExtendedScreenTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.ParticleTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.TagsRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value= EnvType.CLIENT)
public class FarmersDelightModClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		BlocksRegistry.registerRenderLayer();
		NourishmentHungerOverlay.init();
		ComfortHealthOverlay.init();

		// BlockEntityRenderer register
		BlockEntityRendererRegistry.register(BlockEntityTypesRegistry.STOVE.get(), StoveBlockEntityRenderer::new);
		BlockEntityRendererRegistry.register(BlockEntityTypesRegistry.CUTTING_BOARD.get(), CuttingBoardBlockEntityRenderer::new);
		BlockEntityRendererRegistry.register(BlockEntityTypesRegistry.CANVAS_SIGN.get(), CanvasSignBlockEntityRenderer::new);
		BlockEntityRendererRegistry.register(BlockEntityTypesRegistry.SKILLET.get(), SkilletBlockEntityRenderer::new);
		EntityRendererRegistry.register(EntityTypesRegistry.ROTTEN_TOMATO, FlyingItemEntityRenderer::new);

		ModelIdentifier skilletId = new ModelIdentifier(new Identifier(FarmersDelightMod.MOD_ID, "skillet"), "inventory");
		ModelIdentifier skilletCookingId = new ModelIdentifier(new Identifier(FarmersDelightMod.MOD_ID, "skillet_cooking"), "inventory");

		// Particle register
		ParticleFactoryRegistry.getInstance().register(ParticleTypesRegistry.STAR.get(), StarParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(ParticleTypesRegistry.STEAM.get(), SteamParticle.Factory::new);

		// Screen register
		HandledScreens.register(ExtendedScreenTypesRegistry.COOKING_POT.get(), CookingPotScreen::new);

		// Standalone textures register
		ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(
				(atlasTexture, registry) -> registry.register(CookingPotBowlSlot.EMPTY_CONTAINER_SLOT_BOWL));

		ClientSpriteRegistryCallback.event(TexturedRenderLayers.SIGNS_ATLAS_TEXTURE).register(
				(atlasTexture, registry) -> registry.register(CanvasSignBlockEntityRenderer.BLANK_CANVAS_SIGN_SPRITE.getTextureId()));
		CanvasSignBlockEntityRenderer.DYED_CANVAS_SIGN_SPRITES.forEach((dyeColor, spriteIdentifier) ->
			ClientSpriteRegistryCallback.event(TexturedRenderLayers.SIGNS_ATLAS_TEXTURE).register(
					(atlasTexture, registry) -> registry.register(spriteIdentifier.getTextureId())));

		ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
			if (FarmersDelightMod.CONFIG.isRabbitStewJumpBoost() && stack.isOf(Items.RABBIT_STEW)) {
				StatusEffect effect = StatusEffects.JUMP_BOOST;
				lines.add(Text.translatable("potion.withDuration", Text.translatable(effect.getTranslationKey()),
								StatusEffectUtil.durationToString(
										new StatusEffectInstance(effect, Configuration.DURATION_RABBIT_STEW_JUMP), 1))
						.formatted(effect.getCategory().getFormatting()));
			}

			if (FarmersDelightMod.CONFIG.isVanillaSoupExtraEffects() && stack.isIn(TagsRegistry.COMFORT_FOODS)) {
				StatusEffect effect = EffectsRegistry.COMFORT.get();
				lines.add(Text.translatable("potion.withDuration", Text.translatable(effect.getTranslationKey()),
								StatusEffectUtil.durationToString(
										new StatusEffectInstance(effect, Configuration.DURATION_VANILLA_SOUP), 1))
						.formatted(effect.getCategory().getFormatting()));
			}
		});
	}

}