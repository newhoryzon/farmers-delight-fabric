package com.nhoryzon.mc.farmersdelight.block.signs;

import net.minecraft.util.DyeColor;

import java.util.List;

public interface ICanvasSign {

    List<DyeColor> DARK_BACKGROUND_DYE_LIST = List.of(DyeColor.GRAY, DyeColor.PURPLE, DyeColor.BLUE, DyeColor.BROWN, DyeColor.GREEN,
            DyeColor.RED, DyeColor.BLACK);

    DyeColor getBackgroundColor();

    default boolean isDarkBackground() {
        /* TODO : Add configuration to check if current bg color is in the dark background list dye */
        return getBackgroundColor() != null && DARK_BACKGROUND_DYE_LIST.contains(getBackgroundColor());
    }

}
