package com.nhoryzon.mc.farmersdelight.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PopsicleItem extends ConsumableItem {

    public PopsicleItem(Settings settings) {
        super(settings);
    }

    @Override
    public void affectConsumer(ItemStack stack, World world, LivingEntity user) {
        user.extinguish();
    }

}
