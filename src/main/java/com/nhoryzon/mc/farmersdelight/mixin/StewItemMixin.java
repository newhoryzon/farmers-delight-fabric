package com.nhoryzon.mc.farmersdelight.mixin;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import net.minecraft.item.Item;
import net.minecraft.item.StewItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Item.class)
public abstract class StewItemMixin {

    @Inject(method = "getMaxCount", at = @At("RETURN"), cancellable = true)
    public void getMaxCount(CallbackInfoReturnable<Integer> cir) {
        if (((Object) this) instanceof StewItem stewItem && FarmersDelightMod.CONFIG.isEnableStackableSoupSize()) {
            Identifier itemId = Registry.ITEM.getId(stewItem);
            String strItemId = itemId != null ? itemId.toString() : StringUtils.EMPTY;
            boolean isOverrideAllSoupItem = FarmersDelightMod.CONFIG.isOverrideAllSoupItems();
            List<String> soupItemList = FarmersDelightMod.CONFIG.getSoupItemList();
            if ((isOverrideAllSoupItem && !soupItemList.contains(strItemId)) || (!isOverrideAllSoupItem && soupItemList.contains(strItemId))) {
                cir.setReturnValue(16);
            }
        }
    }

}
