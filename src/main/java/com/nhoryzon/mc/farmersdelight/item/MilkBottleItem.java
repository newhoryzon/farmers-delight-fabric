package com.nhoryzon.mc.farmersdelight.item;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class MilkBottleItem extends ConsumableItem {
    public MilkBottleItem(Settings settings) {
        super(settings);
    }

    @Override
    public void affectConsumer(ItemStack stack, World world, LivingEntity user) {
        Collection<StatusEffect> activeStatusEffectList = user.getActiveStatusEffects().keySet();

        if (!activeStatusEffectList.isEmpty()) {
            activeStatusEffectList.stream().skip(world.getRandom().nextInt(activeStatusEffectList.size())).findFirst().ifPresent(user::removeStatusEffect);
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);

        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        TranslatableText empty = FarmersDelightMod.i18n("tooltip.milk_bottle");
        tooltip.add(empty.formatted(Formatting.BLUE));
    }
}