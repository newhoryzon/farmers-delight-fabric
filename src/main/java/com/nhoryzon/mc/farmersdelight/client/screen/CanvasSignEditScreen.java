package com.nhoryzon.mc.farmersdelight.client.screen;

import com.nhoryzon.mc.farmersdelight.block.signs.ICanvasSign;
import com.nhoryzon.mc.farmersdelight.client.render.block.CanvasSignBlockEntityRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer.SignModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;

public class CanvasSignEditScreen extends SignEditScreen {
    @Nullable
    protected SignModel signModel;
    @Nullable
    protected DyeColor dye;
    protected final boolean isFrontText;

    public CanvasSignEditScreen(SignBlockEntity blockEntity, boolean front, boolean filtered) {
        super(blockEntity, front, filtered);
        Block block = blockEntity.getCachedState().getBlock();
        if (block instanceof ICanvasSign canvasSign) {
            this.dye = canvasSign.getBackgroundColor();
        }
        this.isFrontText = front;
    }

    @Override
    protected void init() {
        super.init();
        this.signModel = SignBlockEntityRenderer.createSignModel(this.client.getEntityModelLoader(), this.signType);
    }

    @Override
    protected void renderSignBackground(DrawContext context, BlockState state) {
        if (this.signModel != null) {
            boolean flag = state.getBlock() instanceof SignBlock;
            context.getMatrices().translate(0.0f, 31.0f, 0.0f);
            if (!isFrontText) {
                context.getMatrices().multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
            }
            context.getMatrices().scale(BACKGROUND_SCALE, BACKGROUND_SCALE, -BACKGROUND_SCALE);
            SpriteIdentifier spriteIdentifier = CanvasSignBlockEntityRenderer.getCanvasSignSpriteTexture(dye);
            VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(context.getVertexConsumers(), this.signModel::getLayer);

            this.signModel.stick.visible = flag;
            this.signModel.root.render(context.getMatrices(), vertexConsumer, 15728880, OverlayTexture.DEFAULT_UV);
        }
    }
}
