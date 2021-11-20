package com.nhoryzon.mc.farmersdelight.integration.rei.cutting;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.integration.rei.FarmersDelightModREI;
import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.widgets.Slot;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CuttingRecipeCategory implements RecipeCategory<CuttingRecipeDisplay> {

    private static final Identifier GUI_TEXTURE = new Identifier(FarmersDelightMod.MOD_ID, "textures/gui/rei/cutting_board.png");

    @Override
    public @NotNull Identifier getIdentifier() {
        return FarmersDelightModREI.CUTTING;
    }

    @Override
    public @NotNull EntryStack getLogo() {
        return EntryStack.create(BlocksRegistry.CUTTING_BOARD.get());
    }

    @Override
    public @NotNull String getCategoryName() {
        return FarmersDelightMod.i18n("jei.cutting").getString();
    }

    @Override
    public @NotNull List<Widget> setupDisplay(CuttingRecipeDisplay display, Rectangle bounds) {
        Point origin = bounds.getLocation();
        final List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        Rectangle bgBounds = FarmersDelightModREI.centeredIntoRecipeBase(new Point(origin.x + 10, origin.y), 108, 44);
        widgets.add(Widgets.createTexturedWidget(GUI_TEXTURE, new Rectangle(bgBounds.x, bgBounds.y, 62, 40), 15, 7));

        Rectangle cuttingBoard = FarmersDelightModREI.centeredInto(
                new Rectangle(bgBounds.x, bgBounds.y + 12, 18, 18), 48, 48);
        Slot cuttingBoardBg = Widgets.createSlot(cuttingBoard.getLocation())
                .entry(EntryStack.create(BlocksRegistry.CUTTING_BOARD.get())).notInteractable().notFavoritesInteractable()
                .disableBackground().disableHighlight().disableTooltips();
        cuttingBoardBg.getBounds().setBounds(cuttingBoard);
        widgets.add(cuttingBoardBg);
        widgets.add(Widgets.createSlot(new Point(bgBounds.x + 1, bgBounds.y + 1))
                .entries(display.getToolInput()).markInput().disableBackground());
        widgets.add(Widgets.createSlot(new Point(bgBounds.x + 1, bgBounds.y + 20))
                .entries(display.getIngredientEntries().get(0)).markInput().disableBackground());

        List<List<EntryStack>> outputEntries = display.getResultingEntries();
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
