package com.nhoryzon.mc.farmersdelight.integration.rei;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.client.screen.CookingPotScreen;
import com.nhoryzon.mc.farmersdelight.integration.rei.cooking.CookingRecipeCategory;
import com.nhoryzon.mc.farmersdelight.integration.rei.cooking.CookingRecipeDisplay;
import com.nhoryzon.mc.farmersdelight.integration.rei.cutting.CuttingRecipeCategory;
import com.nhoryzon.mc.farmersdelight.integration.rei.cutting.CuttingRecipeDisplay;
import com.nhoryzon.mc.farmersdelight.integration.rei.decomposition.DecompositionRecipeCategory;
import com.nhoryzon.mc.farmersdelight.integration.rei.decomposition.DecompositionRecipeDisplay;
import com.nhoryzon.mc.farmersdelight.recipe.CookingPotRecipe;
import com.nhoryzon.mc.farmersdelight.recipe.CuttingBoardRecipe;
import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import com.nhoryzon.mc.farmersdelight.registry.RecipeTypesRegistry;
import com.nhoryzon.mc.farmersdelight.tag.Tags;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;

import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class FarmersDelightModREI implements REIClientPlugin {

    public static final CategoryIdentifier<CookingRecipeDisplay> COOKING = CategoryIdentifier.of(FarmersDelightMod.MOD_ID, "cooking");
    public static final CategoryIdentifier<CuttingRecipeDisplay> CUTTING = CategoryIdentifier.of(FarmersDelightMod.MOD_ID, "cutting");
    public static final CategoryIdentifier<DecompositionRecipeDisplay> DECOMPOSITION = CategoryIdentifier.of(FarmersDelightMod.MOD_ID, "decomposition");

    public static Rectangle centeredIntoRecipeBase(Point origin, int width, int height) {
        return centeredInto(new Rectangle(origin.x, origin.y, 150, 66), width, height);
    }

    public static Rectangle centeredInto(Rectangle origin, int width, int height) {
        return new Rectangle(origin.x + (origin.width - width) / 2, origin.y + (origin.height - height) / 2, width, height);
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(
                new CookingRecipeCategory(),
                new CuttingRecipeCategory(),
                new DecompositionRecipeCategory());
        registry.addWorkstations(COOKING, EntryStacks.of(BlocksRegistry.COOKING_POT.get()));
        registry.addWorkstations(CUTTING, EntryStacks.of(BlocksRegistry.CUTTING_BOARD.get()));
        registry.addWorkstations(DECOMPOSITION, EntryStacks.of(BlocksRegistry.RICH_SOIL.get()));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(CookingPotRecipe.class, RecipeTypesRegistry.COOKING_RECIPE_SERIALIZER.type(), CookingRecipeDisplay::new);
        registry.registerRecipeFiller(CuttingBoardRecipe.class, RecipeTypesRegistry.CUTTING_RECIPE_SERIALIZER.type(), CuttingRecipeDisplay::new);
        registry.add(new DecompositionRecipeDisplay(
                EntryIngredients.of(BlocksRegistry.ORGANIC_COMPOST.get()),
                EntryIngredients.of(BlocksRegistry.RICH_SOIL.get()),
                EntryIngredients.ofItems(Registry.BLOCK.getEntryList(Tags.COMPOST_ACTIVATORS).stream().flatMap(RegistryEntryList::stream).map(RegistryEntry::value).map(Block::asItem).collect(Collectors.toList()))));
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerContainerClickArea(new Rectangle(89, 25, 24, 17), CookingPotScreen.class, COOKING);
    }

}
