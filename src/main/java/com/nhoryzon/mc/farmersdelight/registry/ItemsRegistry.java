package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.item.ConsumableItem;
import com.nhoryzon.mc.farmersdelight.item.DogFoodItem;
import com.nhoryzon.mc.farmersdelight.item.HorseFeedItem;
import com.nhoryzon.mc.farmersdelight.item.HotCocoaItem;
import com.nhoryzon.mc.farmersdelight.item.KnifeItem;
import com.nhoryzon.mc.farmersdelight.item.MilkBottleItem;
import com.nhoryzon.mc.farmersdelight.item.ModBlockItem;
import com.nhoryzon.mc.farmersdelight.item.ModItemSettings;
import com.nhoryzon.mc.farmersdelight.item.Foods;
import com.nhoryzon.mc.farmersdelight.item.MushroomColonyBlockItem;
import com.nhoryzon.mc.farmersdelight.item.RopeItem;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public enum ItemsRegistry {
    /* Block Items */

    STOVE("stove", () -> new ModBlockItem(BlocksRegistry.STOVE.get())),
    COOKING_POT("cooking_pot", () -> new ModBlockItem(BlocksRegistry.COOKING_POT.get())),
    BASKET("basket", () -> new ModBlockItem(BlocksRegistry.BASKET.get())),
    CUTTING_BOARD("cutting_board", () -> new ModBlockItem(BlocksRegistry.CUTTING_BOARD.get())),

    WILD_CABBAGES("wild_cabbages", () -> new ModBlockItem(BlocksRegistry.WILD_CABBAGES.get())),
    WILD_ONIONS("wild_onions", () -> new ModBlockItem(BlocksRegistry.WILD_ONIONS.get())),
    WILD_TOMATOES("wild_tomatoes", () -> new ModBlockItem(BlocksRegistry.WILD_TOMATOES.get())),
    WILD_CARROTS("wild_carrots", () -> new ModBlockItem(BlocksRegistry.WILD_CARROTS.get())),
    WILD_POTATOES("wild_potatoes", () -> new ModBlockItem(BlocksRegistry.WILD_POTATOES.get())),
    WILD_BEETROOTS("wild_beetroots", () -> new ModBlockItem(BlocksRegistry.WILD_BEETROOTS.get())),
    WILD_RICE("wild_rice", () -> new ModBlockItem(BlocksRegistry.WILD_RICE.get())),

    BROWN_MUSHROOM_COLONY("brown_mushroom_colony", () -> new MushroomColonyBlockItem(BlocksRegistry.BROWN_MUSHROOM_COLONY.get())),
    RED_MUSHROOM_COLONY("red_mushroom_colony", () -> new MushroomColonyBlockItem(BlocksRegistry.RED_MUSHROOM_COLONY.get())),

    CARROT_CRATE("carrot_crate", () -> new ModBlockItem(BlocksRegistry.CARROT_CRATE.get())),
    POTATO_CRATE("potato_crate", () -> new ModBlockItem(BlocksRegistry.POTATO_CRATE.get())),
    BEETROOT_CRATE("beetroot_crate", () -> new ModBlockItem(BlocksRegistry.BEETROOT_CRATE.get())),
    CABBAGE_CRATE("cabbage_crate", () -> new ModBlockItem(BlocksRegistry.CABBAGE_CRATE.get())),
    TOMATO_CRATE("tomato_crate", () -> new ModBlockItem(BlocksRegistry.TOMATO_CRATE.get())),
    ONION_CRATE("onion_crate", () -> new ModBlockItem(BlocksRegistry.ONION_CRATE.get())),
    RICE_BALE("rice_bale", () -> new ModBlockItem(BlocksRegistry.RICE_BALE.get())),
    RICE_BAG("rice_bag", () -> new ModBlockItem(BlocksRegistry.RICE_BAG.get())),
    STRAW_BALE("straw_bale", () -> new ModBlockItem(BlocksRegistry.STRAW_BALE.get())),

    ROPE("rope", RopeItem::new, 200),
    SAFETY_NET("safety_net", () -> new ModBlockItem(BlocksRegistry.SAFETY_NET.get()), 200),
    OAK_PANTRY("oak_pantry", () -> new ModBlockItem(BlocksRegistry.OAK_PANTRY.get()), 300),
    BIRCH_PANTRY("birch_pantry", () -> new ModBlockItem(BlocksRegistry.BIRCH_PANTRY.get()), 300),
    SPRUCE_PANTRY("spruce_pantry", () -> new ModBlockItem(BlocksRegistry.SPRUCE_PANTRY.get()), 300),
    JUNGLE_PANTRY("jungle_pantry", () -> new ModBlockItem(BlocksRegistry.JUNGLE_PANTRY.get()), 300),
    ACACIA_PANTRY("acacia_pantry", () -> new ModBlockItem(BlocksRegistry.ACACIA_PANTRY.get()), 300),
    DARK_OAK_PANTRY("dark_oak_pantry", () -> new ModBlockItem(BlocksRegistry.DARK_OAK_PANTRY.get()), 300),
    CRIMSON_PANTRY("crimson_pantry", () -> new ModBlockItem(BlocksRegistry.CRIMSON_PANTRY.get())),
    WARPED_PANTRY("warped_pantry", () -> new ModBlockItem(BlocksRegistry.WARPED_PANTRY.get())),
    TATAMI("tatami", () -> new ModBlockItem(BlocksRegistry.TATAMI.get()), 400),
    FULL_TATAMI_MAT("full_tatami_mat", () -> new ModBlockItem(BlocksRegistry.FULL_TATAMI_MAT.get()), 200),
    HALF_TATAMI_MAT("half_tatami_mat", () -> new ModBlockItem(BlocksRegistry.HALF_TATAMI_MAT.get()), 100),

    ORGANIC_COMPOST("organic_compost", () -> new ModBlockItem(BlocksRegistry.ORGANIC_COMPOST.get())),
    RICH_SOIL("rich_soil", () -> new ModBlockItem(BlocksRegistry.RICH_SOIL.get())),
    RICH_SOIL_FARMLAND("rich_soil_farmland", () -> new ModBlockItem(BlocksRegistry.RICH_SOIL_FARMLAND.get())),

    APPLE_PIE("apple_pie", () -> new ModBlockItem(BlocksRegistry.APPLE_PIE.get())),
    SWEET_BERRY_CHEESECAKE("sweet_berry_cheesecake", () -> new ModBlockItem(BlocksRegistry.SWEET_BERRY_CHEESECAKE.get())),
    CHOCOLATE_PIE("chocolate_pie", () -> new ModBlockItem(BlocksRegistry.CHOCOLATE_PIE.get())),

    ROAST_CHICKEN_BLOCK("roast_chicken_block", () -> new ModBlockItem(BlocksRegistry.ROAST_CHICKEN_BLOCK.get())),
    STUFFED_PUMPKIN_BLOCK("stuffed_pumpkin_block", () -> new ModBlockItem(BlocksRegistry.STUFFED_PUMPKIN_BLOCK.get())),
    HONEY_GLAZED_HAM_BLOCK("honey_glazed_ham_block", () -> new ModBlockItem(BlocksRegistry.HONEY_GLAZED_HAM_BLOCK.get())),
    SHEPHERDS_PIE_BLOCK("shepherds_pie_block", () -> new ModBlockItem(BlocksRegistry.SHEPHERDS_PIE_BLOCK.get())),

    /* Items */

    FLINT_KNIFE("flint_knife", () -> new KnifeItem(com.nhoryzon.mc.farmersdelight.item.ToolMaterials.FLINT, new ModItemSettings())),
    IRON_KNIFE("iron_knife", () -> new KnifeItem(ToolMaterials.IRON, new ModItemSettings())),
    GOLDEN_KNIFE("golden_knife", () -> new KnifeItem(ToolMaterials.GOLD, new ModItemSettings())),
    DIAMOND_KNIFE("diamond_knife", () -> new KnifeItem(ToolMaterials.DIAMOND, new ModItemSettings())),
    NETHERITE_KNIFE("netherite_knife", () -> new KnifeItem(ToolMaterials.NETHERITE, new ModItemSettings())),

    STRAW("straw", () -> new Item(new ModItemSettings()), 100),
    CANVAS("canvas", () -> new Item(new ModItemSettings()), 400),
    TREE_BARK("tree_bark", () -> new Item(new ModItemSettings()), 200),

    CABBAGE("cabbage", () -> new Item(new ModItemSettings().food(Foods.CABBAGE.get()))),
    CABBAGE_SEEDS("cabbage_seeds", () -> new BlockItem(BlocksRegistry.CABBAGE_CROP.get(), new ModItemSettings())),
    TOMATO("tomato", () -> new Item(new ModItemSettings().food(Foods.TOMATO.get()))),
    TOMATO_SEED("tomato_seeds", () -> new BlockItem(BlocksRegistry.TOMATO_CROP.get(), new ModItemSettings())),
    ONION("onion", () -> new BlockItem(BlocksRegistry.ONION_CROP.get(), new ModItemSettings().food(Foods.ONION.get()))),
    RICE_PANICLE("rice_panicle", () -> new Item(new ModItemSettings())),
    RICE("rice", () -> new BlockItem(BlocksRegistry.RICE_CROP.get(), new ModItemSettings())),

    FRIED_EGG("fried_egg", () -> new Item(new ModItemSettings().food(Foods.FRIED_EGG.get()))),
    MILK_BOTTLE("milk_bottle", () -> new MilkBottleItem(new ModItemSettings().recipeRemainder(Items.GLASS_BOTTLE).maxCount(16))),
    HOT_COCOA("hot_cocoa", () -> new HotCocoaItem(new ModItemSettings().recipeRemainder(Items.GLASS_BOTTLE).maxCount(16))),
    TOMATO_SAUCE("tomato_sauce", () -> new ConsumableItem(new ModItemSettings().food(Foods.TOMATO_SAUCE.get()).recipeRemainder(Items.BOWL))),
    WHEAT_DOUGH("wheat_dough", () -> new Item(new ModItemSettings().food(Foods.WHEAT_DOUGH.get()))),
    RAW_PASTA("raw_pasta", () -> new Item(new ModItemSettings().food(Foods.RAW_PASTA.get()))),
    PUMPKIN_SLICE("pumpkin_slice", () -> new Item(new ModItemSettings().food(Foods.PUMPKIN_SLICE.get()))),
    CABBAGE_LEAF("cabbage_leaf", () -> new Item(new ModItemSettings().food(Foods.CABBAGE_LEAF.get()))),
    MINCED_BEEF("minced_beef", () -> new Item(new ModItemSettings().food(Foods.MINCED_BEEF.get()))),
    BEEF_PATTY("beef_patty", () -> new Item(new ModItemSettings().food(Foods.BEEF_PATTY.get()))),
    CHICKEN_CUTS("chicken_cuts", () -> new Item(new ModItemSettings().food(Foods.CHICKEN_CUTS.get()))),
    COOKED_CHICKEN_CUTS("cooked_chicken_cuts", () -> new Item(new ModItemSettings().food(Foods.COOKED_CHICKEN_CUTS.get()))),
    BACON("bacon", () -> new Item(new ModItemSettings().food(Foods.BACON.get()))),
    COOKED_BACON("cooked_bacon", () -> new Item(new ModItemSettings().food(Foods.COOKED_BACON.get()))),
    COD_SLICE("cod_slice", () -> new Item(new ModItemSettings().food(Foods.COD_SLICE.get()))),
    COOKED_COD_SLICE("cooked_cod_slice", () -> new Item(new ModItemSettings().food(Foods.COOKED_COD_SLICE.get()))),
    SALMON_SLICE("salmon_slice", () -> new Item(new ModItemSettings().food(Foods.SALMON_SLICE.get()))),
    COOKED_SALMON_SLICE("cooked_salmon_slice", () -> new Item(new ModItemSettings().food(Foods.COOKED_SALMON_SLICE.get()))),
    MUTTON_CHOPS("mutton_chops", () -> new Item(new ModItemSettings().food(Foods.MUTTON_CHOP.get()))),
    COOKED_MUTTON_CHOPS("cooked_mutton_chops", () -> new Item(new ModItemSettings().food(Foods.COOKED_MUTTON_CHOP.get()))),
    HAM("ham", () -> new Item(new ModItemSettings().food(Foods.HAM.get()))),
    SMOKED_HAM("smoked_ham", () -> new Item(new ModItemSettings().food(Foods.SMOKED_HAM.get()))),

    PIE_CRUST("pie_crust", () -> new Item(new ModItemSettings().food(Foods.PIE_CRUST.get()))),
    CAKE_SLICE("cake_slice", () -> new Item(new ModItemSettings().food(Foods.CAKE_SLICE.get()))),
    APPLE_PIE_SLICE("apple_pie_slice", () -> new Item(new ModItemSettings().food(Foods.PIE_SLICE.get()))),
    SWEET_BERRY_CHEESECAKE_SLICE("sweet_berry_cheesecake_slice", () -> new Item(new ModItemSettings().food(Foods.PIE_SLICE.get()))),
    CHOCOLATE_PIE_SLICE("chocolate_pie_slice", () -> new Item(new ModItemSettings().food(Foods.PIE_SLICE.get()))),
    SWEET_BERRY_COOKIE("sweet_berry_cookie", () -> new Item(new ModItemSettings().food(Foods.COOKIES.get()))),
    HONEY_COOKIE("honey_cookie", () -> new Item(new ModItemSettings().food(Foods.COOKIES.get()))),
    MELON_POPSICLE("melon_popsicle", () -> new Item(new ModItemSettings().food(Foods.POPSICLE.get()))),
    FRUIT_SALAD("fruit_salad", () -> new ConsumableItem(new ModItemSettings().food(Foods.FRUIT_SALAD.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    MIXED_SALAD("mixed_salad", () -> new ConsumableItem(new ModItemSettings().food(Foods.MIXED_SALAD.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    NETHER_SALAD("nether_salad", () -> new ConsumableItem(new ModItemSettings().food(Foods.NETHER_SALAD.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    BARBECUE_STICK("barbecue_stick", () -> new Item(new ModItemSettings().food(Foods.BARBECUE_STICK.get()))),
    EGG_SANDWICH("egg_sandwich", () -> new Item(new ModItemSettings().food(Foods.EGG_SANDWICH.get()))),
    CHICKEN_SANDWICH("chicken_sandwich", () -> new Item(new ModItemSettings().food(Foods.CHICKEN_SANDWICH.get()))),
    HAMBURGER("hamburger", () -> new Item(new ModItemSettings().food(Foods.HAMBURGER.get()))),
    BACON_SANDWICH("bacon_sandwich", () -> new Item(new ModItemSettings().food(Foods.BACON_SANDWICH.get()))),
    MUTTON_WRAP("mutton_wrap", () -> new Item(new ModItemSettings().food(Foods.MUTTON_WRAP.get()))),
    DUMPLINGS("dumplings", () -> new Item(new ModItemSettings().food(Foods.DUMPLINGS.get()))),
    STUFFED_POTATO("stuffed_potato", () -> new Item(new ModItemSettings().food(Foods.STUFFED_POTATO.get()))),
    CABBAGE_ROLLS("cabbage_rolls", () -> new Item(new ModItemSettings().food(Foods.CABBAGE_ROLLS.get()))),

    COOKED_RICE("cooked_rice", () -> new ConsumableItem(new ModItemSettings().food(Foods.COOKED_RICE.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    BEEF_STEW("beef_stew", () -> new ConsumableItem(new ModItemSettings().food(Foods.BEEF_STEW.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    CHICKEN_SOUP("chicken_soup", () -> new ConsumableItem(new ModItemSettings().food(Foods.CHICKEN_SOUP.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    VEGETABLE_SOUP("vegetable_soup", () -> new ConsumableItem(new ModItemSettings().food(Foods.VEGETABLE_SOUP.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    FISH_STEW("fish_stew", () -> new ConsumableItem(new ModItemSettings().food(Foods.FISH_STEW.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    FRIED_RICE("fried_rice", () -> new ConsumableItem(new ModItemSettings().food(Foods.FRIED_RICE.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    PUMPKIN_SOUP("pumpkin_soup", () -> new ConsumableItem(new ModItemSettings().food(Foods.PUMPKIN_SOUP.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    BAKED_COD_STEW("baked_cod_stew", () -> new ConsumableItem(new ModItemSettings().food(Foods.BAKED_COD_STEW.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    NOODLE_SOUP("noodle_soup", () -> new ConsumableItem(new ModItemSettings().food(Foods.NOODLE_SOUP.get()).recipeRemainder(Items.BOWL).maxCount(16))),

    PASTA_WITH_MEATBALLS("pasta_with_meatballs", () -> new ConsumableItem(new ModItemSettings().food(Foods.PASTA_WITH_MEATBALLS.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    PASTA_WITH_MUTTON_CHOP("pasta_with_mutton_chop", () -> new ConsumableItem(new ModItemSettings().food(Foods.PASTA_WITH_MUTTON_CHOP.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    ROASTED_MUTTON_CHOPS("roasted_mutton_chops", () -> new ConsumableItem(new ModItemSettings().food(Foods.ROASTED_MUTTON_CHOPS.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    VEGETABLE_NOODLES("vegetable_noodles", () -> new ConsumableItem(new ModItemSettings().food(Foods.VEGETABLE_NOODLES.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    STEAK_AND_POTATOES("steak_and_potatoes", () -> new ConsumableItem(new ModItemSettings().food(Foods.STEAK_AND_POTATOES.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    RATATOUILLE("ratatouille", () -> new ConsumableItem(new ModItemSettings().food(Foods.RATATOUILLE.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    SQUID_INK_PASTA("squid_ink_pasta", () -> new ConsumableItem(new ModItemSettings().food(Foods.SQUID_INK_PASTA.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    GRILLED_SALMON("grilled_salmon", () -> new ConsumableItem(new ModItemSettings().food(Foods.GRILLED_SALMON.get()).recipeRemainder(Items.BOWL).maxCount(16))),

    ROAST_CHICKEN("roast_chicken", () -> new ConsumableItem(new ModItemSettings().food(Foods.ROAST_CHICKEN.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    STUFFED_PUMPKIN("stuffed_pumpkin", () -> new ConsumableItem(new ModItemSettings().food(Foods.STUFFED_PUMPKIN.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    HONEY_GLAZED_HAM("honey_glazed_ham", () -> new ConsumableItem(new ModItemSettings().food(Foods.HONEY_GLAZED_HAM.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    SHEPHERDS_PIE("shepherds_pie", () -> new ConsumableItem(new ModItemSettings().food(Foods.SHEPHERDS_PIE.get()).recipeRemainder(Items.BOWL).maxCount(16))),

    DOG_FOOD("dog_food", () -> new DogFoodItem(new ModItemSettings().food(Foods.DOG_FOOD.get()).recipeRemainder(Items.BOWL).maxCount(16))),
    HORSE_FEED("horse_feed", () -> new HorseFeedItem(new ModItemSettings().maxCount(16)));

    private final String pathName;
    private final Supplier<Item> itemSupplier;
    private final Integer burnTime;
    private Item item;

    ItemsRegistry(String pathName, Supplier<Item> itemSupplier) {
        this(pathName, itemSupplier, null);
    }

    ItemsRegistry(String pathName, Supplier<Item> itemSupplier, Integer burnTime) {
        this.pathName = pathName;
        this.itemSupplier = itemSupplier;
        this.burnTime = burnTime;
    }

    public static void registerAll() {
        for (ItemsRegistry value : values()) {
            Registry.register(Registry.ITEM, new Identifier(FarmersDelightMod.MOD_ID, value.pathName), value.get());
            if (value.burnTime != null) {
                FuelRegistry.INSTANCE.add(value.get(), value.burnTime);
            }
        }
    }

    public Item get() {
        if (item == null) {
            item = itemSupplier.get();
        }
        return item;
    }
}