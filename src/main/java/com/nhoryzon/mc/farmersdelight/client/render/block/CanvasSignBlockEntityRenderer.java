package com.nhoryzon.mc.farmersdelight.client.render.block;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.block.signs.ICanvasSign;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.OrderedText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

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

    public static final Vec3d TEXT_OFFSET = new Vec3d(0.d, .33333334f, .046666667f);

    private static final int RENDER_DISTANCE = MathHelper.square(16);

    private final SignModel signModel;
    private final TextRenderer textRenderer;

    public CanvasSignBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context);

        signModel = new SignModel(context.getLayerModelPart(EntityModelLayers.createSign(WoodType.SPRUCE)));
        textRenderer = context.getTextRenderer();
    }

    @Override
    public void render(SignBlockEntity signBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
            int packedLight, int packedOverlay) {
        BlockState blockState = signBlockEntity.getCachedState();
        if (blockState.getBlock() instanceof AbstractSignBlock signBlock) {
            SignBlockEntityRenderer.SignModel model = signModel;
            model.stick.visible = signBlock instanceof SignBlock;

            DyeColor dye = null;
            if (signBlock instanceof ICanvasSign canvasSign) {
                dye = canvasSign.getBackgroundColor();
            }

            renderSignWithText(signBlockEntity, matrixStack, vertexConsumerProvider, packedLight, packedOverlay, blockState, signBlock, dye,
                    model);
        }
    }

    protected void renderSignWithText(SignBlockEntity signBlockEntity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
            int packedLight, int packedOverlay, BlockState blockState, AbstractSignBlock signBlock, DyeColor dye, SignModel model) {
        matrixStack.push();
        translateSign(matrixStack, -signBlock.getRotationDegrees(blockState), blockState);
        renderSign(matrixStack, vertexConsumerProvider, packedLight, packedOverlay, dye, model);
        renderSignText(signBlockEntity.getPos(), signBlockEntity.getFrontText(), matrixStack, vertexConsumerProvider, packedLight,
                signBlockEntity.getTextLineHeight(), signBlockEntity.getMaxTextWidth(), true);
        renderSignText(signBlockEntity.getPos(), signBlockEntity.getBackText(), matrixStack, vertexConsumerProvider, packedLight,
                signBlockEntity.getTextLineHeight(), signBlockEntity.getMaxTextWidth(), false);
        matrixStack.pop();
    }

    protected void translateSign(MatrixStack matrixStack, float angle, BlockState blockState) {
        matrixStack.translate(.5f, .75f * getSignScale(), .5f);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle));
        if (!(blockState.getBlock() instanceof SignBlock)) {
            matrixStack.translate(0.0, -0.3125, -0.4375);
        }
    }

    protected void renderSign(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int packedLight, int packedOverlay,
            DyeColor dye, SignModel model) {
        matrixStack.push();
        float rootScale = getSignScale();
        matrixStack.scale(rootScale, -rootScale, -rootScale);
        SpriteIdentifier spriteIdentifier = getCanvasSignSpriteTexture(dye);
        Objects.requireNonNull(signModel);
        VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumerProvider, model::getLayer);
        signModel.root.render(matrixStack, vertexConsumer, packedLight, packedOverlay);
        matrixStack.pop();
    }

    protected void renderSignText(BlockPos pos, SignText text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
            int packedLight, int textLineHeight, int maxTextWidth, boolean isFrontText) {
        matrixStack.push();
        translateSignText(matrixStack, isFrontText, getTextOffset());

        OrderedText[] orderedTexts = text.getOrderedMessages(MinecraftClient.getInstance().shouldFilterText(), (line) -> {
            List<OrderedText> list = this.textRenderer.wrapLines(line, maxTextWidth);
            return list.isEmpty() ? OrderedText.EMPTY : list.get(0);
        });

        int darkColor;
        int baseColor;
        boolean hasOutline;
        int light;
        if (text.isGlowing()) {
            darkColor = getColor(text, true);
            baseColor = text.getColor().getSignColor();
            hasOutline = shouldRenderOutline(pos, baseColor);
            light = 15728880;
        } else {
            darkColor = getColor(text, false);
            baseColor = darkColor;
            hasOutline = false;
            light = packedLight;
        }

        int verticalOffset = 2 * textLineHeight + getCustomVerticalOffset();

        for(int p = 0; p < 4; ++p) {
            OrderedText orderedText = orderedTexts[p];
            float x = (float)(-this.textRenderer.getWidth(orderedText) / 2);
            float y = (float)p * textLineHeight - verticalOffset;
            if (hasOutline) {
                this.textRenderer.drawWithOutline(orderedText, x, y, baseColor, darkColor, matrixStack.peek().getPositionMatrix(),
                        vertexConsumerProvider, light);
            } else {
                this.textRenderer.draw(orderedText, x, y, baseColor, false, matrixStack.peek().getPositionMatrix(),
                        vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0, light);
            }
        }

        matrixStack.pop();
    }

    /*
        float textScale = 0.010416667F;
        matrixStack.translate(0.0, 0.3333333432674408, 0.046666666865348816);
        matrixStack.scale(textScale, -textScale, textScale);

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


    }
    */

    private void translateSignText(MatrixStack matrixStack, boolean isFrontText, Vec3d pos) {
        if (!isFrontText) {
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.f));
        }

        float textScale = .015625f * getTextScale();
        matrixStack.translate(pos.x, pos.y, pos.z);
        matrixStack.scale(textScale, -textScale, textScale);
    }

    private static boolean shouldRenderOutline(BlockPos pos, int textColor) {
        if (textColor == DyeColor.BLACK.getSignColor()) {
            return true;
        } else {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
            if (clientPlayerEntity != null && minecraftClient.options.getPerspective().isFirstPerson() && clientPlayerEntity.isUsingSpyglass()) {
                return true;
            } else {
                Entity entity = minecraftClient.getCameraEntity();
                return entity != null && entity.squaredDistanceTo(Vec3d.ofCenter(pos)) < (double)RENDER_DISTANCE;
            }
        }
    }

    private static int getColor(SignText signText, boolean isOutlineVisible) {
        int textColor = signText.getColor().getSignColor();
        if (textColor == DyeColor.BLACK.getSignColor() && signText.isGlowing()) {
            return -988212;
        } else {
            double brightness = isOutlineVisible ? .4d : .6d;
            int red = (int)((double)ColorHelper.Argb.getRed(textColor) * brightness);
            int green = (int)((double)ColorHelper.Argb.getGreen(textColor) * brightness);
            int blue = (int)((double)ColorHelper.Argb.getBlue(textColor) * brightness);
            return ColorHelper.Argb.getArgb(0, red, green, blue);
        }
    }

    public Vec3d getTextOffset() {
        return TEXT_OFFSET;
    }

    public int getCustomVerticalOffset() {
        return -1;
    }

    public static SpriteIdentifier getCanvasSignSpriteTexture(DyeColor dyeColor) {
        return dyeColor != null ? DYED_CANVAS_SIGN_SPRITES.get(dyeColor) : BLANK_CANVAS_SIGN_SPRITE;
    }

    public static SpriteIdentifier createSignSpriteIdentifier(DyeColor dyeColor) {
        return new SpriteIdentifier(TexturedRenderLayers.SIGNS_ATLAS_TEXTURE,
                new Identifier(FarmersDelightMod.MOD_ID, "entity/signs/canvas_" + dyeColor.getName()));
    }

}
