package com.nhoryzon.mc.farmersdelight;

import com.nhoryzon.mc.farmersdelight.entity.block.dispenser.CuttingBoardDispenseBehavior;
import com.nhoryzon.mc.farmersdelight.event.CuttingBoardEventListener;
import com.nhoryzon.mc.farmersdelight.event.KnivesEventListener;
import com.nhoryzon.mc.farmersdelight.event.LivingEntityFeedItemEventListener;
import com.nhoryzon.mc.farmersdelight.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.*;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;

import java.util.Arrays;
import java.util.Set;

/**
 * This fabric port of Farmer's Delight will <b>NOT</b> implement these features :
 * <ul>
 *     <li>the "Nourished Hunger" overlay</li>
 *     <li>the possibility to disable vanilla "Crops Crates"</li>
 *     <li>the integrations of others mods like : CraftTweaker, BotanyPots, Create, Immersive Engineering and Silent Gear</li>
 * </ul>
 */
public class FarmersDelightMod implements ModInitializer {

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
        /* ConfiguredFeaturesRegistry.registerAll(); */

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
        PlayerBlockBreakEvents.AFTER.register(KnivesEventListener.INSTANCE);
        UseBlockCallback.EVENT.register(KnivesEventListener.INSTANCE);
        UseBlockCallback.EVENT.register(CuttingBoardEventListener.INSTANCE);
        UseEntityCallback.EVENT.register(LivingEntityFeedItemEventListener.INSTANCE);
    }

    @SuppressWarnings("deprecation")
    protected void registerBiomeModifications() {
        /*
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
        */
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
        Set<Identifier> villageHouseChestsId = Set.of(
                LootTables.VILLAGE_PLAINS_CHEST,
                LootTables.VILLAGE_SAVANNA_HOUSE_CHEST,
                LootTables.VILLAGE_SNOWY_HOUSE_CHEST,
                LootTables.VILLAGE_TAIGA_HOUSE_CHEST,
                LootTables.VILLAGE_DESERT_HOUSE_CHEST);
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