package com.nhoryzon.mc.farmersdelight.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
        this.scale(2.f);
        this.setBoundingBoxSpacing(.25f, .25f);

        this.maxAge = this.random.nextInt(50) + 80;

        this.gravityStrength = 3.E-6f;
        this.velocityX = motionX;
        this.velocityY = motionY + (double) (this.random.nextFloat() / 500.f);
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
        if (this.age++ < this.maxAge && !(this.alpha <= 0.f)) {
            this.velocityX += this.random.nextFloat() / 5000.f * (float) (this.random.nextBoolean() ? 1 : -1);
            this.velocityZ += this.random.nextFloat() / 5000.f * (float) (this.random.nextBoolean() ? 1 : -1);
            this.velocityY -= this.gravityStrength;
            this.move(this.velocityX, this.velocityY, this.velocityZ);
            if (this.age >= this.maxAge - 60 && this.alpha > .01f) {
                this.alpha -= .02f;
            }
        } else {
            this.markDead();
        }
    }

    @Environment(value= EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {

        private final SpriteProvider sprite;

        public Factory(SpriteProvider sprite) {
            this.sprite = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX,
                double velocityY, double velocityZ) {
            SteamParticle particle = new SteamParticle(world, x, y + 0.3d, z, velocityX, velocityY, velocityZ);
            particle.setAlpha(.6f);
            particle.setSprite(sprite);

            return particle;
        }

    }

}
