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
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value= EnvType.CLIENT)
public class StarParticle extends SpriteBillboardParticle {
    protected StarParticle(ClientWorld world, double posX, double posY, double posZ) {
        super(world, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
        this.velocityX *= .01f;
        this.velocityY *= .01f;
        this.velocityY += .1D;
        this.velocityZ *= .01f;
        this.scale *= 1.5f;
        this.maxAge = 16;
        this.collidesWithWorld = false;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getSize(float tickDelta) {
        return scale * MathHelper.clamp((age + tickDelta) / maxAge * 32.f, .0f, 1.f);
    }

    @Override
    public void tick() {
        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;
        if (this.age++ >= this.maxAge) {
            markDead();
        } else {
            move(velocityX, velocityY, velocityZ);
            if (y == this.prevPosY) {
                velocityX *= 1.1d;
                velocityZ *= 1.1d;
            }

            velocityX *= .86f;
            velocityY *= .86f;
            velocityZ *= .86f;
            if (this.onGround) {
                velocityX *= .7f;
                velocityZ *= .7f;
            }

        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {

        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX,
                double velocityY, double velocityZ) {
            StarParticle particle = new StarParticle(world, x, y + 0.3D, z);
            particle.setSprite(spriteProvider);
            particle.setColor(1.0F, 1.0F, 1.0F);
            return particle;
        }

    }
}