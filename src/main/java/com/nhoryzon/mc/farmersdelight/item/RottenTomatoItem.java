package com.nhoryzon.mc.farmersdelight.item;

import com.nhoryzon.mc.farmersdelight.entity.RottenTomatoEntity;
import com.nhoryzon.mc.farmersdelight.registry.SoundsRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class RottenTomatoItem extends Item {

    public RottenTomatoItem() {
        super(new ModItemSettings().maxCount(16));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack heldStack = player.getStackInHand(hand);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundsRegistry.ENTITY_ROTTEN_TOMATO_THROW.get(),
                SoundCategory.NEUTRAL, .5f, .4f / world.getRandom().nextFloat() * .4f + .8f);
        if (!world.isClient()) {
            RottenTomatoEntity projectile = new RottenTomatoEntity(world, player);
            projectile.setItem(heldStack);
            projectile.setVelocity(player, player.getPitch(), player.getYaw(), .0f, 1.5f, 1.f);
            world.spawnEntity(projectile);
        }

        player.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!player.getAbilities().creativeMode) {
            heldStack.decrement(1);
        }

        return TypedActionResult.success(heldStack, world.isClient());
    }

}
