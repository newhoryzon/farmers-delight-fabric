package com.nhoryzon.mc.farmersdelight.block.signs;

import com.nhoryzon.mc.farmersdelight.registry.BlockEntityTypesRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HangingCanvasSignBlock extends HangingSignBlock implements ICanvasSign {
    private final DyeColor backgroundColor;

    public HangingCanvasSignBlock(@Nullable DyeColor backgroundColor) {
        super(FabricBlockSettings.copyOf(Blocks.OAK_HANGING_SIGN), WoodType.SPRUCE);
        this.backgroundColor = backgroundColor;
    }

    @Override
    public DyeColor getBackgroundColor() {
        return this.backgroundColor;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityTypesRegistry.HANGING_CANVAS_SIGN.get().instantiate(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        Block block = state.getBlock();

        if (blockEntity instanceof SignBlockEntity signEntity && block instanceof ICanvasSign canvasSign) {
            if (canvasSign.isDarkBackground()) {
                signEntity.changeText(text -> text.withColor(DyeColor.WHITE), true);
                signEntity.changeText(text -> text.withColor(DyeColor.WHITE), false);
            }
        }
    }
}
