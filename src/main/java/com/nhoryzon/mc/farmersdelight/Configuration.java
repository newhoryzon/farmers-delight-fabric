package com.nhoryzon.mc.farmersdelight;

import net.minecraft.util.DyeColor;

import java.util.List;

public final class Configuration {

    public static final List<DyeColor> DARK_BACKGROUND_DYE_LIST = List.of(
            DyeColor.GRAY, DyeColor.PURPLE, DyeColor.BLUE, DyeColor.BROWN, DyeColor.GREEN, DyeColor.RED, DyeColor.BLACK);

    public static final boolean FOOD_EFFECT_TOOLTIP = true;

    private Configuration() throws InstantiationException {
        throw new InstantiationException();
    }

}
