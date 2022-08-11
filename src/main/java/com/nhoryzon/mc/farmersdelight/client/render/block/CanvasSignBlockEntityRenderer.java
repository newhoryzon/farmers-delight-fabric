package com.nhoryzon.mc.farmersdelight.client.render.block;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.block.signs.ICanvasSign;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.OrderedText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.SignType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Environment(value= EnvType.CLIENT)
public class CanvasSignBlockEntityRenderer extends SignBlockEntityRenderer {

    public static final SpriteIdentifier BLANK_CANVAS_SIGN_SPRITE = new SpriteIdentifier(TexturedRenderLayers.SIGNS_ATLAS_TEXTURE,
            new Identifier(FarmersDelightMod.MOD_ID, "entity/signs/canvas"));
    public static final Map<DyeColor, SpriteIdentifier> DYED_CANVAS_SIGN_SPRITES = Arrays.stream(DyeColor.values()).collect(
            Collectors.toMap(Function.identity(), CanvasSignBlockEntityRenderer::createSignSpriteIdentifier));

    public static final float TEXT_LINE_HEIGHT = 10;
    public static final float TEXT_VERTICAL_OFFSET = 19;
    private static final int RENDER_DISTANCE = MathHelper.square(16);

    private final SignModel signModel;
    private final TextRenderer textRenderer;

    public CanvasSignBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context);

        signModel = new SignModel(context.getLayerModelPart(EntityModelLayers.createSign(SignType.SPRUCE)));
        textRenderer = context.getTextRenderer();
    }

    @Override
    public void render(SignBlockEntity signBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
            int packedLight, int packedOverlay) {
        BlockState blockState = signBlockEntity.getCachedState();
        matrixStack.push();
        float h;
        if (blockState.getBlock() instanceof SignBlock) {
            matrixStack.translate(0.5, 0.5, 0.5);
            h = -((float)(blockState.get(SignBlock.ROTATION) * 360) / 16.0F);
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(h));
            signModel.stick.visible = true;
        } else {
            matrixStack.translate(0.5, 0.5, 0.5);
            h = -(blockState.get(WallSignBlock.FACING)).asRotation();
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(h));
            matrixStack.translate(0.0, -0.3125, -0.4375);
            signModel.stick.visible = false;
        }

        matrixStack.push();
        float rootScale = 0.6666667F;
        matrixStack.scale(rootScale, -rootScale, -rootScale);
        DyeColor dye = null;
        if (blockState.getBlock() instanceof ICanvasSign canvasSign) {
            dye = canvasSign.getBackgroundColor();
        }
        SpriteIdentifier spriteIdentifier = getCanvasSignSpriteTexture(dye);
        Objects.requireNonNull(signModel);
        VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumerProvider, signModel::getLayer);
        signModel.root.render(matrixStack, vertexConsumer, packedLight, packedOverlay);
        matrixStack.pop();
        float textScale = 0.010416667F;
        matrixStack.translate(0.0, 0.3333333432674408, 0.046666666865348816);
        matrixStack.scale(textScale, -textScale, textScale);
        OrderedText[] orderedTexts = signBlockEntity.updateSign(MinecraftClient.getInstance().shouldFilterText(), (text) -> {
            List<OrderedText> list = this.textRenderer.wrapLines(text, 90);
            return list.isEmpty() ? OrderedText.EMPTY : list.get(0);
        });

        int darkColor;
        int baseColor;
        boolean hasOutline;
        int light;
        if (signBlockEntity.isGlowingText()) {
            darkColor = getColor(signBlockEntity, true);
            baseColor = signBlockEntity.getTextColor().getSignColor();
            hasOutline = shouldRender(signBlockEntity, baseColor);
            light = 15728880;
        } else {
            darkColor = getColor(signBlockEntity, false);
            baseColor = darkColor;
            hasOutline = false;
            light = packedLight;
        }

        for(int p = 0; p < 4; ++p) {
            OrderedText orderedText = orderedTexts[p];
            float x = (float)(-this.textRenderer.getWidth(orderedText) / 2);
            float y = (float)p * TEXT_LINE_HEIGHT - TEXT_VERTICAL_OFFSET;
            if (hasOutline) {
                this.textRenderer.drawWithOutline(orderedText, x, y, baseColor, darkColor, matrixStack.peek().getPositionMatrix(),
                        vertexConsumerProvider, light);
            } else {
                this.textRenderer.draw(orderedText, x, y, baseColor, false, matrixStack.peek().getPositionMatrix(),
                        vertexConsumerProvider, false, 0, light);
            }
        }

        matrixStack.pop();
    }

    private static boolean shouldRender(SignBlockEntity sign, int textColor) {
        if (textColor == DyeColor.BLACK.getSignColor()) {
            return true;
        } else {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
            if (clientPlayerEntity != null && minecraftClient.options.getPerspective().isFirstPerson() && clientPlayerEntity.isUsingSpyglass()) {
                return true;
            } else {
                Entity entity = minecraftClient.getCameraEntity();
                return entity != null && entity.squaredDistanceTo(Vec3d.ofCenter(sign.getPos())) < (double)RENDER_DISTANCE;
            }
        }
    }

    private static int getColor(SignBlockEntity sign, boolean isOutlineVisible) {
        int textColor = sign.getTextColor().getSignColor();
        double brightness = isOutlineVisible ? .4d : .6d;
        int red = (int)((double) NativeImage.getRed(textColor) * brightness);
        int green = (int)((double)NativeImage.getGreen(textColor) * brightness);
        int blue = (int)((double)NativeImage.getBlue(textColor) * brightness);
        return textColor == DyeColor.BLACK.getSignColor() && sign.isGlowingText() ? -988212 : NativeImage.packColor(0, blue, green, red);
    }

    public static SpriteIdentifier getCanvasSignSpriteTexture(DyeColor dyeColor) {
        return dyeColor != null ? DYED_CANVAS_SIGN_SPRITES.get(dyeColor) : BLANK_CANVAS_SIGN_SPRITE;
    }

    public static SpriteIdentifier createSignSpriteIdentifier(DyeColor dyeColor) {
        return new SpriteIdentifier(TexturedRenderLayers.SIGNS_ATLAS_TEXTURE,
                new Identifier(FarmersDelightMod.MOD_ID, "entity/signs/canvas_" + dyeColor.getName()));
    }

}
