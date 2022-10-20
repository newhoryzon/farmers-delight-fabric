package com.nhoryzon.mc.farmersdelight.mixin;

import com.nhoryzon.mc.farmersdelight.item.KnifeItem;
import com.nhoryzon.mc.farmersdelight.item.SkilletItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentEnhancementMixin {

    @Inject(method = "isAcceptableItem", at = @At("RETURN"), cancellable = true)
    private void getPossibleEntriesEnhanced(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        boolean canBeEnchanted = false;

        if (stack.getItem() instanceof KnifeItem) {
            for (Enchantment enchantment : KnifeItem.ALLOWED_ENCHANTMENTS) {
                if ((Object)this == enchantment) {
                    canBeEnchanted = true;
                    break;
                }
            }
            cir.setReturnValue(canBeEnchanted);
        } else if (stack.getItem() instanceof SkilletItem) {
            for (Enchantment enchantment : SkilletItem.ALLOWED_ENCHANTMENTS) {
                if ((Object)this == enchantment) {
                    canBeEnchanted = true;
                    break;
                }
            }
            cir.setReturnValue(canBeEnchanted);
        }
    }

}
