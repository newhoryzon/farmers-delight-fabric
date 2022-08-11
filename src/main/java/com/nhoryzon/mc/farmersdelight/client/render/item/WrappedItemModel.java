package com.nhoryzon.mc.farmersdelight.client.render.item;

import net.minecraft.client.render.model.BakedModel;

public class WrappedItemModel<T extends BakedModel> extends BakedModelWrapper<T>
{
    public WrappedItemModel(T originalModel) {
        super(originalModel);
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

}
