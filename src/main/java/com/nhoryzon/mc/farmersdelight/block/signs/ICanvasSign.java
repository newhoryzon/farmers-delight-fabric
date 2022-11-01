package com.nhoryzon.mc.farmersdelight.block.signs;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import net.minecraft.util.DyeColor;

public interface ICanvasSign {

    DyeColor getBackgroundColor();

    default boolean isDarkBackground() {
        return getBackgroundColor() != null && FarmersDelightMod.CONFIG.getCanvasSignDarkBackgroundDyeList().contains(getBackgroundColor());
    }

}
