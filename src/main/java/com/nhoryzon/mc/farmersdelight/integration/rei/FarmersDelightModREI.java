package com.nhoryzon.mc.farmersdelight.integration.rei;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.integration.rei.cooking.CookingRecipeCategory;
import com.nhoryzon.mc.farmersdelight.integration.rei.cooking.CookingRecipeDisplay;
import com.nhoryzon.mc.farmersdelight.integration.rei.cutting.CuttingRecipeCategory;
import com.nhoryzon.mc.farmersdelight.integration.rei.cutting.CuttingRecipeDisplay;
import com.nhoryzon.mc.farmersdelight.recipe.CookingPotRecipe;
import com.nhoryzon.mc.farmersdelight.recipe.CuttingBoardRecipe;
import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FarmersDelightModREI implements REIPluginV0 {

    public static final Identifier COOKING = new Identifier(FarmersDelightMod.MOD_ID, "cooking");
    public static final Identifier CUTTING = new Identifier(FarmersDelightMod.MOD_ID, "cutting");

    public static Rectangle centeredIntoRecipeBase(Point origin, int width, int height) {
        return centeredInto(new Rectangle(origin.x, origin.y, 150, 66), width, height);
    }

    public static Rectangle centeredInto(Rectangle origin, int width, int height) {
        return new Rectangle(origin.x + (origin.width - width) / 2, origin.y + (origin.height - height) / 2, width, height);
    }

    @Override
    public void registerPluginCategories(RecipeHelper registry) {
        registry.registerCategories(
                new CookingRecipeCategory(),
                new CuttingRecipeCategory());
    }

    @Override
    public void registerOthers(RecipeHelper registry) {
        registry.registerWorkingStations(COOKING, EntryStack.create(BlocksRegistry.COOKING_POT.get()));
        registry.registerWorkingStations(CUTTING, EntryStack.create(BlocksRegistry.CUTTING_BOARD.get()));
    }

    @Override
    public void registerRecipeDisplays(RecipeHelper registry) {
        registry.registerRecipes(COOKING, CookingPotRecipe.class, CookingRecipeDisplay::new);
        registry.registerRecipes(CUTTING, CuttingBoardRecipe.class, CuttingRecipeDisplay::new);
    }

    @Override
    public Identifier getPluginIdentifier() {
        return new Identifier(FarmersDelightMod.MOD_ID, "rei_plugin");
    }

}
