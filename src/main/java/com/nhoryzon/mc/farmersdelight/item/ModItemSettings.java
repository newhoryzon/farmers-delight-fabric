package com.nhoryzon.mc.farmersdelight.item;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.item.enumeration.Foods;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;

public class ModItemSettings extends FabricItemSettings {

    public static FabricItemSettings base() {
        return new ModItemSettings();
    }

    public static FabricItemSettings noStack() {
        return new ModItemSettings().maxCount(1);
    }

    public static FabricItemSettings food(Foods food) {
        return new ModItemSettings().food(food.get());
    }

    public static FabricItemSettings food(Foods food, Item remainder, int maxCount) {
        return new ModItemSettings().food(food.get()).recipeRemainder(remainder).maxCount(maxCount);
    }

    public ModItemSettings() {
        super();
        group(FarmersDelightMod.ITEM_GROUP);
    }

}