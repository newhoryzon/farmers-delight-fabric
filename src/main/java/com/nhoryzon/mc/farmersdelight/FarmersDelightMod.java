package com.nhoryzon.mc.farmersdelight;

import com.google.common.collect.Sets;
import com.nhoryzon.mc.farmersdelight.entity.block.CuttingBoardBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.dispenser.CuttingBoardDispenseBehavior;
import com.nhoryzon.mc.farmersdelight.item.LivingEntityFeedItem;
import com.nhoryzon.mc.farmersdelight.registry.*;
import com.nhoryzon.mc.farmersdelight.tag.Tags;
import com.nhoryzon.mc.farmersdelight.util.MathUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CakeBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.*;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.HeightmapDecoratorConfig;

import java.util.Arrays;
import java.util.Set;

/**
 * This fabric port of Farmer's Delight will <b>NOT</b> implement these features :
 * <ul>
 *     <li>the possibility to disable vanilla "Crops Crates"</li>
 *     <li>the integrations of others mods like : CraftTweaker, JEI, BotanyPots, Create, Immersive Engineering and Silent Gear</li>
 * </ul>
 */
public class FarmersDelightMod implements ModInitializer {
    public static final ConfiguredDecorator<HeightmapDecoratorConfig> HEIGHTMAP = Decorator.HEIGHTMAP.configure(
            new HeightmapDecoratorConfig(Heightmap.Type.MOTION_BLOCKING));
    public static final ConfiguredDecorator<HeightmapDecoratorConfig> HEIGHTMAP_SPREAD_DOUBLE = Decorator.HEIGHTMAP_SPREAD_DOUBLE.configure(
            new HeightmapDecoratorConfig(Heightmap.Type.MOTION_BLOCKING));
    public static final ConfiguredDecorator<?> SQUARE_HEIGHTMAP = HEIGHTMAP.spreadHorizontally();
    public static final ConfiguredDecorator<?> SQUARE_HEIGHTMAP_SPREAD_DOUBLE = HEIGHTMAP_SPREAD_DOUBLE.spreadHorizontally();

    public static final String MOD_ID = "farmersdelight";

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "main"),
            () -> new ItemStack(ItemsRegistry.STOVE.get()));

    public static TranslatableText i18n(String key, Object... args) {
        return new TranslatableText(MOD_ID + "." + key, args);
    }

    @Override
    public void onInitialize() {
        BlocksRegistry.registerAll();
        ItemsRegistry.registerAll();
        EffectsRegistry.registerAll();
        BlockEntityTypesRegistry.registerAll();
        SoundsRegistry.registerAll();
        AdvancementsRegistry.registerAll();
        RecipeTypesRegistry.registerAll();
        LootFunctionsRegistry.registerAll();
        ExtendedScreenTypesRegistry.registerAll();
        ParticleTypesRegistry.registerAll();
        EnchantmentsRegistry.registerAll();
        ConfiguredFeaturesRegistry.registerAll();

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 1,
                factories -> new TradeOffer(new ItemStack(ItemsRegistry.ONION.get(), 26), new ItemStack(Items.EMERALD), 16, 2, .05f));
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 1,
                factories -> new TradeOffer(new ItemStack(ItemsRegistry.TOMATO.get(), 26), new ItemStack(Items.EMERALD), 16, 2, .05f));
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 2,
                factories -> new TradeOffer(new ItemStack(ItemsRegistry.CABBAGE.get(), 16), new ItemStack(Items.EMERALD), 16, 5, .05f));
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 2,
                factories -> new TradeOffer(new ItemStack(ItemsRegistry.RICE.get(), 20), new ItemStack(Items.EMERALD), 16, 5, .05f));

        registerCompostables();
        registerBiomeModifications();
        registerEventListeners();
        registerLootTable();
        registerDispenserBehavior();
    }

    protected void registerEventListeners() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            ItemStack heldItem = player.getMainHandStack();
            if (Tags.KNIVES.contains(heldItem.getItem()) && state.getBlock() instanceof CakeBlock) {
                ItemScatterer.spawn(world, pos,
                        DefaultedList.ofSize(1, new ItemStack(ItemsRegistry.CAKE_SLICE.get(), 7 - state.get(CakeBlock.BITES))));
            }
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            BlockPos pos = hitResult.getBlockPos();
            BlockEntity blockEntity = world.getBlockEntity(pos);
            ItemStack heldItem = player.getStackInHand(hand);
            if (player.isSneaking() && blockEntity instanceof CuttingBoardBlockEntity && !heldItem.isEmpty()) {
                if (heldItem.getItem() instanceof ToolItem || heldItem.getItem() instanceof TridentItem ||
                        heldItem.getItem() instanceof ShearsItem) {
                    boolean success = ((CuttingBoardBlockEntity) blockEntity).carveToolOnBoard(player.getAbilities().creativeMode ? heldItem.copy() : heldItem);

                    if (success) {
                        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.f,
                                .8f);

                        return ActionResult.SUCCESS;
                    }
                }
            }

            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            ItemStack heldItem = player.getStackInHand(hand);

            if (state.getBlock() instanceof CakeBlock && Tags.KNIVES.contains(heldItem.getItem())) {
                int bites = state.get(CakeBlock.BITES);
                if (bites < 6) {
                    world.setBlockState(pos, state.with(CakeBlock.BITES, bites + 1), 3);
                } else {
                    world.removeBlock(pos, false);
                }
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemsRegistry.CAKE_SLICE.get()));
                world.playSound(null, pos, SoundEvents.BLOCK_WOOL_BREAK, SoundCategory.PLAYERS, .8f, .8f);

                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            ItemStack itemStack = player.getStackInHand(hand);

            if (entity instanceof LivingEntity livingEntity && (Tags.DOG_FOOD_USERS.contains(entity.getType()) || Tags.HORSE_FEED_USERS.contains(
                    entity.getType()))) {
                boolean isTameable = livingEntity instanceof TameableEntity;

                if (livingEntity.isAlive() && (!isTameable || ((TameableEntity) livingEntity).isTamed()) &&
                        itemStack.getItem() instanceof LivingEntityFeedItem livingEntityFeedItem &&
                        ((LivingEntityFeedItem) itemStack.getItem()).canFeed(itemStack, player, livingEntity, hand)) {
                    livingEntity.setHealth(livingEntity.getMaxHealth());
                    for (StatusEffectInstance effect : livingEntityFeedItem.getStatusEffectApplied()) {
                        livingEntity.addStatusEffect(new StatusEffectInstance(effect));
                    }
                    livingEntity.getEntityWorld().playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_GENERIC_EAT,
                            SoundCategory.PLAYERS, .8f, .8f);

                    for (int i = 0; i < 5; ++i) {
                        double d0 = MathUtils.RAND.nextGaussian() * .02d;
                        double d1 = MathUtils.RAND.nextGaussian() * .02d;
                        double d2 = MathUtils.RAND.nextGaussian() * .02d;
                        livingEntity.getEntityWorld().addParticle(ParticleTypesRegistry.STAR.get(), livingEntity.getParticleX(1.d),
                                livingEntity.getRandomBodyY() + .5d, livingEntity.getParticleZ(1.d), d0, d1, d2);
                    }

                    if (!player.isCreative()) {
                        if (itemStack.getItem().getRecipeRemainder() != null) {
                            player.giveItemStack(new ItemStack(itemStack.getItem().getRecipeRemainder()));
                        }
                        itemStack.decrement(1);
                    }

                    return ActionResult.SUCCESS;
                }
            }

            return ActionResult.PASS;
        });
    }

    @SuppressWarnings("deprecation")
    protected void registerBiomeModifications() {
        BiomeModifications.addFeature(context -> context.getBiomeKey().equals(BiomeKeys.BEACH), GenerationStep.Feature.VEGETAL_DECORATION,
                ConfiguredFeaturesRegistry.PATCH_WILD_BEETROOTS.key());
        BiomeModifications.addFeature(context -> context.getBiomeKey().equals(BiomeKeys.BEACH), GenerationStep.Feature.VEGETAL_DECORATION,
                ConfiguredFeaturesRegistry.PATCH_WILD_CABBAGES.key());
        BiomeModifications.addFeature(context -> Arrays.asList(Biome.Category.SWAMP, Biome.Category.JUNGLE).contains(context.getBiome().getCategory()),
                GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeaturesRegistry.PATCH_WILD_RICE.key());
        BiomeModifications.addFeature(context -> context.getBiome().getTemperature() >= 1.f, GenerationStep.Feature.VEGETAL_DECORATION,
                ConfiguredFeaturesRegistry.PATCH_WILD_TOMATOES.key());
        BiomeModifications.addFeature(context -> context.getBiome().getTemperature() > .3f && context.getBiome().getTemperature() < 1.f,
                GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeaturesRegistry.PATCH_WILD_CARROTS.key());
        BiomeModifications.addFeature(context -> context.getBiome().getTemperature() > .3f && context.getBiome().getTemperature() < 1.f,
                GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeaturesRegistry.PATCH_WILD_ONIONS.key());
        BiomeModifications.addFeature(context -> context.getBiome().getTemperature() > .0f && context.getBiome().getTemperature() < .3f,
                GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeaturesRegistry.PATCH_WILD_POTATOES.key());
    }

    protected void registerCompostables() {
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.TREE_BARK.get(), .3f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.STRAW.get(), .3f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.CABBAGE_SEEDS.get(), .3f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.TOMATO_SEED.get(), .3f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.RICE.get(), .5f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.RICE_PANICLE.get(), .5f);

        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.PUMPKIN_SLICE.get(), .5f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.CABBAGE_LEAF.get(), .5f);

        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.CABBAGE.get(), .65f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.ONION.get(), .65f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.TOMATO.get(), .65f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.WILD_CABBAGES.get(), .65f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.WILD_ONIONS.get(), .65f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.WILD_TOMATOES.get(), .65f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.WILD_CARROTS.get(), .65f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.WILD_POTATOES.get(), .65f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.WILD_BEETROOTS.get(), .65f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.WILD_RICE.get(), .65f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.PIE_CRUST.get(), .65f);

        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.RICE_BALE.get(), .85f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.SWEET_BERRY_COOKIE.get(), .85f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.HONEY_COOKIE.get(), .85f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.CAKE_SLICE.get(), .85f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.APPLE_PIE_SLICE.get(), .85f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.SWEET_BERRY_CHEESECAKE_SLICE.get(), .85f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.CHOCOLATE_PIE_SLICE.get(), .85f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.RAW_PASTA.get(), .85f);

        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.APPLE_PIE.get(), 1.f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.SWEET_BERRY_CHEESECAKE.get(), 1.f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.CHOCOLATE_PIE.get(), 1.f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.DUMPLINGS.get(), 1.f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.STUFFED_PUMPKIN.get(), 1.f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.BROWN_MUSHROOM_COLONY.get(), 1.f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.RED_MUSHROOM_COLONY.get(), 1.f);
    }

    protected void registerLootTable() {
        Set<Identifier> villageHouseChestsId = Sets.newHashSet(
                LootTables.VILLAGE_PLAINS_CHEST,
                LootTables.VILLAGE_SAVANNA_HOUSE_CHEST,
                LootTables.VILLAGE_SNOWY_HOUSE_CHEST,
                LootTables.VILLAGE_TAIGA_HOUSE_CHEST,
                LootTables.VILLAGE_DESERT_HOUSE_CHEST);
        Set<Identifier> scavengingEntityIdList = Sets.newHashSet(
                EntityType.PIG.getLootTableId(),
                EntityType.HOGLIN.getLootTableId(),
                EntityType.CHICKEN.getLootTableId(),
                EntityType.COW.getLootTableId(),
                EntityType.DONKEY.getLootTableId(),
                EntityType.HORSE.getLootTableId(),
                EntityType.LLAMA.getLootTableId(),
                EntityType.MULE.getLootTableId(),
                EntityType.RABBIT.getLootTableId(),
                EntityType.SHULKER.getLootTableId(),
                EntityType.SPIDER.getLootTableId());
        Set<Identifier> addItemLootBlockIdList = Sets.newHashSet(
                Blocks.GRASS.getLootTableId(),
                Blocks.TALL_GRASS.getLootTableId(),
                Blocks.WHEAT.getLootTableId());

        LootTableLoadingCallback.EVENT.register((resourceManager, manager, id, supplier, setter) -> {
            Identifier injectId = new Identifier(FarmersDelightMod.MOD_ID, "inject/" + id.getPath());
            if (scavengingEntityIdList.contains(id)) {
                supplier.withPool(LootPool.builder().with(LootTableEntry.builder(injectId)).build());
            }

            if (addItemLootBlockIdList.contains(id)) {
                supplier.withPool(LootPool.builder().with(LootTableEntry.builder(injectId)).build());
            }

            if (villageHouseChestsId.contains(id)) {
                supplier.withPool(LootPool.builder().with(LootTableEntry.builder(injectId).weight(1).quality(0)).build());
            }

            if (LootTables.SHIPWRECK_SUPPLY_CHEST.equals(id)) {
                supplier.withPool(LootPool.builder().with(LootTableEntry.builder(injectId).weight(1).quality(0)).build());
            }
        });
    }

    protected void registerDispenserBehavior() {
        CuttingBoardDispenseBehavior.registerBehaviour(Items.WOODEN_PICKAXE, new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(Items.WOODEN_AXE, new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(Items.WOODEN_SHOVEL, new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(Items.STONE_PICKAXE, new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(Items.STONE_AXE, new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(Items.STONE_SHOVEL, new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(Items.IRON_PICKAXE, new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(Items.IRON_AXE, new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(Items.IRON_SHOVEL, new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(Items.DIAMOND_PICKAXE, new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(Items.DIAMOND_AXE, new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(Items.DIAMOND_SHOVEL, new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(Items.GOLDEN_PICKAXE, new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(Items.GOLDEN_AXE, new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(Items.GOLDEN_SHOVEL, new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(Items.NETHERITE_PICKAXE, new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(Items.NETHERITE_AXE, new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(Items.NETHERITE_SHOVEL, new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(Items.SHEARS, new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(ItemsRegistry.FLINT_KNIFE.get(), new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(ItemsRegistry.IRON_KNIFE.get(), new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(ItemsRegistry.DIAMOND_KNIFE.get(), new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(ItemsRegistry.GOLDEN_KNIFE.get(), new CuttingBoardDispenseBehavior());
        CuttingBoardDispenseBehavior.registerBehaviour(ItemsRegistry.NETHERITE_KNIFE.get(), new CuttingBoardDispenseBehavior());
    }
}