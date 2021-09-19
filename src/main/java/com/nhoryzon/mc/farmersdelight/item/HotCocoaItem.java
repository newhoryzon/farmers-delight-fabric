package com.nhoryzon.mc.farmersdelight.item;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HotCocoaItem extends MilkBottleItem {

    public HotCocoaItem(Settings settings) {
        super(settings);
    }

    @Override
    public void affectConsumer(ItemStack stack, World world, LivingEntity user) {
        Set<StatusEffect> compatibleEffectList = user.getStatusEffects().stream().filter(this::canEffectInstanceBeRemovedByMilk).map(
                StatusEffectInstance::getEffectType).collect(Collectors.toSet());

        if (!compatibleEffectList.isEmpty()) {
            compatibleEffectList.stream().skip(world.getRandom().nextInt(compatibleEffectList.size())).findFirst().ifPresent(
                    user::removeStatusEffect);
        }
    }

    private boolean canEffectInstanceBeRemovedByMilk(StatusEffectInstance effectInstance) {
        return effectInstance.getEffectType().getCategory() == StatusEffectCategory.HARMFUL;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        TranslatableText empty = FarmersDelightMod.i18n("tooltip.hot_cocoa");
        tooltip.add(empty.formatted(Formatting.BLUE));
    }
}