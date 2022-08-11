package com.nhoryzon.mc.farmersdelight.client.render.item;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BakedModelWrapper<T extends BakedModel> implements BakedModel {

    protected final T originalModel;

    public BakedModelWrapper(T originalModel) {
        this.originalModel = originalModel;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return originalModel.getQuads(state, face, random);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return originalModel.useAmbientOcclusion();
    }

    @Override
    public boolean hasDepth() {
        return originalModel.hasDepth();
    }

    @Override
    public boolean isSideLit() {
        return originalModel.isSideLit();
    }

    @Override
    public boolean isBuiltin() {
        return originalModel.isBuiltin();
    }

    @Override
    public Sprite getParticleSprite() {
        return originalModel.getParticleSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return originalModel.getTransformation();
    }

    @Override
    public ModelOverrideList getOverrides() {
        return originalModel.getOverrides();
    }

}
