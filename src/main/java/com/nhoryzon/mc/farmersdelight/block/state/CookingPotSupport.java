package com.nhoryzon.mc.farmersdelight.block.state;

import net.minecraft.util.StringIdentifiable;

public enum CookingPotSupport implements StringIdentifiable {

    NONE("none"),
    TRAY("tray"),
    HANDLE("handle");

    private final String supportName;

    CookingPotSupport(String name) {
        supportName = name;
    }

    @Override
    public String toString() {
        return asString();
    }

    @Override
    public String asString() {
        return supportName;
    }

}
