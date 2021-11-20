package com.nhoryzon.mc.farmersdelight.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.entity.block.screen.CookingPotScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@Environment(value = EnvType.CLIENT)
public class CookingPotScreen extends HandledScreen<CookingPotScreenHandler> {

    private static final Identifier BACKGROUND_TEXTURE = new Identifier(FarmersDelightMod.MOD_ID, "textures/gui/cooking_pot.png");

    public CookingPotScreen(CookingPotScreenHandler screenContainer, PlayerInventory inv, Text titleIn) {
        super(screenContainer, inv, titleIn);
        this.x = 0;
        this.y = 0;
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
        this.titleX = 28;
    }

    @Override
    public void render(MatrixStack ms, final int mouseX, final int mouseY, float partialTicks) {
        renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        renderMealDisplayTooltip(ms, mouseX, mouseY);
    }

    protected void renderMealDisplayTooltip(MatrixStack ms, int mouseX, int mouseY) {
        if (client != null && client.player != null && client.player.getInventory().getMainHandStack().isEmpty() && focusedSlot != null &&
                focusedSlot.hasStack()) {
            if (focusedSlot.id == 6) {
                List<Text> tooltip = new ArrayList<>();

                ItemStack meal = focusedSlot.getStack();
                tooltip.add(((MutableText) meal.getItem().getName()).formatted(meal.getRarity().formatting));

                ItemStack containerItem = handler.tileEntity.getContainer();
                String container = !containerItem.isEmpty() ? containerItem.getItem().getName().getString() : "";

                tooltip.add(FarmersDelightMod.i18n("container.cooking_pot.served_on", container).formatted(Formatting.GRAY));

                renderTooltip(ms, tooltip, mouseX, mouseY);
            } else {
                renderTooltip(ms, focusedSlot.getStack(), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void drawForeground(MatrixStack ms, int mouseX, int mouseY) {
        super.drawForeground(ms, mouseX, mouseY);
        textRenderer.draw(ms, playerInventoryTitle, 8.f, backgroundHeight - 94.f, 4210752);
    }

    @Override
    protected void drawBackground(MatrixStack ms, float partialTicks, int mouseX, int mouseY) {
        // Render UI background
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        if (client == null) {
            return;
        }

        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        int centerX = (width - backgroundWidth) / 2;
        int centerY = (height - backgroundHeight) / 2;
        drawTexture(ms, centerX, centerY, 0, 0, backgroundWidth, backgroundHeight);

        // Render heat indicator
        if (handler.isHeated()) {
            drawTexture(ms, centerX + 47, centerY + 55, 176, 0, 17, 15);
        }

        // Render progress arrow
        int l = handler.getCookProgressionScaled();
        drawTexture(ms, x + 89, y + 25, 176, 15, l + 1, 17);
    }

}