package com.nhoryzon.mc.farmersdelight.client.render.block;

import com.nhoryzon.mc.farmersdelight.block.CuttingBoardBlock;
import com.nhoryzon.mc.farmersdelight.entity.block.CuttingBoardBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

@Environment(value= EnvType.CLIENT)
public class CuttingBoardBlockEntityRenderer implements BlockEntityRenderer<CuttingBoardBlockEntity> {

    public CuttingBoardBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(CuttingBoardBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            int light, int overlay) {
        Direction direction = entity.getCachedState().get(CuttingBoardBlock.FACING).getOpposite();
        ItemStack itemStack = entity.getStoredItem();
        int intPos = (int)entity.getPos().asLong();

        if (!itemStack.isEmpty()) {
            matrices.push();

            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
            boolean blockItem = itemRenderer.getModel(itemStack, entity.getWorld(), null, intPos).hasDepth();

            if (entity.getIsItemCarvingBoard()) {
                renderItemCarved(matrices, direction, itemStack);
            } else if (blockItem) {
                renderBlock(matrices, direction);
            } else {
                renderItemLayingDown(matrices, direction);
            }

            MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformation.Mode.FIXED, light, overlay, matrices,
                    vertexConsumers, intPos);
            matrices.pop();
        }

    }

    private void renderItemLayingDown(MatrixStack matrixStackIn, Direction direction) {
        // Center item above the cutting board
        matrixStackIn.translate(.5d, .08d, .5d);

        // Rotate item to face the cutting board's front side
        float f = -direction.asRotation();
        matrixStackIn.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(f));

        // Rotate item flat on the cutting board. Use X and Y from now on
        matrixStackIn.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.f));

        // Resize the item
        matrixStackIn.scale(.6f, .6f, .6f);
    }

    private void renderBlock(MatrixStack matrixStackIn, Direction direction) {
        // Center block above the cutting board
        matrixStackIn.translate(.5d, .27d, .5d);

        // Rotate block to face the cutting board's front side
        float f = -direction.asRotation();
        matrixStackIn.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(f));

        // Resize the block
        matrixStackIn.scale(.8f, .8f, .8f);
    }

    private void renderItemCarved(MatrixStack matrixStackIn, Direction direction, ItemStack itemStack) {
        // Center item above the cutting board
        matrixStackIn.translate(.5d, .25d, .5d);

        // Rotate item to face the cutting board's front side
        float f = -direction.asRotation();
        matrixStackIn.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(f));

        // Rotate item to be carved on the surface, A little less so for hoes and pickaxes.
        matrixStackIn.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(
                itemStack.getItem() instanceof PickaxeItem || itemStack.getItem() instanceof HoeItem ? 225.f : 180.f));

        // Resize the item
        matrixStackIn.scale(.6f, .6f, .6f);
    }
}