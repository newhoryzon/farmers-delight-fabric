package com.nhoryzon.mc.farmersdelight.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class DrinkableItem extends ConsumableItem {

    public DrinkableItem(Settings settings) {
        super(settings);
    }

    public DrinkableItem(Settings settings, boolean hasFoodEffectTooltip) {
        super(settings, hasFoodEffectTooltip);
    }

    public DrinkableItem(Settings settings, boolean hasFoodEffectTooltip, boolean hasCustomTooltip) {
        super(settings, hasFoodEffectTooltip, hasCustomTooltip);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack heldStack = user.getStackInHand(hand);
        if (heldStack.isFood()) {
            if (user.canConsume(heldStack.getItem().getFoodComponent().isAlwaysEdible())) {
                user.setCurrentHand(hand);

                return TypedActionResult.consume(heldStack);
            } else {
                return TypedActionResult.fail(heldStack);
            }
        }

        return ItemUsage.consumeHeldItem(world, user, hand);
    }

}
