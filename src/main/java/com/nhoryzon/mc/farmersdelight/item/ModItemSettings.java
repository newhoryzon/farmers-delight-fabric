package com.nhoryzon.mc.farmersdelight.item;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

public class ModItemSettings extends FabricItemSettings {
    public ModItemSettings() {
        super();
        group(FarmersDelightMod.ITEM_GROUP);
    }
}