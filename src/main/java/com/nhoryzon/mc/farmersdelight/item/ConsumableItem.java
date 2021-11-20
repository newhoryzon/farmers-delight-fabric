package com.nhoryzon.mc.farmersdelight.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

public class ConsumableItem extends Item {
    public ConsumableItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient()) {
            affectConsumer(stack, world, user);
        }

        ItemStack container = new ItemStack(stack.getItem().getRecipeRemainder());

        if (stack.isFood()) {
            super.finishUsing(stack, world, user);
        } else if (user instanceof PlayerEntity player) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
            }
            player.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }

        if (stack.isEmpty()) {
            return container;
        } else {
            if (user instanceof PlayerEntity player && !player.getAbilities().creativeMode && !player.getInventory().insertStack(container)) {
                player.dropItem(container, false);
            }

            return stack;
        }
    }

    public void affectConsumer(ItemStack stack, World world, LivingEntity user) {
        // Do nothing for basic consumable item
    }
}