package com.nhoryzon.mc.farmersdelight.loot.function;

import com.nhoryzon.mc.farmersdelight.registry.LootFunctionsRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmokingRecipe;

import java.util.Optional;

public class SmokerCookFunction extends ConditionalLootFunction {
    public SmokerCookFunction(LootCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        if (stack.isEmpty()) {
            return stack;
        } else {
            Optional<SmokingRecipe> recipe = context.getWorld().getRecipeManager().listAllOfType(RecipeType.SMOKING).stream()
                    .filter(smokingRecipe -> smokingRecipe.getIngredients().get(0).test(stack)).findFirst();
            if (recipe.isPresent()) {
                ItemStack result = recipe.get().getOutput().copy();
                result.setCount(result.getCount() * stack.getCount());

                return result;
            } else {
                return stack;
            }
        }
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionsRegistry.SMOKER_COOK.type();
    }
}