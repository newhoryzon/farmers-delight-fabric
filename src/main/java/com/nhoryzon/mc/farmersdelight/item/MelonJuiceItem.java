package com.nhoryzon.mc.farmersdelight.item;

import net.minecraft.item.Items;

public class MelonJuiceItem extends DrinkableItem {

    public MelonJuiceItem() {
        super(new ModItemSettings().recipeRemainder(Items.GLASS_BOTTLE).maxCount(16), false, true);
    }

}
