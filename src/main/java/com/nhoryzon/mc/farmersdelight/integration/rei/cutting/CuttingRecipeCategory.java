package com.nhoryzon.mc.farmersdelight.integration.rei.cutting;

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
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CuttingRecipeCategory implements DisplayCategory<CuttingRecipeDisplay> {

    private static final Identifier GUI_TEXTURE = new Identifier(FarmersDelightMod.MOD_ID, "textures/gui/rei/cutting_board.png");

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(BlocksRegistry.CUTTING_BOARD.get());
    }

    @Override
    public Text getTitle() {
        return FarmersDelightMod.i18n("jei.cutting");
    }

    @Override
    public CategoryIdentifier<? extends CuttingRecipeDisplay> getCategoryIdentifier() {
        return FarmersDelightModREI.CUTTING;
    }

    @Override
    public List<Widget> setupDisplay(CuttingRecipeDisplay display, Rectangle bounds) {
        Point origin = bounds.getLocation();
        final List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        Rectangle bgBounds = FarmersDelightModREI.centeredIntoRecipeBase(new Point(origin.x + 10, origin.y), 108, 44);
        widgets.add(Widgets.createTexturedWidget(GUI_TEXTURE, new Rectangle(bgBounds.x, bgBounds.y, 62, 40), 15, 7));

        Rectangle cuttingBoard = FarmersDelightModREI.centeredInto(
                new Rectangle(bgBounds.x, bgBounds.y + 12, 18, 18), 48, 48);
        widgets.add(Widgets.createSlot(cuttingBoard)
                .entry(EntryStacks.of(BlocksRegistry.CUTTING_BOARD.get())).notInteractable().notFavoritesInteractable()
                .disableBackground().disableHighlight().disableTooltips());
        widgets.add(Widgets.createSlot(new Point(bgBounds.x + 1, bgBounds.y + 1))
                .entries(display.getToolInput()).markInput().disableBackground());
        widgets.add(Widgets.createSlot(new Point(bgBounds.x + 1, bgBounds.y + 20))
                .entries(display.getIngredientEntries().get(0)).markInput().disableBackground());

        List<EntryIngredient> outputEntries = display.getOutputEntries();
        int maxOutputWidth = outputEntries.size() <= 1 ? 18 : 37;
        int maxOutputHeight = 18 + ((outputEntries.size() - 1) / 2) * 19;
        Rectangle outputBounds = FarmersDelightModREI.centeredInto(
                new Rectangle(bgBounds.x + 62, bgBounds.y + 4, 36, 36), maxOutputWidth, maxOutputHeight);
        for (int i = 0; i < outputEntries.size(); i++) {
            Point slotLoc = new Point(outputBounds.x + i % 2 * 19, outputBounds.y + (i / 2) * 19);
            widgets.add(Widgets.createTexturedWidget(GUI_TEXTURE, new Rectangle(slotLoc.x, slotLoc.y, 18, 18), 15, 7));
            widgets.add(Widgets.createSlot(new Point(slotLoc.x + 1, slotLoc.y + 1))
                    .entries(outputEntries.get(i)).markOutput().disableBackground());
        }

        return widgets;
    }

}
