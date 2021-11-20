package com.nhoryzon.mc.farmersdelight.loot.function;

import com.nhoryzon.mc.farmersdelight.entity.block.CookingPotBlockEntity;
import com.nhoryzon.mc.farmersdelight.registry.LootFunctionsRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.nbt.NbtCompound;

public class CopyMealFunction extends ConditionalLootFunction {
    public static ConditionalLootFunction.Builder<?> builder() {
        return builder(CopyMealFunction::new);
    }

    public CopyMealFunction(LootCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        if (context.get(LootContextParameters.BLOCK_ENTITY) instanceof CookingPotBlockEntity cookingPotBlockEntity) {
            NbtCompound tag = cookingPotBlockEntity.writeMeal(new NbtCompound());
            if (!tag.isEmpty()) {
                stack.setSubNbt("BlockEntityTag", tag);
            }
        }

        return stack;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionsRegistry.COPY_MEAL.type();
    }
}