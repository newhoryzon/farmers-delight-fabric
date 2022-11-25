package com.nhoryzon.mc.farmersdelight;

import com.mojang.datafixers.util.Pair;
import com.nhoryzon.mc.farmersdelight.entity.RottenTomatoEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.dispenser.CuttingBoardDispenseBehavior;
import com.nhoryzon.mc.farmersdelight.event.CuttingBoardEventListener;
import com.nhoryzon.mc.farmersdelight.event.KnivesEventListener;
import com.nhoryzon.mc.farmersdelight.event.LivingEntityFeedItemEventListener;
import com.nhoryzon.mc.farmersdelight.mixin.accessors.ParrotsTamingIngredientsAccessorMixin;
import com.nhoryzon.mc.farmersdelight.mixin.accessors.StructurePoolAccessorMixin;
import com.nhoryzon.mc.farmersdelight.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Position;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * This fabric port of Farmer's Delight is <b>NOT</b> implementing these features :
 * <ul>
 *     <li>visual cooking on pan in hand</li>
 * </ul>
 */
public class FarmersDelightMod implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Farmer's Delight");

    public static final String MOD_ID = "farmersdelight";

    public static Configuration CONFIG = new Configuration();

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "main"),
            () -> new ItemStack(ItemsRegistry.STOVE.get()));

    public static MutableText i18n(String key, Object... args) {
        return Text.translatable(MOD_ID + "." + key, args);
    }

    @Override
    public void onInitialize() {
        initConfiguration();

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
        EntityTypesRegistry.registerAll();

        registerBiomeModifications();
        registerCompostables();
        registerEventListeners();
        registerLootTable();
        registerDispenserBehavior();
        registerVillagerTradeOffer();

        ParrotsTamingIngredientsAccessorMixin.getTamingIngredients().addAll(List.of(
                ItemsRegistry.CABBAGE_SEEDS.get(),
                ItemsRegistry.TOMATO_SEEDS.get(),
                ItemsRegistry.RICE.get()));
    }

    protected void initConfiguration() {
        CONFIG = Configuration.load();

        ResourceConditions.register(new Identifier(MOD_ID, "vanilla_crates_enabled"),
                jsonObject -> FarmersDelightMod.CONFIG.isEnableVanillaCropCrates());

        if (FarmersDelightMod.CONFIG.isGenerateVillageCompostHeaps()) {
            List<Pair<String, Integer>> compostPileList = List.of(
                    Pair.of("plains", 5),
                    Pair.of("savanna", 4),
                    Pair.of("snowy", 3),
                    Pair.of("taiga", 4),
                    Pair.of("desert", 3));
            ServerLifecycleEvents.SERVER_STARTING.register(server -> compostPileList.forEach(villageType -> {
                LOGGER.info("Registering compost heaps in village type of {}", villageType.getFirst());
                Identifier compostPileId = new Identifier(MOD_ID, "village/houses/" + villageType.getFirst() + "_compost_pile");
                Identifier villageHousePoolId = new Identifier("minecraft:village/" + villageType.getFirst() + "/houses");
                addToStructurePool(server, villageHousePoolId, compostPileId, villageType.getSecond());
            }));
        }
    }

    protected void addToStructurePool(MinecraftServer server, Identifier poolIdentifier, Identifier nbtIdentifier, int weight) {
        RegistryEntry<StructureProcessorList> emptyProcessorList = server.getRegistryManager().get(Registry.STRUCTURE_PROCESSOR_LIST_KEY)
                .entryOf(RegistryKey.of(Registry.STRUCTURE_PROCESSOR_LIST_KEY, new Identifier("minecraft", "empty")));

        server.getRegistryManager().get(Registry.STRUCTURE_POOL_KEY).stream()
                .filter(structurePoolReference -> structurePoolReference.getId().equals(poolIdentifier))
                .findFirst().ifPresentOrElse(structurePool -> {
                    SinglePoolElement compostPilePool = StructurePoolElement.ofProcessedSingle(nbtIdentifier.toString(), emptyProcessorList)
                            .apply(StructurePool.Projection.RIGID);
                    List<Pair<StructurePoolElement, Integer>> elementCounts = new ArrayList<>(((StructurePoolAccessorMixin) structurePool).getElementCounts());
                    elementCounts.add(Pair.of(compostPilePool, weight));
                    ((StructurePoolAccessorMixin) structurePool).setElementCounts(elementCounts);

                    IntStream.range(0, weight).forEach(value -> ((StructurePoolAccessorMixin) structurePool).getElements().add(compostPilePool));
                }, () -> LOGGER.warn("No structure pool found for {}, no compost heaps will be added on it.", poolIdentifier));
    }

    protected void registerEventListeners() {
        PlayerBlockBreakEvents.AFTER.register(KnivesEventListener.INSTANCE);
        UseBlockCallback.EVENT.register(KnivesEventListener.INSTANCE);
        UseBlockCallback.EVENT.register(CuttingBoardEventListener.INSTANCE);
        UseEntityCallback.EVENT.register(LivingEntityFeedItemEventListener.INSTANCE);
    }

    protected void registerBiomeModifications() {
        if (FarmersDelightMod.CONFIG.isGenerateWildBeetroots()) {
            BiomeModifications.addFeature(context -> context.getBiomeKey().equals(BiomeKeys.BEACH), GenerationStep.Feature.VEGETAL_DECORATION,
                    ConfiguredFeaturesRegistry.PATCH_WILD_BEETROOTS.key());
        }

        if (FarmersDelightMod.CONFIG.isGenerateWildCabbages()) {
        BiomeModifications.addFeature(context -> context.getBiomeKey().equals(BiomeKeys.BEACH), GenerationStep.Feature.VEGETAL_DECORATION,
                ConfiguredFeaturesRegistry.PATCH_WILD_CABBAGES.key());
        }

        if (FarmersDelightMod.CONFIG.isGenerateWildRice()) {
            BiomeModifications.addFeature(context -> BiomeSelectors.includeByKey(BiomeKeys.SWAMP, BiomeKeys.JUNGLE).test(context),
                    GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeaturesRegistry.PATCH_WILD_RICE.key());
        }

        if (FarmersDelightMod.CONFIG.isGenerateWildTomatoes()) {
            BiomeModifications.addFeature(context -> context.getBiome().getTemperature() >= 1.f, GenerationStep.Feature.VEGETAL_DECORATION,
                    ConfiguredFeaturesRegistry.PATCH_WILD_TOMATOES.key());
        }

        if (FarmersDelightMod.CONFIG.isGenerateWildCarrots()) {
            BiomeModifications.addFeature(context -> context.getBiome().getTemperature() > .3f && context.getBiome().getTemperature() < 1.f,
                    GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeaturesRegistry.PATCH_WILD_CARROTS.key());
        }

        if (FarmersDelightMod.CONFIG.isGenerateWildOnions()) {
            BiomeModifications.addFeature(context -> context.getBiome().getTemperature() > .3f && context.getBiome().getTemperature() < 1.f,
                    GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeaturesRegistry.PATCH_WILD_ONIONS.key());
        }

        if (FarmersDelightMod.CONFIG.isGenerateWildPotatoes()) {
            BiomeModifications.addFeature(context -> context.getBiome().getTemperature() > .0f && context.getBiome().getTemperature() < .3f,
                    GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeaturesRegistry.PATCH_WILD_POTATOES.key());
        }
    }

    protected void registerCompostables() {
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.TREE_BARK.get(), .3f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.STRAW.get(), .3f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.CABBAGE_SEEDS.get(), .3f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.TOMATO_SEEDS.get(), .3f);
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
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.ROTTEN_TOMATO.get(), .85f);

        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.APPLE_PIE.get(), 1.f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.SWEET_BERRY_CHEESECAKE.get(), 1.f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.CHOCOLATE_PIE.get(), 1.f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.DUMPLINGS.get(), 1.f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.STUFFED_PUMPKIN.get(), 1.f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.BROWN_MUSHROOM_COLONY.get(), 1.f);
        CompostingChanceRegistry.INSTANCE.add(ItemsRegistry.RED_MUSHROOM_COLONY.get(), 1.f);
    }

    protected void registerLootTable() {
        Set<Identifier> chestsId = Set.of(
                LootTables.ABANDONED_MINESHAFT_CHEST,
                LootTables.BASTION_HOGLIN_STABLE_CHEST,
                LootTables.BASTION_TREASURE_CHEST,
                LootTables.END_CITY_TREASURE_CHEST,
                LootTables.PILLAGER_OUTPOST_CHEST,
                LootTables.RUINED_PORTAL_CHEST,
                LootTables.SHIPWRECK_SUPPLY_CHEST,
                LootTables.SIMPLE_DUNGEON_CHEST,
                LootTables.VILLAGE_PLAINS_CHEST,
                LootTables.VILLAGE_SAVANNA_HOUSE_CHEST,
                LootTables.VILLAGE_SNOWY_HOUSE_CHEST,
                LootTables.VILLAGE_TAIGA_HOUSE_CHEST,
                LootTables.VILLAGE_DESERT_HOUSE_CHEST,
                LootTables.VILLAGE_BUTCHER_CHEST);
        Set<Identifier> scavengingEntityIdList = Set.of(
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
        Set<Identifier> addItemLootBlockIdList = Set.of(
                Blocks.GRASS.getLootTableId(),
                Blocks.TALL_GRASS.getLootTableId(),
                Blocks.WHEAT.getLootTableId());

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            Identifier injectId = new Identifier(FarmersDelightMod.MOD_ID, "inject/" + id.getPath());
            if (scavengingEntityIdList.contains(id) || addItemLootBlockIdList.contains(id)) {
                tableBuilder.pool(LootPool.builder().with(LootTableEntry.builder(injectId)).build());
            }

            if (chestsId.contains(id) && FarmersDelightMod.CONFIG.isGenerateFDChestLoot()) {
                tableBuilder.pool(LootPool.builder().with(LootTableEntry.builder(injectId).weight(1).quality(0)).build());
            }
        });
    }

    protected void registerDispenserBehavior() {
        if (FarmersDelightMod.CONFIG.isDispenserToolsCuttingBoard()) {
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

        DispenserBlock.registerBehavior(ItemsRegistry.ROTTEN_TOMATO.get(), new ProjectileDispenserBehavior() {
            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                return new RottenTomatoEntity(world, position.getX(), position.getY(), position.getZ());
            }
        });
    }

    protected void registerVillagerTradeOffer() {
        if (FarmersDelightMod.CONFIG.isFarmersBuyFDCrops()) {
            TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 1,
                    factories -> new TradeOffer(new ItemStack(ItemsRegistry.ONION.get(), 26),
                            new ItemStack(Items.EMERALD), 16, 2, .05f));
            TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 1,
                    factories -> new TradeOffer(new ItemStack(ItemsRegistry.TOMATO.get(), 26),
                            new ItemStack(Items.EMERALD), 16, 2, .05f));
            TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 2,
                    factories -> new TradeOffer(new ItemStack(ItemsRegistry.CABBAGE.get(), 16),
                            new ItemStack(Items.EMERALD), 16, 5, .05f));
            TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 2,
                    factories -> new TradeOffer(new ItemStack(ItemsRegistry.RICE.get(), 20),
                            new ItemStack(Items.EMERALD), 16, 5, .05f));
        }

        if (FarmersDelightMod.CONFIG.isWanderingTraderSellsFDItems()) {
            TradeOfferHelper.registerWanderingTraderOffers(1,
                    factories -> new TradeOffer(new ItemStack(ItemsRegistry.CABBAGE_SEEDS.get()),
                            new ItemStack(Items.EMERALD), 1, 12, .05f));
            TradeOfferHelper.registerWanderingTraderOffers(1,
                    factories -> new TradeOffer(new ItemStack(ItemsRegistry.TOMATO_SEEDS.get()),
                            new ItemStack(Items.EMERALD), 1, 12, .05f));
            TradeOfferHelper.registerWanderingTraderOffers(1,
                    factories -> new TradeOffer(new ItemStack(ItemsRegistry.RICE.get()),
                            new ItemStack(Items.EMERALD), 1, 12, .05f));
            TradeOfferHelper.registerWanderingTraderOffers(1,
                    factories -> new TradeOffer(new ItemStack(ItemsRegistry.ONION.get()),
                            new ItemStack(Items.EMERALD), 1, 12, .05f));
        }
    }
}