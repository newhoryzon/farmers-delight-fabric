package com.nhoryzon.mc.farmersdelight.client.render.block;

import com.nhoryzon.mc.farmersdelight.block.StoveBlock;
import com.nhoryzon.mc.farmersdelight.entity.block.SkilletBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;

import java.util.Random;

@Environment(value= EnvType.CLIENT)
public class SkilletBlockEntityRenderer implements BlockEntityRenderer<SkilletBlockEntity> {

    private final Random random = new Random();

    public SkilletBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(SkilletBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
            int overlay) {
        Direction direction = entity.getCachedState().get(StoveBlock.FACING);
        Inventory inventory = entity.getInventory();
        int posLong = (int) entity.getPos().asLong();

        ItemStack stack = inventory.getStack(0);
        int seed = stack.isEmpty() ? 187 : Registry.ITEM.getRawId(stack.getItem()) + stack.getDamage();
        this.random.setSeed(seed);

        if (!stack.isEmpty()) {
            int itemRenderCount = this.getModelCount(stack);
            for (int i = 0; i < itemRenderCount; i++) {
                matrices.push();

                // Stack up items in the skillet, with a slight offset per item
                float xOffset = (this.random.nextFloat() * 2.f - 1.f) * .15f * .5f;
                float zOffset = (this.random.nextFloat() * 2.f - 1.f) * .15f * .5f;
                matrices.translate(.5d + xOffset, .1d + .03 * (i + 1), .5d + zOffset);

                float degrees = -direction.asRotation();
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(degrees));
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.f));

                // Resize the items
                matrices.scale(.5f, .5f, .5f);

                if (entity.getWorld() != null) {
                    MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.FIXED, light, overlay,
                            matrices, vertexConsumers, posLong);
                }
                matrices.pop();
            }
        }
    }

    protected int getModelCount(ItemStack stack) {
        if (stack.getCount() > 48) {
            return 5;
        } else if (stack.getCount() > 32) {
            return 4;
        } else if (stack.getCount() > 16) {
            return 3;
        } else if (stack.getCount() > 1) {
            return 2;
        }
        return 1;
    }

}
