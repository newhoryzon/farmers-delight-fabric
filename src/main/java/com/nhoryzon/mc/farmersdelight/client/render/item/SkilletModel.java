package com.nhoryzon.mc.farmersdelight.client.render.item;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.item.SkilletItem;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkilletModel implements BakedModel {

    private final ModelLoader loader;
    private final BakedModel originalModel;
    private final BakedModel cookingModel;

    private final ModelOverrideList itemOverrides;

    private final HashMap<Item, CompositeBakedModel> cache = new HashMap<>();

    public SkilletModel(ModelLoader loader, BakedModel originalModel, BakedModel cookingModel) {
        this.loader = loader;
        this.originalModel = originalModel;
        this.cookingModel = cookingModel;
        this.itemOverrides = new ModelOverrideList(loader, null, null, Collections.emptyList()) {
            @Nullable
            @Override
            public BakedModel apply(BakedModel model, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
                NbtCompound tag = stack.getOrCreateNbt();
                if (tag.contains(SkilletItem.TAG_KEY_SKILLET_COOKING)) {
                    ItemStack ingredientStack = ItemStack.fromNbt(tag.getCompound(SkilletItem.TAG_KEY_SKILLET_COOKING));

                    return SkilletModel.this.getCookingModel(ingredientStack);
                }

                return originalModel;
            }
        };
    }

    private CompositeBakedModel getCookingModel(ItemStack ingredientStack) {
        return cache.computeIfAbsent(ingredientStack.getItem(), p -> new CompositeBakedModel(loader, ingredientStack, cookingModel));
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return originalModel.getQuads(state, face, random);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return originalModel.useAmbientOcclusion();
    }

    @Override
    public boolean hasDepth() {
        return originalModel.hasDepth();
    }

    @Override
    public boolean isSideLit() {
        return originalModel.isSideLit();
    }

    @Override
    public boolean isBuiltin() {
        return originalModel.isBuiltin();
    }

    @Override
    public Sprite getParticleSprite() {
        return originalModel.getParticleSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return originalModel.getTransformation();
    }

    @Override
    public ModelOverrideList getOverrides() {
        return itemOverrides;
    }

    private static class CompositeBakedModel extends WrappedItemModel<BakedModel> {

        private final List<BakedQuad> genQuads = new ArrayList<>();
        private final Map<Direction, List<BakedQuad>> faceQuads = new EnumMap<>(Direction.class);

        public CompositeBakedModel(ModelLoader loader, ItemStack ingredientStack, BakedModel skillet) {
            super(skillet);

            Identifier ingredientLocation = Registry.ITEM.getId(ingredientStack.getItem());
            UnbakedModel ingredientUnbaked = loader.getOrLoadModel(new ModelIdentifier(ingredientLocation, "inventory"));
            ModelBakeSettings transform = new SimpleModelSettings(
                    new AffineTransformation(
                            new Vec3f(.0f, -.4f, .0f),
                            Vec3f.POSITIVE_X.getDegreesQuaternion(270),
                            new Vec3f(.625f, .625f, .625f), null));
            Identifier name = new Identifier(FarmersDelightMod.MOD_ID, "skillet_with_" + ingredientLocation.toString().replace(':', '_'));

            BakedModel ingredientBaked;
            if (ingredientUnbaked instanceof JsonUnbakedModel bm && bm.getRootModel() == ModelLoader.GENERATION_MARKER) {
                ingredientBaked = new ItemModelGenerator()
                        .create(SpriteIdentifier::getSprite, bm)
                        .bake(loader, bm, SpriteIdentifier::getSprite, transform, name, false);
            } else {
                ingredientBaked = ingredientUnbaked.bake(loader, SpriteIdentifier::getSprite, transform, name);
            }

            for (Direction e : Direction.values()) {
                faceQuads.put(e, new ArrayList<>());
            }

            Random rand = Random.create(0);
            genQuads.addAll(ingredientBaked.getQuads(null, null, rand));

            for (Direction e : Direction.values()) {
                rand.setSeed(0);
                faceQuads.get(e).addAll(ingredientBaked.getQuads(null, e, rand));
            }

            rand.setSeed(0);
            genQuads.addAll(skillet.getQuads(null, null, rand));
            for (Direction e : Direction.values()) {
                rand.setSeed(0);
                faceQuads.get(e).addAll(skillet.getQuads(null, e, rand));
            }
        }

        @Override
        public boolean isBuiltin() {
            return originalModel.isBuiltin();
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
            return face == null ? genQuads : faceQuads.get(face);
        }

    }

}
