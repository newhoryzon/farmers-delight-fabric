package com.nhoryzon.mc.farmersdelight.entity.block.inventory.slot;

import com.mojang.datafixers.util.Pair;
import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.ItemHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class CookingPotBowlSlot extends SlotItemHandler {

    public static final Identifier EMPTY_CONTAINER_SLOT_BOWL = new Identifier(FarmersDelightMod.MOD_ID, "item/empty_container_slot_bowl");

    public CookingPotBowlSlot(ItemHandler inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Environment(value= EnvType.CLIENT)
    @Override
    public Pair<Identifier, Identifier> getBackgroundSprite() {
        return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, EMPTY_CONTAINER_SLOT_BOWL);
    }

}
