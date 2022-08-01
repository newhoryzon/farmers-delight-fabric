package com.nhoryzon.mc.farmersdelight.loot.function;

import com.nhoryzon.mc.farmersdelight.entity.block.SkilletBlockEntity;
import com.nhoryzon.mc.farmersdelight.registry.LootFunctionsRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.nbt.NbtCompound;

public class CopySkilletFunction extends ConditionalLootFunction {

    public static ConditionalLootFunction.Builder<?> builder() {
        return builder(CopySkilletFunction::new);
    }

    public CopySkilletFunction(LootCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        if (context.get(LootContextParameters.BLOCK_ENTITY) instanceof SkilletBlockEntity skilletBlockEntity) {
            NbtCompound tag = skilletBlockEntity.writeSkilletItem(new NbtCompound());
            if (!tag.isEmpty()) {
                stack = ItemStack.fromNbt(tag.getCompound(SkilletBlockEntity.TAG_KEY_SKILLET_STACK));
            }
        }

        return stack;
    }


    @Override
    public LootFunctionType getType() {
        return LootFunctionsRegistry.COPY_SKILLET.type();
    }

}
