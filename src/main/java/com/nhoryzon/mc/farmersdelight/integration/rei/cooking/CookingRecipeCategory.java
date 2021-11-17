package com.nhoryzon.mc.farmersdelight.integration.rei.cooking;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.integration.rei.FarmersDelightModREI;
import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.widgets.Arrow;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.entries.RecipeEntry;
import me.shedaniel.rei.gui.entries.SimpleRecipeEntry;
import me.shedaniel.rei.gui.widget.Widget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CookingRecipeCategory implements RecipeCategory<CookingRecipeDisplay> {

    private static final Identifier GUI_TEXTURE = new Identifier(FarmersDelightMod.MOD_ID, "textures/gui/cooking_pot.png");

    @Override
    public @NotNull Identifier getIdentifier() {
        return FarmersDelightModREI.COOKING;
    }

    @Override
    public @NotNull EntryStack getLogo() {
        return EntryStack.create(BlocksRegistry.COOKING_POT.get());
    }

    @Override
    public @NotNull String getCategoryName() {
        return FarmersDelightMod.i18n("jei.cooking").getString();
    }

    @Override
    public @NotNull RecipeEntry getSimpleRenderer(CookingRecipeDisplay recipe) {
        return SimpleRecipeEntry.from(recipe.getInputEntries(), recipe.getResultingEntries());
    }

    @Override
    public @NotNull List<Widget> setupDisplay(CookingRecipeDisplay display, Rectangle bounds) {
        Point origin = bounds.getLocation();
        final List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));
        Rectangle bgBounds = FarmersDelightModREI.centeredIntoRecipeBase(origin, 116, 56);
        widgets.add(Widgets.createTexturedWidget(GUI_TEXTURE, bgBounds, 29, 16));

        List<List<EntryStack>> ingredientEntries = display.getIngredientEntries();
        if (ingredientEntries != null) {
            for (int i = 0; i < ingredientEntries.size(); i++) {
                Point slotLoc = new Point(bgBounds.x + 1 + i % 3 * 18, bgBounds.y + 1 + (i / 3) * 18);
                widgets.add(Widgets.createSlot(slotLoc).entries(ingredientEntries.get(i)).markInput().disableBackground());
            }
        }

        widgets.add(Widgets.createSlot(new Point(bgBounds.x + 63, bgBounds.y + 39))
                .entries(display.getContainerOutput()).markInput().disableBackground());

        widgets.add(Widgets.createSlot(new Point(bgBounds.x + 95, bgBounds.y + 12))
                .entries(display.getResultingEntries().get(0)).markOutput().disableBackground());
        widgets.add(Widgets.createSlot(new Point(bgBounds.x + 95, bgBounds.y + 39))
                .entries(display.getResultingEntries().get(0)).markOutput().disableBackground());

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
