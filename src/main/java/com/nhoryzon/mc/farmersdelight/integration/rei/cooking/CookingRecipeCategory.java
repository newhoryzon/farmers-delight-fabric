package com.nhoryzon.mc.farmersdelight.integration.rei.cooking;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.integration.rei.FarmersDelightModREI;
import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Arrow;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CookingRecipeCategory implements DisplayCategory<CookingRecipeDisplay> {

    private static final Identifier GUI_TEXTURE = new Identifier(FarmersDelightMod.MOD_ID, "textures/gui/cooking_pot.png");

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(BlocksRegistry.COOKING_POT.get());
    }

    @Override
    public Text getTitle() {
        return FarmersDelightMod.i18n("jei.cooking");
    }

    @Override
    public CategoryIdentifier<? extends CookingRecipeDisplay> getCategoryIdentifier() {
        return FarmersDelightModREI.COOKING;
    }

    @Override
    public List<Widget> setupDisplay(CookingRecipeDisplay display, Rectangle bounds) {
        Point origin = bounds.getLocation();
        final List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));
        Rectangle bgBounds = FarmersDelightModREI.centeredIntoRecipeBase(origin, 116, 56);
        widgets.add(Widgets.createTexturedWidget(GUI_TEXTURE, bgBounds, 29, 16));

        List<EntryIngredient> ingredientEntries = display.getIngredientEntries();
        if (ingredientEntries != null) {
            for (int i = 0; i < ingredientEntries.size(); i++) {
                Point slotLoc = new Point(bgBounds.x + 1 + i % 3 * 18, bgBounds.y + 1 + (i / 3) * 18);
                widgets.add(Widgets.createSlot(slotLoc).entries(ingredientEntries.get(i)).markInput().disableBackground());
            }
        }

        widgets.add(Widgets.createSlot(new Point(bgBounds.x + 63, bgBounds.y + 39))
                .entries(display.getContainerOutput()).markInput().disableBackground());

        widgets.add(Widgets.createSlot(new Point(bgBounds.x + 95, bgBounds.y + 12))
                .entries(display.getOutputEntries().get(0)).markOutput().disableBackground());
        widgets.add(Widgets.createSlot(new Point(bgBounds.x + 95, bgBounds.y + 39))
                .entries(display.getOutputEntries().get(0)).markOutput().disableBackground());

        widgets.add(Widgets.createTexturedWidget(GUI_TEXTURE,
                new Rectangle(bgBounds.x + 18, bgBounds.y + 39, 17, 15), 176, 0));
        Arrow cookArrow = Widgets.createArrow(new Point(bgBounds.x + 61, bgBounds.y + 10))
                .animationDurationTicks(display.getCookTime());
        widgets.add(cookArrow);
        widgets.add(Widgets.createLabel(new Point(
                cookArrow.getBounds().x + cookArrow.getBounds().width / 2, cookArrow.getBounds().y - 8),
                new LiteralText(display.getCookTime() + " t"))
                .noShadow().centered().tooltipLine("Ticks")
                .color(Formatting.DARK_GRAY.getColorValue(), Formatting.GRAY.getColorValue()));

        return widgets;
    }

}
