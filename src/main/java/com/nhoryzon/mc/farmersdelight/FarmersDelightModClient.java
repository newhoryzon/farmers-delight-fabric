package com.nhoryzon.mc.farmersdelight;

import com.nhoryzon.mc.farmersdelight.client.particle.StarParticle;
import com.nhoryzon.mc.farmersdelight.client.render.block.CuttingBoardBlockEntityRenderer;
import com.nhoryzon.mc.farmersdelight.client.render.block.StoveBlockEntityRenderer;
import com.nhoryzon.mc.farmersdelight.client.screen.CookingPotScreen;
import com.nhoryzon.mc.farmersdelight.entity.block.screen.CookingPotScreenHandler;
import com.nhoryzon.mc.farmersdelight.registry.BlockEntityTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import com.nhoryzon.mc.farmersdelight.registry.ExtendedScreenTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.ParticleTypesRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.screen.PlayerScreenHandler;

@Environment(value= EnvType.CLIENT)
public class FarmersDelightModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlocksRegistry.registerRenderLayer();

		// BlockEntityRenderer register
		BlockEntityRendererRegistry.register(BlockEntityTypesRegistry.STOVE.get(), StoveBlockEntityRenderer::new);
		BlockEntityRendererRegistry.register(BlockEntityTypesRegistry.CUTTING_BOARD.get(), CuttingBoardBlockEntityRenderer::new);

		// Particle register
		ParticleFactoryRegistry.getInstance().register(ParticleTypesRegistry.STAR.get(), StarParticle.Factory::new);

		// Screen register
		ScreenRegistry.register(ExtendedScreenTypesRegistry.COOKING_POT.get(), CookingPotScreen::new);

		// Standalone textures register
		ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(
				(atlasTexture, registry) -> registry.register(CookingPotScreenHandler.EMPTY_CONTAINER_SLOT_BOWL));
	}
}