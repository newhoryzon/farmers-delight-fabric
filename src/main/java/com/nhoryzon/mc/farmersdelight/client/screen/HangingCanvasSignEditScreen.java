package com.nhoryzon.mc.farmersdelight.client.screen;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.block.signs.ICanvasSign;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

public class HangingCanvasSignEditScreen extends AbstractSignEditScreen {
    private static final Vector3f TEXT_SCALE = new Vector3f(1.0F, 1.0F, 1.0F);

    protected DyeColor dye;
    private final Identifier texture;

    public HangingCanvasSignEditScreen(SignBlockEntity blockEntity, boolean front, boolean filtered) {
        super(blockEntity, front, filtered, Text.translatable("hanging_sign.edit"));
        Block block = blockEntity.getCachedState().getBlock();
        if (block instanceof ICanvasSign canvasSign) {
            dye = canvasSign.getBackgroundColor();
        }
        String dyeName = dye != null ? "_" + dye.getName() : "";
        this.texture = new Identifier(FarmersDelightMod.MOD_ID, "canvas" + dyeName + ".png").withPrefixedPath("textures/gui/hanging_signs/");
    }

    @Override
    protected void translateForRender(DrawContext context, BlockState state) {
        context.getMatrices().translate((float) this.width / 2.0f, 125.0f, 50.0f);
    }

    @Override
    protected void renderSignBackground(DrawContext context, BlockState state) {
        context.getMatrices().translate(0.0F, -13.0F, 0.0F);
        context.getMatrices().scale(4.5F, 4.5F, 1.0F);
        context.drawTexture(this.texture, -8, -8, 0.0F, 0.0F, 16, 16, 16, 16);
    }

    @Override
    protected Vector3f getTextScale() {
        return TEXT_SCALE;
    }
}
