package com.nhoryzon.mc.farmersdelight.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import org.jetbrains.annotations.Nullable;

public class SteamParticle extends SpriteBillboardParticle {

    protected SteamParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z);
        this.scale(2.0F);
        this.setBoundingBoxSpacing(0.25F, 0.25F);

        this.maxAge = this.random.nextInt(50) + 80;

        this.gravityStrength = 3.0E-6F;
        this.velocityX = motionX;
        this.velocityY = motionY + (double) (this.random.nextFloat() / 500.0F);
        this.velocityZ = motionZ;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ < this.maxAge && !(this.alpha <= 0.0F)) {
            this.velocityX += this.random.nextFloat() / 5000.0F * (float) (this.random.nextBoolean() ? 1 : -1);
            this.velocityZ += this.random.nextFloat() / 5000.0F * (float) (this.random.nextBoolean() ? 1 : -1);
            this.velocityY -= this.gravityStrength;
            this.move(this.velocityX, this.velocityZ, this.velocityY);
            if (this.age >= this.maxAge - 60 && this.alpha > 0.01F) {
                this.alpha -= 0.02F;
            }
        } else {
            this.markDead();
        }
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {

        private final SpriteProvider sprite;

        public Factory(SpriteProvider sprite) {
            this.sprite = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX,
                double velocityY, double velocityZ) {
            SteamParticle particle = new SteamParticle(world, x, y + 3.d, z, velocityX, velocityY, velocityZ);
            particle.setAlpha(.6f);
            particle.setSprite(sprite);

            return particle;
        }

    }

}
