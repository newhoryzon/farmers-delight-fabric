package com.nhoryzon.mc.farmersdelight.mixin.accessors;

import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChickenEntity.class)
public interface ChickenEntityAccessorMixin {

    @Accessor("BREEDING_INGREDIENT")
    static void setFoodItems(Ingredient ingredient) {
        throw new AssertionError();
    }

    @Accessor("BREEDING_INGREDIENT")
    static Ingredient getFoodItems() {
        throw new AssertionError();
    }

}
