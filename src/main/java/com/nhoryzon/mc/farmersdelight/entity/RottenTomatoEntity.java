package com.nhoryzon.mc.farmersdelight.entity;

import com.nhoryzon.mc.farmersdelight.registry.EntityTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.ItemsRegistry;
import com.nhoryzon.mc.farmersdelight.registry.SoundsRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class RottenTomatoEntity extends ThrownItemEntity {

    public RottenTomatoEntity(EntityType<RottenTomatoEntity> entityType, World world) {
        super(entityType, world);
    }

    public RottenTomatoEntity(World world, LivingEntity entity) {
        super(EntityTypesRegistry.ROTTEN_TOMATO, entity, world);
    }

    public RottenTomatoEntity(World world, double x, double y, double z) {
        super(EntityTypesRegistry.ROTTEN_TOMATO, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ItemsRegistry.ROTTEN_TOMATO.get();
    }

    @Override
    public void handleStatus(byte status) {
        ItemStack entityStack = new ItemStack(this.getDefaultItem());
        if (status == 3) {
            for (int i = 0; i < 12; ++i) {
                this.world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, entityStack),
                        this.getX(), this.getY(), this.getZ(),
                        ((double) this.random.nextFloat() * 2.0D - 1.0D) * 0.1F,
                        ((double) this.random.nextFloat() * 2.0D - 1.0D) * 0.1F + 0.1F,
                        ((double) this.random.nextFloat() * 2.0D - 1.0D) * 0.1F);
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        entity.damage(DamageSource.thrownProjectile(this, getOwner()), 0);
        playSound(SoundsRegistry.ENTITY_ROTTEN_TOMATO_HIT.get(), 1.f, (random.nextFloat() - random.nextFloat()) * 2.f + 1.f);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (!world.isClient) {
            world.sendEntityStatus(this, (byte) 3);
            playSound(SoundsRegistry.ENTITY_ROTTEN_TOMATO_HIT.get(), 1.f, (random.nextFloat() - random.nextFloat()) * 2.f + 1.f);
            discard();
        }
    }

}
