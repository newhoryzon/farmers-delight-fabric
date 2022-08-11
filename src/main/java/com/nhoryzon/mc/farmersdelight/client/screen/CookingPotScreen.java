package com.nhoryzon.mc.farmersdelight.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.entity.block.CookingPotBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.screen.CookingPotScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;
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
    private static final Rect2i HEAT_ICON = new Rect2i(47, 55, 17, 15);
    private static final Rect2i PROGRESS_ARROW = new Rect2i(89, 25, 0, 17);

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
        renderHeatIndicatorTooltip(ms, mouseX, mouseY);
        drawMouseoverTooltip(ms, mouseX, mouseY);
    }

    private void renderHeatIndicatorTooltip(MatrixStack ms, int mouseX, int mouseY) {
        if (isPointWithinBounds(HEAT_ICON.getX(), HEAT_ICON.getY(), HEAT_ICON.getWidth(), HEAT_ICON.getHeight(), mouseX, mouseY)) {
            List<Text> tooltip = new ArrayList<>();
            String key = "container.cooking_pot." + (handler.isHeated() ? "heated" : "not_heated");
            tooltip.add(FarmersDelightMod.i18n(key));
            renderTooltip(ms, tooltip, mouseX, mouseY);
        }
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack ms, int mouseX, int mouseY) {
        if (this.handler.getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack()) {
            if (focusedSlot.id == CookingPotBlockEntity.MEAL_DISPLAY_SLOT) {
                List<Text> tooltip = new ArrayList<>();

                ItemStack meal = focusedSlot.getStack();
                Text text = meal.getName();
                if (text instanceof MutableText mutableName) {
                    tooltip.add(mutableName.formatted(meal.getRarity().formatting));
                } else {
                    tooltip.add(text);
                }
                meal.getItem().appendTooltip(meal, handler.tileEntity.getWorld(), tooltip, TooltipContext.Default.NORMAL);

                ItemStack containerItem = handler.tileEntity.getMealContainer();
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
        drawTexture(ms, x, y, 0, 0, backgroundWidth, backgroundHeight);

        // Render heat indicator
        if (handler.isHeated()) {
            drawTexture(ms, x + HEAT_ICON.getX(), y + HEAT_ICON.getY(), 176, 0, HEAT_ICON.getWidth(), HEAT_ICON.getHeight());
        }

        // Render progress arrow
        int l = handler.getCookProgressionScaled();
        drawTexture(ms, x + PROGRESS_ARROW.getX(), y + PROGRESS_ARROW.getY(), 176, 15, l + 1, PROGRESS_ARROW.getHeight());
    }

}