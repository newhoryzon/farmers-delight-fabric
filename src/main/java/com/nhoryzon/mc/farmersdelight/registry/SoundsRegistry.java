package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public enum SoundsRegistry {

    BLOCK_STOVE_CRACKLE("block.stove.crackle"),

    BLOCK_COOKING_POT_BOIL_SOUP("block.cooking_pot.boil_soup"),
    BLOCK_COOKING_POT_BOIL("block.cooking_pot.boil"),

    BLOCK_CUTTING_BOARD_KNIFE("block.cutting_board.knife"),

    BLOCK_CABINET_OPEN("block.cabinet.open"),
    BLOCK_CABINET_CLOSE("block.cabinet.close"),

    BLOCK_SKILLET_SIZZLE("block.skillet.sizzle"),
    BLOCK_SKILLET_ADD_FOOD("block.skillet.add_food"),
    ITEM_SKILLET_ATTACK_STRONG("item.skillet.attack.strong"),
    ITEM_SKILLET_ATTACK_WEAK("item.skillet.attack.weak"),

    BLOCK_TOMATO_PICK_FROM_BUSH("block.tomato_bush.pick_tomatoes"),

    ENTITY_ROTTEN_TOMATO_THROW("entity.rotten_tomato.throw"),
    ENTITY_ROTTEN_TOMATO_HIT("entity.rotten_tomato.hit");

    private final String pathName;
    private final SoundEvent soundEvent;

    SoundsRegistry(String pathName) {
        this.pathName = pathName;
        this.soundEvent = new SoundEvent(new Identifier(FarmersDelightMod.MOD_ID, this.pathName));
    }

    public static void registerAll() {
        for (SoundsRegistry value : values()) {
            Registry.register(Registry.SOUND_EVENT, new Identifier(FarmersDelightMod.MOD_ID, value.pathName), value.soundEvent);
        }
    }

    public SoundEvent get() {
        return soundEvent;
    }

}