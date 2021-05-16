package com.nhoryzon.mc.farmersdelight.mixin;

import com.google.common.collect.Lists;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperEnhancementMixin {
    @Inject(method = "getPossibleEntries", at = @At("HEAD"), cancellable = true)
    private static void getPossibleEntriesEnhanced(int power, ItemStack stack, boolean treasureAllowed,
            CallbackInfoReturnable<List<EnchantmentLevelEntry>> returnCallback) {
        ArrayList<EnchantmentLevelEntry> list = Lists.newArrayList();

        boolean bl2 = stack.getItem() == Items.BOOK;
        block0: for (Enchantment lv2 : Registry.ENCHANTMENT) {
            if (lv2.isTreasure() && !treasureAllowed || !lv2.isAvailableForRandomSelection() || !lv2.isAcceptableItem(stack) && !bl2) continue;
            for (int j = lv2.getMaxLevel(); j > lv2.getMinLevel() - 1; --j) {
                if (power < lv2.getMinPower(j) || power > lv2.getMaxPower(j)) continue;
                list.add(new EnchantmentLevelEntry(lv2, j));
                continue block0;
            }
        }

        if (!list.isEmpty()) {
            returnCallback.setReturnValue(list);
        }
    }
}