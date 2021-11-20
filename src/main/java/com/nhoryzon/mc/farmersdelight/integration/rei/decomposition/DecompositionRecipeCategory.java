package com.nhoryzon.mc.farmersdelight.integration.rei.decomposition;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.integration.rei.FarmersDelightModREI;
import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class DecompositionRecipeCategory implements DisplayCategory<DecompositionRecipeDisplay> {

    private static final Identifier GUI_TEXTURE = new Identifier(FarmersDelightMod.MOD_ID, "textures/gui/rei/decomposition.png");

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(BlocksRegistry.RICH_SOIL.get());
    }

    @Override
    public Text getTitle() {
        return FarmersDelightMod.i18n("jei.decomposition");
    }

    @Override
    public CategoryIdentifier<? extends DecompositionRecipeDisplay> getCategoryIdentifier() {
        return FarmersDelightModREI.DECOMPOSITION;
    }

    @Override
    public List<Widget> setupDisplay(DecompositionRecipeDisplay display, Rectangle bounds) {
        final List<Widget> widgets = new ArrayList<>();

        Rectangle baseBounds = new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height + 6);
        widgets.add(Widgets.createRecipeBase(baseBounds));
        Rectangle bgBounds = FarmersDelightModREI.centeredInto(baseBounds, 102, 62);
        widgets.add(Widgets.createTexturedWidget(GUI_TEXTURE, new Rectangle(bgBounds.x, bgBounds.y, 102, 40), 8, 9));

        widgets.add(Widgets.createSlot(new Point(bgBounds.x + 1, bgBounds.y + 17))
                .entries(display.getInputEntries().get(0)).markInput().disableBackground());
        widgets.add(Widgets.createSlot(new Point(bgBounds.x + 85, bgBounds.y + 17))
                .entries(display.getOutputEntries().get(0)).markOutput().disableBackground());

        widgets.add(Widgets.createTexturedWidget(GUI_TEXTURE, new Rectangle(bgBounds.x + 55, bgBounds.y + 44, 18, 18), 119, 0));
        widgets.add(Widgets.createSlot(new Point(bgBounds.x + 56, bgBounds.y + 45))
                .entries(display.getModifier()).markInput().disableBackground());
        widgets.add(Widgets.createLabel(new Point(bgBounds.x + 33, bgBounds.y + 30), new LiteralText("  "))
                .noShadow().leftAligned().tooltipLine(FarmersDelightMod.i18n("jei.decomposition.light").getString()));
        widgets.add(Widgets.createLabel(new Point(bgBounds.x + 46, bgBounds.y + 30), new LiteralText("  "))
                .noShadow().leftAligned().tooltipLine(FarmersDelightMod.i18n("jei.decomposition.fluid").getString()));
        widgets.add(Widgets.createLabel(new Point(bgBounds.x + 59, bgBounds.y + 30), new LiteralText("  "))
                .noShadow().leftAligned().tooltipLine(FarmersDelightMod.i18n("jei.decomposition.accelerators").getString()));

        return widgets;
    }

}
