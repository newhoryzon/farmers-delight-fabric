package com.nhoryzon.mc.farmersdelight.client.render.block;

import com.nhoryzon.mc.farmersdelight.block.StoveBlock;
import com.nhoryzon.mc.farmersdelight.entity.block.StoveBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec2f;

@Environment(value= EnvType.CLIENT)
public class StoveBlockEntityRenderer implements BlockEntityRenderer<StoveBlockEntity> {

    public StoveBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(StoveBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
            int overlay) {
        Direction direction = entity.getCachedState().get(StoveBlock.FACING).getOpposite();
        DefaultedList<ItemStack> inventory = entity.getInventory();
        int intPos = (int)entity.getPos().asLong();

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = inventory.get(i);
            if (!itemStack.isEmpty()) {
                matrices.push();
                matrices.translate(.5d, 1.02d, .5d);

                float angle = -direction.asRotation();
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.f));

                Vec2f itemOffset = entity.getStoveItemOffset(i);
                matrices.translate(itemOffset.x, itemOffset.y, .0d);
                matrices.scale(.375f, .375f, .375f);

                if (entity.getWorld() != null) {
                    int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());
                    MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformationMode.FIXED, lightAbove,
                            overlay, matrices, vertexConsumers, entity.getWorld(), intPos + i);
                }
                matrices.pop();
            }
        }
    }

}