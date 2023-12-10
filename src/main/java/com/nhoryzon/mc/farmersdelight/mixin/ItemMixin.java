package com.nhoryzon.mc.farmersdelight.mixin;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.registry.TagsRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.StewItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Shadow public abstract ItemStack getDefaultStack();

    @Inject(method = "getMaxCount", at = @At("RETURN"), cancellable = true)
    public void getMaxCount(CallbackInfoReturnable<Integer> cir) {
        //noinspection ConstantValue
        if (!(((Object)this) instanceof StewItem) || !FarmersDelightMod.CONFIG.isEnableStackableSoupSize()) return;
        if (FarmersDelightMod.CONFIG.isOverrideAllSoupItems() || this.getDefaultStack().isIn(TagsRegistry.STEW_ITEMS)) {
            cir.setReturnValue(16);
        }
    }

}
