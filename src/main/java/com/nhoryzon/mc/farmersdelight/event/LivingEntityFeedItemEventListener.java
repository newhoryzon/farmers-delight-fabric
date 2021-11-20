package com.nhoryzon.mc.farmersdelight.event;

import com.nhoryzon.mc.farmersdelight.item.LivingEntityFeedItem;
import com.nhoryzon.mc.farmersdelight.registry.ParticleTypesRegistry;
import com.nhoryzon.mc.farmersdelight.util.MathUtils;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LivingEntityFeedItemEventListener implements UseEntityCallback {

    public static final LivingEntityFeedItemEventListener INSTANCE = new LivingEntityFeedItemEventListener();

    private LivingEntityFeedItemEventListener() {
        // Non-instantiable listener
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (entity instanceof LivingEntity livingEntity && itemStack.getItem() instanceof LivingEntityFeedItem livingEntityFeedItem &&
                livingEntityFeedItem.canFeed(itemStack, player, livingEntity, hand)) {
            livingEntity.setHealth(livingEntity.getMaxHealth());
            livingEntityFeedItem.getStatusEffectApplied().forEach(effect -> livingEntity.addStatusEffect(new StatusEffectInstance(effect)));
            livingEntity.getEntityWorld().playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, .8f,
                    .8f);

            for (int i = 0; i < 5; ++i) {
                double d0 = MathUtils.RAND.nextGaussian() * .02d;
                double d1 = MathUtils.RAND.nextGaussian() * .02d;
                double d2 = MathUtils.RAND.nextGaussian() * .02d;
                livingEntity.getEntityWorld().addParticle(ParticleTypesRegistry.STAR.get(), livingEntity.getParticleX(1.d),
                        livingEntity.getRandomBodyY() + .5d, livingEntity.getParticleZ(1.d), d0, d1, d2);
            }

            if (!player.isCreative()) {
                if (itemStack.getItem().getRecipeRemainder() != null) {
                    player.giveItemStack(new ItemStack(itemStack.getItem().getRecipeRemainder()));
                }
                itemStack.decrement(1);
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

}
