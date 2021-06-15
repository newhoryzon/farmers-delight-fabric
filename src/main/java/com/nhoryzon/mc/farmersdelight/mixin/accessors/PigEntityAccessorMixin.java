package com.nhoryzon.mc.farmersdelight.mixin.accessors;

import net.minecraft.entity.passive.PigEntity;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PigEntity.class)
public interface PigEntityAccessorMixin {

    @Accessor("BREEDING_INGREDIENT")
    static void setFoodItems(Ingredient ingredient) {
        throw new AssertionError();
    }

    @Accessor("BREEDING_INGREDIENT")
    static Ingredient getFoodItems() {
        throw new AssertionError();
    }

}
