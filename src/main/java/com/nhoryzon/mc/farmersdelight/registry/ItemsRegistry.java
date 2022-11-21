package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.item.ConsumableItem;
import com.nhoryzon.mc.farmersdelight.item.DogFoodItem;
import com.nhoryzon.mc.farmersdelight.item.DrinkableItem;
import com.nhoryzon.mc.farmersdelight.item.HorseFeedItem;
import com.nhoryzon.mc.farmersdelight.item.HotCocoaItem;
import com.nhoryzon.mc.farmersdelight.item.KelpRollItem;
import com.nhoryzon.mc.farmersdelight.item.KnifeItem;
import com.nhoryzon.mc.farmersdelight.item.MelonJuiceItem;
import com.nhoryzon.mc.farmersdelight.item.MilkBottleItem;
import com.nhoryzon.mc.farmersdelight.item.ModBlockItem;
import com.nhoryzon.mc.farmersdelight.item.ModItemSettings;
import com.nhoryzon.mc.farmersdelight.item.SkilletItem;
import com.nhoryzon.mc.farmersdelight.item.enumeration.Foods;
import com.nhoryzon.mc.farmersdelight.item.MushroomColonyBlockItem;
import com.nhoryzon.mc.farmersdelight.item.RopeItem;
import com.nhoryzon.mc.farmersdelight.item.RottenTomatoItem;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SignItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public enum ItemsRegistry {

    /* Block Items */

    STOVE("stove", blockItem(BlocksRegistry.STOVE)),
    COOKING_POT("cooking_pot", oneBlockItem(BlocksRegistry.COOKING_POT)),
    SKILLET("skillet", SkilletItem::new),
    BASKET("basket", blockItem(BlocksRegistry.BASKET)),
    CUTTING_BOARD("cutting_board", blockItem(BlocksRegistry.CUTTING_BOARD)),

    SANDY_SHRUB("sandy_shrub", blockItem(BlocksRegistry.SANDY_SHRUB)),
    WILD_CABBAGES("wild_cabbages", blockItem(BlocksRegistry.WILD_CABBAGES)),
    WILD_ONIONS("wild_onions", blockItem(BlocksRegistry.WILD_ONIONS)),
    WILD_TOMATOES("wild_tomatoes", blockItem(BlocksRegistry.WILD_TOMATOES)),
    WILD_CARROTS("wild_carrots", blockItem(BlocksRegistry.WILD_CARROTS)),
    WILD_POTATOES("wild_potatoes", blockItem(BlocksRegistry.WILD_POTATOES)),
    WILD_BEETROOTS("wild_beetroots", blockItem(BlocksRegistry.WILD_BEETROOTS)),
    WILD_RICE("wild_rice", blockItem(BlocksRegistry.WILD_RICE)),

    BROWN_MUSHROOM_COLONY("brown_mushroom_colony", () -> new MushroomColonyBlockItem(BlocksRegistry.BROWN_MUSHROOM_COLONY.get())),
    RED_MUSHROOM_COLONY("red_mushroom_colony", () -> new MushroomColonyBlockItem(BlocksRegistry.RED_MUSHROOM_COLONY.get())),

    CARROT_CRATE("carrot_crate", blockItem(BlocksRegistry.CARROT_CRATE)),
    POTATO_CRATE("potato_crate", blockItem(BlocksRegistry.POTATO_CRATE)),
    BEETROOT_CRATE("beetroot_crate", blockItem(BlocksRegistry.BEETROOT_CRATE)),
    CABBAGE_CRATE("cabbage_crate", blockItem(BlocksRegistry.CABBAGE_CRATE)),
    TOMATO_CRATE("tomato_crate", blockItem(BlocksRegistry.TOMATO_CRATE)),
    ONION_CRATE("onion_crate", blockItem(BlocksRegistry.ONION_CRATE)),
    RICE_BALE("rice_bale", blockItem(BlocksRegistry.RICE_BALE)),
    RICE_BAG("rice_bag", blockItem(BlocksRegistry.RICE_BAG)),
    STRAW_BALE("straw_bale", blockItem(BlocksRegistry.STRAW_BALE)),

    ROPE("rope", RopeItem::new, 200),
    SAFETY_NET("safety_net", blockItem(BlocksRegistry.SAFETY_NET), 200),
    OAK_CABINET("oak_cabinet", blockItem(BlocksRegistry.OAK_CABINET), 300),
    BIRCH_CABINET("birch_cabinet", blockItem(BlocksRegistry.BIRCH_CABINET), 300),
    SPRUCE_CABINET("spruce_cabinet", blockItem(BlocksRegistry.SPRUCE_CABINET), 300),
    JUNGLE_CABINET("jungle_cabinet", blockItem(BlocksRegistry.JUNGLE_CABINET), 300),
    ACACIA_CABINET("acacia_cabinet", blockItem(BlocksRegistry.ACACIA_CABINET), 300),
    DARK_OAK_CABINET("dark_oak_cabinet", blockItem(BlocksRegistry.DARK_OAK_CABINET), 300),
    CRIMSON_CABINET("crimson_cabinet", blockItem(BlocksRegistry.CRIMSON_CABINET)),
    WARPED_CABINET("warped_cabinet", blockItem(BlocksRegistry.WARPED_CABINET)),
    CANVAS_RUG("canvas_rug", blockItem(BlocksRegistry.CANVAS_RUG), 200),
    TATAMI("tatami", blockItem(BlocksRegistry.TATAMI), 400),
    FULL_TATAMI_MAT("full_tatami_mat", blockItem(BlocksRegistry.FULL_TATAMI_MAT), 200),
    HALF_TATAMI_MAT("half_tatami_mat", blockItem(BlocksRegistry.HALF_TATAMI_MAT), 100),

    CANVAS_SIGN("canvas_sign", () -> new SignItem(new ModItemSettings(),
            BlocksRegistry.CANVAS_SIGN.get(), BlocksRegistry.CANVAS_WALL_SIGN.get())),
    WHITE_CANVAS_SIGN("white_canvas_sign", () -> new SignItem(new ModItemSettings(),
            BlocksRegistry.WHITE_CANVAS_SIGN.get(), BlocksRegistry.WHITE_CANVAS_WALL_SIGN.get())),
    ORANGE_CANVAS_SIGN("orange_canvas_sign", () -> new SignItem(new ModItemSettings(),
            BlocksRegistry.ORANGE_CANVAS_SIGN.get(), BlocksRegistry.ORANGE_CANVAS_WALL_SIGN.get())),
    MAGENTA_CANVAS_SIGN("magenta_canvas_sign", () -> new SignItem(new ModItemSettings(),
            BlocksRegistry.MAGENTA_CANVAS_SIGN.get(), BlocksRegistry.MAGENTA_CANVAS_WALL_SIGN.get())),
    LIGHT_BLUE_CANVAS_SIGN("light_blue_canvas_sign", () -> new SignItem(new ModItemSettings(),
            BlocksRegistry.LIGHT_BLUE_CANVAS_SIGN.get(), BlocksRegistry.LIGHT_BLUE_CANVAS_WALL_SIGN.get())),
    YELLOW_CANVAS_SIGN("yellow_canvas_sign", () -> new SignItem(new ModItemSettings(),
            BlocksRegistry.YELLOW_CANVAS_SIGN.get(), BlocksRegistry.YELLOW_CANVAS_WALL_SIGN.get())),
    LIME_CANVAS_SIGN("lime_canvas_sign", () -> new SignItem(new ModItemSettings(),
            BlocksRegistry.LIME_CANVAS_SIGN.get(), BlocksRegistry.LIME_CANVAS_WALL_SIGN.get())),
    PINK_CANVAS_SIGN("pink_canvas_sign", () -> new SignItem(new ModItemSettings(),
            BlocksRegistry.PINK_CANVAS_SIGN.get(), BlocksRegistry.PINK_CANVAS_WALL_SIGN.get())),
    GRAY_CANVAS_SIGN("gray_canvas_sign", () -> new SignItem(new ModItemSettings(),
            BlocksRegistry.GRAY_CANVAS_SIGN.get(), BlocksRegistry.GRAY_CANVAS_WALL_SIGN.get())),
    LIGHT_GRAY_CANVAS_SIGN("light_gray_canvas_sign", () -> new SignItem(new ModItemSettings(),
            BlocksRegistry.LIGHT_GRAY_CANVAS_SIGN.get(), BlocksRegistry.LIGHT_GRAY_CANVAS_WALL_SIGN.get())),
    CYAN_CANVAS_SIGN("cyan_canvas_sign", () -> new SignItem(new ModItemSettings(),
            BlocksRegistry.CYAN_CANVAS_SIGN.get(), BlocksRegistry.CYAN_CANVAS_WALL_SIGN.get())),
    PURPLE_CANVAS_SIGN("purple_canvas_sign", () -> new SignItem(new ModItemSettings(),
            BlocksRegistry.PURPLE_CANVAS_SIGN.get(), BlocksRegistry.PURPLE_CANVAS_WALL_SIGN.get())),
    BLUE_CANVAS_SIGN("blue_canvas_sign", () -> new SignItem(new ModItemSettings(),
            BlocksRegistry.BLUE_CANVAS_SIGN.get(), BlocksRegistry.BLUE_CANVAS_WALL_SIGN.get())),
    BROWN_CANVAS_SIGN("brown_canvas_sign", () -> new SignItem(new ModItemSettings(),
            BlocksRegistry.BROWN_CANVAS_SIGN.get(), BlocksRegistry.BROWN_CANVAS_WALL_SIGN.get())),
    GREEN_CANVAS_SIGN("green_canvas_sign", () -> new SignItem(new ModItemSettings(),
            BlocksRegistry.GREEN_CANVAS_SIGN.get(), BlocksRegistry.GREEN_CANVAS_WALL_SIGN.get())),
    RED_CANVAS_SIGN("red_canvas_sign", () -> new SignItem(new ModItemSettings(),
            BlocksRegistry.RED_CANVAS_SIGN.get(), BlocksRegistry.RED_CANVAS_WALL_SIGN.get())),
    BLACK_CANVAS_SIGN("black_canvas_sign", () -> new SignItem(new ModItemSettings(),
            BlocksRegistry.BLACK_CANVAS_SIGN.get(), BlocksRegistry.BLACK_CANVAS_WALL_SIGN.get())),

    ORGANIC_COMPOST("organic_compost", blockItem(BlocksRegistry.ORGANIC_COMPOST)),
    RICH_SOIL("rich_soil", blockItem(BlocksRegistry.RICH_SOIL)),
    RICH_SOIL_FARMLAND("rich_soil_farmland", blockItem(BlocksRegistry.RICH_SOIL_FARMLAND)),

    APPLE_PIE("apple_pie", blockItem(BlocksRegistry.APPLE_PIE)),
    SWEET_BERRY_CHEESECAKE("sweet_berry_cheesecake", blockItem(BlocksRegistry.SWEET_BERRY_CHEESECAKE)),
    CHOCOLATE_PIE("chocolate_pie", blockItem(BlocksRegistry.CHOCOLATE_PIE)),

    ROAST_CHICKEN_BLOCK("roast_chicken_block", oneBlockItem(BlocksRegistry.ROAST_CHICKEN_BLOCK)),
    STUFFED_PUMPKIN_BLOCK("stuffed_pumpkin_block", oneBlockItem(BlocksRegistry.STUFFED_PUMPKIN_BLOCK)),
    HONEY_GLAZED_HAM_BLOCK("honey_glazed_ham_block", oneBlockItem(BlocksRegistry.HONEY_GLAZED_HAM_BLOCK)),
    SHEPHERDS_PIE_BLOCK("shepherds_pie_block", oneBlockItem(BlocksRegistry.SHEPHERDS_PIE_BLOCK)),
    RICE_ROLL_MEDLEY_BLOCK("rice_roll_medley_block", oneBlockItem(BlocksRegistry.RICE_ROLL_MEDLEY_BLOCK)),

    /* Items */

    FLINT_KNIFE("flint_knife", () -> new KnifeItem(com.nhoryzon.mc.farmersdelight.item.enumeration.ToolMaterials.FLINT)),
    IRON_KNIFE("iron_knife", () -> new KnifeItem(ToolMaterials.IRON)),
    GOLDEN_KNIFE("golden_knife", () -> new KnifeItem(ToolMaterials.GOLD)),
    DIAMOND_KNIFE("diamond_knife", () -> new KnifeItem(ToolMaterials.DIAMOND)),
    NETHERITE_KNIFE("netherite_knife", () -> new KnifeItem(ToolMaterials.NETHERITE)),

    STRAW("straw", item(), 100),
    CANVAS("canvas", item(), 400),
    TREE_BARK("tree_bark", item(), 200),

    CABBAGE("cabbage", foodItem(Foods.CABBAGE)),
    CABBAGE_SEEDS("cabbage_seeds", aliasedBlockItem(BlocksRegistry.CABBAGE_CROP)),
    TOMATO("tomato", foodItem(Foods.TOMATO)),
    TOMATO_SEEDS("tomato_seeds", aliasedBlockItem(BlocksRegistry.BUDDING_TOMATO_CROP)),
    ONION("onion", aliasedFoodBlockItem(BlocksRegistry.ONION_CROP, Foods.ONION)),
    RICE_PANICLE("rice_panicle", item()),
    RICE("rice", aliasedBlockItem(BlocksRegistry.RICE_CROP)),
    ROTTEN_TOMATO("rotten_tomato", RottenTomatoItem::new),

    FRIED_EGG("fried_egg", foodItem(Foods.FRIED_EGG)),
    MILK_BOTTLE("milk_bottle", MilkBottleItem::new),
    HOT_COCOA("hot_cocoa", HotCocoaItem::new),
    APPLE_CIDER("apple_cider", drinkItem(Foods.APPLE_CIDER, Items.GLASS_BOTTLE, 16, true)),
    MELON_JUICE("melon_juice", MelonJuiceItem::new),
    TOMATO_SAUCE("tomato_sauce", foodItem(Foods.TOMATO_SAUCE, Items.BOWL, 64, false)),
    WHEAT_DOUGH("wheat_dough", foodItem(Foods.WHEAT_DOUGH)),
    RAW_PASTA("raw_pasta", foodItem(Foods.RAW_PASTA)),
    PUMPKIN_SLICE("pumpkin_slice", foodItem(Foods.PUMPKIN_SLICE)),
    CABBAGE_LEAF("cabbage_leaf", foodItem(Foods.CABBAGE_LEAF)),
    MINCED_BEEF("minced_beef", foodItem(Foods.MINCED_BEEF)),
    BEEF_PATTY("beef_patty", foodItem(Foods.BEEF_PATTY)),
    CHICKEN_CUTS("chicken_cuts", foodItem(Foods.CHICKEN_CUTS)),
    COOKED_CHICKEN_CUTS("cooked_chicken_cuts", foodItem(Foods.COOKED_CHICKEN_CUTS)),
    BACON("bacon", foodItem(Foods.BACON)),
    COOKED_BACON("cooked_bacon", foodItem(Foods.COOKED_BACON)),
    COD_SLICE("cod_slice", foodItem(Foods.COD_SLICE)),
    COOKED_COD_SLICE("cooked_cod_slice", foodItem(Foods.COOKED_COD_SLICE)),
    SALMON_SLICE("salmon_slice", foodItem(Foods.SALMON_SLICE)),
    COOKED_SALMON_SLICE("cooked_salmon_slice", foodItem(Foods.COOKED_SALMON_SLICE)),
    MUTTON_CHOPS("mutton_chops", foodItem(Foods.MUTTON_CHOP)),
    COOKED_MUTTON_CHOPS("cooked_mutton_chops", foodItem(Foods.COOKED_MUTTON_CHOP)),
    HAM("ham", foodItem(Foods.HAM)),
    SMOKED_HAM("smoked_ham", foodItem(Foods.SMOKED_HAM)),

    PIE_CRUST("pie_crust", foodItem(Foods.PIE_CRUST)),
    CAKE_SLICE("cake_slice", foodItem(Foods.CAKE_SLICE)),
    APPLE_PIE_SLICE("apple_pie_slice", foodItem(Foods.PIE_SLICE)),
    SWEET_BERRY_CHEESECAKE_SLICE("sweet_berry_cheesecake_slice", foodItem(Foods.PIE_SLICE)),
    CHOCOLATE_PIE_SLICE("chocolate_pie_slice", foodItem(Foods.PIE_SLICE)),
    SWEET_BERRY_COOKIE("sweet_berry_cookie", foodItem(Foods.COOKIES)),
    HONEY_COOKIE("honey_cookie", foodItem(Foods.COOKIES)),
    MELON_POPSICLE("melon_popsicle", foodItem(Foods.POPSICLE)),
    GLOW_BERRY_CUSTARD("glow_berry_custard", foodItem(Foods.GLOW_BERRY_CUSTARD, Items.GLASS_BOTTLE, 16, true)),
    FRUIT_SALAD("fruit_salad", foodItem(Foods.FRUIT_SALAD, Items.BOWL, 16, true)),

    MIXED_SALAD("mixed_salad", foodItem(Foods.MIXED_SALAD, Items.BOWL, 16, true)),
    NETHER_SALAD("nether_salad", foodItem(Foods.NETHER_SALAD, Items.BOWL, 16, false)),
    BARBECUE_STICK("barbecue_stick", foodItem(Foods.BARBECUE_STICK)),
    EGG_SANDWICH("egg_sandwich", foodItem(Foods.EGG_SANDWICH)),
    CHICKEN_SANDWICH("chicken_sandwich", foodItem(Foods.CHICKEN_SANDWICH)),
    HAMBURGER("hamburger", foodItem(Foods.HAMBURGER)),
    BACON_SANDWICH("bacon_sandwich", foodItem(Foods.BACON_SANDWICH)),
    MUTTON_WRAP("mutton_wrap", foodItem(Foods.MUTTON_WRAP)),
    DUMPLINGS("dumplings", foodItem(Foods.DUMPLINGS)),
    STUFFED_POTATO("stuffed_potato", foodItem(Foods.STUFFED_POTATO)),
    CABBAGE_ROLLS("cabbage_rolls", foodItem(Foods.CABBAGE_ROLLS)),
    SALMON_ROLL("salmon_roll", foodItem(Foods.SALMON_ROLL)),
    COD_ROLL("cod_roll", foodItem(Foods.COD_ROLL)),
    KELP_ROLL("kelp_roll", KelpRollItem::new),
    KELP_ROLL_SLICE("kelp_roll_slice", foodItem(Foods.KELP_ROLL_SLICE)),

    COOKED_RICE("cooked_rice", foodItem(Foods.COOKED_RICE, Items.BOWL, 16, true)),
    BONE_BROTH("bone_broth", drinkItem(Foods.BONE_BROTH, Items.BOWL, 16, true)),
    BEEF_STEW("beef_stew", foodItem(Foods.BEEF_STEW, Items.BOWL, 16, true)),
    CHICKEN_SOUP("chicken_soup", foodItem(Foods.CHICKEN_SOUP, Items.BOWL, 16, true)),
    VEGETABLE_SOUP("vegetable_soup", foodItem(Foods.VEGETABLE_SOUP, Items.BOWL, 16, true)),
    FISH_STEW("fish_stew", foodItem(Foods.FISH_STEW, Items.BOWL, 16, true)),
    FRIED_RICE("fried_rice", foodItem(Foods.FRIED_RICE, Items.BOWL, 16, true)),
    PUMPKIN_SOUP("pumpkin_soup", foodItem(Foods.PUMPKIN_SOUP, Items.BOWL, 16, true)),
    BAKED_COD_STEW("baked_cod_stew", foodItem(Foods.BAKED_COD_STEW, Items.BOWL, 16, true)),
    NOODLE_SOUP("noodle_soup", foodItem(Foods.NOODLE_SOUP, Items.BOWL, 16, true)),

    BACON_AND_EGGS("bacon_and_eggs", foodItem(Foods.BACON_AND_EGGS, Items.BOWL, 16, true)),
    PASTA_WITH_MEATBALLS("pasta_with_meatballs", foodItem(Foods.PASTA_WITH_MEATBALLS, Items.BOWL, 16, true)),
    PASTA_WITH_MUTTON_CHOP("pasta_with_mutton_chop", foodItem(Foods.PASTA_WITH_MUTTON_CHOP, Items.BOWL, 16, true)),
    MUSHROOM_RICE("mushroom_rice", foodItem(Foods.MUSHROOM_RICE, Items.BOWL, 16, true)),
    ROASTED_MUTTON_CHOPS("roasted_mutton_chops", foodItem(Foods.ROASTED_MUTTON_CHOPS, Items.BOWL, 16, true)),
    VEGETABLE_NOODLES("vegetable_noodles", foodItem(Foods.VEGETABLE_NOODLES, Items.BOWL, 16, true)),
    STEAK_AND_POTATOES("steak_and_potatoes", foodItem(Foods.STEAK_AND_POTATOES, Items.BOWL, 16, true)),
    RATATOUILLE("ratatouille", foodItem(Foods.RATATOUILLE, Items.BOWL, 16, true)),
    SQUID_INK_PASTA("squid_ink_pasta", foodItem(Foods.SQUID_INK_PASTA, Items.BOWL, 16, true)),
    GRILLED_SALMON("grilled_salmon", foodItem(Foods.GRILLED_SALMON, Items.BOWL, 16, true)),

    ROAST_CHICKEN("roast_chicken", foodItem(Foods.ROAST_CHICKEN, Items.BOWL, 16, true)),
    STUFFED_PUMPKIN("stuffed_pumpkin", foodItem(Foods.STUFFED_PUMPKIN, Items.BOWL, 16, true)),
    HONEY_GLAZED_HAM("honey_glazed_ham", foodItem(Foods.HONEY_GLAZED_HAM, Items.BOWL, 16, true)),
    SHEPHERDS_PIE("shepherds_pie", foodItem(Foods.SHEPHERDS_PIE, Items.BOWL, 16, true)),

    DOG_FOOD("dog_food", DogFoodItem::new),
    HORSE_FEED("horse_feed", HorseFeedItem::new);

    private static Supplier<Item> blockItem(BlocksRegistry blocksRegistry) {
        return () -> new ModBlockItem(blocksRegistry.get());
    }

    private static Supplier<Item> aliasedBlockItem(BlocksRegistry blocksRegistry) {
        return () -> new AliasedBlockItem(blocksRegistry.get(), new ModItemSettings());
    }

    private static Supplier<Item> aliasedFoodBlockItem(BlocksRegistry blocksRegistry, Foods food) {
        return () -> new AliasedBlockItem(blocksRegistry.get(), new ModItemSettings().food(food.get()));
    }

    private static Supplier<Item> oneBlockItem(BlocksRegistry blocksRegistry) {
        return () -> new ModBlockItem(blocksRegistry.get(), new ModItemSettings().maxCount(1));
    }

    private static Supplier<Item> item() {
        return () -> new Item(new ModItemSettings());
    }

    private static Supplier<Item> foodItem(Foods food) {
        return () -> new Item(new ModItemSettings().food(food.get()));
    }
    
    private static Supplier<Item> foodItem(Foods food, Item remainder, int maxCount, boolean hasFoodEffectTooltip) {
        return () -> new ConsumableItem(new ModItemSettings().food(food.get()).recipeRemainder(remainder).maxCount(maxCount), hasFoodEffectTooltip);
    }

    private static Supplier<Item> drinkItem(Foods food, Item remainder, int maxCount, boolean hasFoodEffectTooltip) {
        return () -> new DrinkableItem(new ModItemSettings().food(food.get()).recipeRemainder(remainder).maxCount(maxCount), hasFoodEffectTooltip);
    }


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
            if (value.burnTime != null && value.burnTime > 0) {
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

    public String getId() {
        return Registry.ITEM.getId(get()).toString();
    }

}