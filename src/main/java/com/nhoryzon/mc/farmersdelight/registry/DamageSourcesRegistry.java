package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import net.minecraft.entity.damage.DamageSource;

public class DamageSourcesRegistry extends DamageSource {

    public static final DamageSource STOVE_BLOCK = (new DamageSourcesRegistry("stove")).setFire();

    protected DamageSourcesRegistry(String name) {
        super(FarmersDelightMod.MOD_ID + "." + name);
    }

}
