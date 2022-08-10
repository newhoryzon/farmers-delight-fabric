package com.nhoryzon.mc.farmersdelight.block.signs;

import com.nhoryzon.mc.farmersdelight.Configuration;
import net.minecraft.util.DyeColor;

public interface ICanvasSign {

    DyeColor getBackgroundColor();

    default boolean isDarkBackground() {
        /* TODO : Add configuration to check if current bg color is in the dark background list dye */
        return getBackgroundColor() != null && Configuration.DARK_BACKGROUND_DYE_LIST.contains(getBackgroundColor());
    }

}
