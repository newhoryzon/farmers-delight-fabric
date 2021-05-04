package com.nhoryzon.mc.farmersdelight.item;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

// TODO
public class KnifeItem extends MiningToolItem {

    private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet();
    private static final Set<Material> EFFECTIVE_ON_MATERIAL = Sets.newHashSet(Material.WOOL, Material.CARPET, Material.CAKE, Material.COBWEB);
    private static final Set<Enchantment> ALLOWED_ENCHANTMENTS = Sets.newHashSet(Enchantments.SHARPNESS, Enchantments.SMITE,
            Enchantments.BANE_OF_ARTHROPODS, Enchantments.KNOCKBACK, Enchantments.FIRE_ASPECT, Enchantments.LOOTING);

    public KnifeItem(ToolMaterial material, Settings settings) {
        super(.5f, -1.8f, material, EFFECTIVE_ON, settings);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        Material material = state.getMaterial();
        if (EFFECTIVE_ON.contains(state.getBlock()) || EFFECTIVE_ON_MATERIAL.contains(material)) {
            return this.miningSpeed;
        } else {
            return super.getMiningSpeedMultiplier(stack, state);
        }
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, (user) -> user.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

        return true;
    }

}
