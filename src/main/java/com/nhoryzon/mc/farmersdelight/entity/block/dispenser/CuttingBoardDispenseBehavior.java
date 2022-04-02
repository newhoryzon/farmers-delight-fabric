package com.nhoryzon.mc.farmersdelight.entity.block.dispenser;

import com.nhoryzon.mc.farmersdelight.block.CuttingBoardBlock;
import com.nhoryzon.mc.farmersdelight.entity.block.CuttingBoardBlockEntity;
import com.nhoryzon.mc.farmersdelight.mixin.accessors.DispenserBehaviorsAccessorMixin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class CuttingBoardDispenseBehavior extends FallibleItemDispenserBehavior {
    private static final HashMap<Item, DispenserBehavior> DISPENSE_ITEM_BEHAVIOR_HASH_MAP = new HashMap<>();

    public static void registerBehaviour(Item item, CuttingBoardDispenseBehavior behavior) {
        DISPENSE_ITEM_BEHAVIOR_HASH_MAP.put(item, DispenserBehaviorsAccessorMixin.getBehaviors().get(item)); // Save the old behaviours so they can be used later
        DispenserBlock.registerBehavior(item, behavior);
    }

    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        if (tryDispenseStackOnCuttingBoard(pointer, stack)) {
            playSound(pointer); // I added this because i completely overrode the super implementation which had the sounds.
            spawnParticles(pointer, pointer.getBlockState().get(DispenserBlock.FACING)); // see above, same reasoning
            return stack;
        }

        return DISPENSE_ITEM_BEHAVIOR_HASH_MAP.get(stack.getItem()).dispense(pointer, stack); // Not targeted on cutting board, use vanilla/other mods behaviour
    }

    public boolean tryDispenseStackOnCuttingBoard(BlockPointer source, ItemStack stack) {
        setSuccess(false);
        World world = source.getWorld();
        BlockPos blockPos = source.getPos().offset(source.getBlockState().get(DispenserBlock.FACING));
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (block instanceof CuttingBoardBlock && blockEntity instanceof CuttingBoardBlockEntity cuttingBoardBlockEntity) {
            ItemStack boardItem = cuttingBoardBlockEntity.getStoredItem().copy();
            if (!boardItem.isEmpty() && cuttingBoardBlockEntity.processItemUsingTool(stack, null)) {
                CuttingBoardBlock.spawnCuttingParticles(world, blockPos, boardItem, 5);
                setSuccess(true);
            }

            return true;
        }

        return false;
    }
}