package com.nhoryzon.mc.farmersdelight.item;

import com.google.common.collect.Sets;
import com.nhoryzon.mc.farmersdelight.tag.Tags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Set;

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

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        ItemStack tool = context.getStack();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Direction facing = context.getSide();

        if (state.getBlock() == Blocks.PUMPKIN && Tags.KNIVES.contains(tool.getItem())) {
            PlayerEntity player = context.getPlayer();
            if (player != null && !world.isClient()) {
                Direction direction = facing.getAxis() == Direction.Axis.Y ? player.getHorizontalFacing().getOpposite() : facing;
                world.playSound(null, pos, SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1.f, 1.f);
                world.setBlockState(pos, Blocks.CARVED_PUMPKIN.getDefaultState().with(CarvedPumpkinBlock.FACING, direction), 11);
                ItemEntity itemEntity = new ItemEntity(world, (double) pos.getX() + .5d + (double) direction.getOffsetX() * .65d,
                        (double) pos.getY() + .1d, (double) pos.getZ() + .5d + (double) direction.getOffsetZ() * .65d,
                        new ItemStack(Items.PUMPKIN_SEEDS, 4));
                itemEntity.setVelocity(.05d * (double) direction.getOffsetX() + world.getRandom().nextDouble() * .02d, .05d,
                        .05d * (double) direction.getOffsetZ() + world.getRandom().nextDouble() * 0.02D);
                world.spawnEntity(itemEntity);
                tool.damage(1, player, (playerIn) -> playerIn.sendToolBreakStatus(context.getHand()));
            }
            return ActionResult.success(world.isClient());
        } else {
            return ActionResult.PASS;
        }
    }

}
