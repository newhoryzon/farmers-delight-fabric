package com.nhoryzon.mc.farmersdelight.client.render.item;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.util.math.AffineTransformation;

public class SimpleModelSettings implements ModelBakeSettings {

    private final ImmutableMap<?, AffineTransformation> map;
    private final AffineTransformation base;

    public SimpleModelSettings(AffineTransformation base) {
        this(ImmutableMap.of(), base);
    }

    public SimpleModelSettings(ImmutableMap<?, AffineTransformation> map, AffineTransformation base) {
        this.map = map;
        this.base = base;
    }

    @Override
    public AffineTransformation getRotation() {
        return base;
    }

}
