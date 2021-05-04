package com.nhoryzon.mc.farmersdelight.loot.function;

import com.nhoryzon.mc.farmersdelight.entity.block.CookingPotBlockEntity;
import com.nhoryzon.mc.farmersdelight.registry.LootFunctionsRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.nbt.CompoundTag;

public class CopyMealFunction extends ConditionalLootFunction {

    public static ConditionalLootFunction.Builder<?> builder() {
        return builder(CopyMealFunction::new);
    }

    public CopyMealFunction(LootCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        BlockEntity blockEntity = context.get(LootContextParameters.BLOCK_ENTITY);
        if (blockEntity instanceof CookingPotBlockEntity) {
            CompoundTag tag = ((CookingPotBlockEntity) blockEntity).writeMeal(new CompoundTag());
            if (!tag.isEmpty()) {
                stack.putSubTag("BlockEntityTag", tag);
            }
        }

        return stack;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionsRegistry.COPY_MEAL.type();
    }

}
