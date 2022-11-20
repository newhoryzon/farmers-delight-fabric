package com.nhoryzon.mc.farmersdelight.item;

import com.nhoryzon.mc.farmersdelight.item.enumeration.Foods;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class KelpRollItem extends Item {

    public KelpRollItem() {
        super(new ModItemSettings().food(Foods.KELP_ROLL.get()));
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 64;
    }

}
