package com.nhoryzon.mc.farmersdelight.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.StewItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StewItem.class)
public abstract class StewItemMixin extends Item {

	public StewItemMixin(Settings settings) {
		super(settings);
	}
	
	@Inject(method = "finishUsing", at = @At("HEAD"), cancellable = true)
	public void finishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
		super.finishUsing(stack.copy(), world, user);
		
		if (user instanceof PlayerEntity player) {
			if (player.getAbilities().creativeMode) {
				cir.setReturnValue(stack);
			} else {
				cir.setReturnValue(ItemUsage.exchangeStack(stack, player, new ItemStack(Items.BOWL)));
			}
		} else {
			cir.setReturnValue(new ItemStack(Items.BOWL));
		}
	}

}
