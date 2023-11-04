package com.nhoryzon.mc.farmersdelight.client.render.block;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.block.signs.ICanvasSign;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HangingCanvasSignBlockEntityRenderer extends CanvasSignBlockEntityRenderer {
    public static final SpriteIdentifier BLANK_CANVAS_SIGN_SPRITE = new SpriteIdentifier(TexturedRenderLayers.SIGNS_ATLAS_TEXTURE,
            new Identifier(FarmersDelightMod.MOD_ID, "entity/signs/hanging/canvas"));
    public static final Map<DyeColor, SpriteIdentifier> DYED_CANVAS_SIGN_SPRITES = Arrays.stream(DyeColor.values()).collect(
            Collectors.toMap(Function.identity(), HangingCanvasSignBlockEntityRenderer::createSignSpriteIdentifier));

    private static final Vec3d TEXT_OFFSET = new Vec3d(0.0D, -0.32F, 0.073F);

    private final HangingSignBlockEntityRenderer.HangingSignModel signModel;

    public HangingCanvasSignBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
        this.signModel = new HangingSignBlockEntityRenderer.HangingSignModel(context.getLayerModelPart(EntityModelLayers.createHangingSign(WoodType.SPRUCE)));
    }

    @Override
    public float getSignScale() {
        return 1.0F;
    }

    @Override
    public float getTextScale() {
        return 0.8f;
    }

    @Override
    public void render(SignBlockEntity signBlockEntity, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int packedLight, int packedOverlay) {
        BlockState state = signBlockEntity.getCachedState();
        AbstractSignBlock block = (AbstractSignBlock) state.getBlock();
        HangingSignBlockEntityRenderer.HangingSignModel model = this.signModel;
        model.updateVisibleParts(state);

        DyeColor dye = null;
        if (block instanceof ICanvasSign canvasSign) {
            dye = canvasSign.getBackgroundColor();
        }
        this.renderSignWithText(signBlockEntity, matrixStack, vertexConsumerProvider, packedLight, packedOverlay, state, block, dye, model);
    }

    @Override
    protected void renderSign(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int packedLight, int packedOverlay, DyeColor dye, Model model) {
        matrixStack.push();
        float rootScale = getSignScale();
        matrixStack.scale(rootScale, -rootScale, -rootScale);
        SpriteIdentifier spriteIdentifier = HangingCanvasSignBlockEntityRenderer.getCanvasSignSpriteTexture(dye);
        Objects.requireNonNull(signModel);
        VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumerProvider, model::getLayer);
        signModel.root.render(matrixStack, vertexConsumer, packedLight, packedOverlay);
        matrixStack.pop();
    }

    @Override
    protected void translateSign(MatrixStack matrixStack, float angle, BlockState blockState) {
        matrixStack.translate(0.5D, 0.9375D, 0.5D);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle));
        matrixStack.translate(0.0f, -0.3125f, 0.0f);
    }

    @Override
    public void renderSignModel(MatrixStack matrices, int light, int overlay, Model model, VertexConsumer vertexConsumers) {
        HangingSignBlockEntityRenderer.HangingSignModel hangingSignModel = (HangingSignBlockEntityRenderer.HangingSignModel) model;
        hangingSignModel.root.render(matrices, vertexConsumers, light, overlay);
    }

    @Override
    public int getCustomVerticalOffset() {
        return 0;
    }

    @Override
    public Vec3d getTextOffset() {
        return TEXT_OFFSET;
    }

    public static SpriteIdentifier getCanvasSignSpriteTexture(DyeColor dyeColor) {
        return dyeColor != null ? DYED_CANVAS_SIGN_SPRITES.get(dyeColor) : BLANK_CANVAS_SIGN_SPRITE;
    }

    public static SpriteIdentifier createSignSpriteIdentifier(DyeColor dyeColor) {
        return new SpriteIdentifier(TexturedRenderLayers.SIGNS_ATLAS_TEXTURE,
                new Identifier(FarmersDelightMod.MOD_ID, "entity/signs/hanging/canvas_" + dyeColor.getName()));
    }
}
