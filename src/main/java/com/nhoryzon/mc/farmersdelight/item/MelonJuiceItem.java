package com.nhoryzon.mc.farmersdelight.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class MelonJuiceItem extends DrinkableItem {

    public MelonJuiceItem() {
        super(new ModItemSettings().recipeRemainder(Items.GLASS_BOTTLE).maxCount(16), false, true);
    }

    @Override
    public void affectConsumer(ItemStack stack, World world, LivingEntity user) {
        user.heal(2.f);
    }

}
