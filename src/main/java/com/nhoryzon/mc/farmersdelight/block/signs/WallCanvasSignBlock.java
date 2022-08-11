package com.nhoryzon.mc.farmersdelight.block.signs;

import com.nhoryzon.mc.farmersdelight.entity.block.CanvasSignBlockEntity;
import com.nhoryzon.mc.farmersdelight.registry.BlockEntityTypesRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.SignType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WallCanvasSignBlock extends WallSignBlock implements ICanvasSign {

    private final DyeColor backgroundColor;

    public WallCanvasSignBlock(Block dropLike, DyeColor backgroundColor) {
        super(FabricBlockSettings.copyOf(Blocks.OAK_SIGN).dropsLike(dropLike), SignType.SPRUCE);
        this.backgroundColor = backgroundColor;
    }

    @Override
    public DyeColor getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityTypesRegistry.CANVAS_SIGN.get().instantiate(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.getBlockEntity(pos) instanceof CanvasSignBlockEntity blockEntity && state.getBlock() instanceof ICanvasSign canvasSign) {
            if (canvasSign.isDarkBackground()) {
                blockEntity.setTextColor(DyeColor.WHITE);
            }
        }

        super.onPlaced(world, pos, state, placer, itemStack);
    }

}
