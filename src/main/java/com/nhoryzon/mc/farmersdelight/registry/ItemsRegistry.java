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
import com.nhoryzon.mc.farmersdelight.item.MushroomColonyBlockItem;
import com.nhoryzon.mc.farmersdelight.item.RopeItem;
import com.nhoryzon.mc.farmersdelight.item.RottenTomatoItem;
import com.nhoryzon.mc.farmersdelight.item.SkilletItem;
import com.nhoryzon.mc.farmersdelight.item.enumeration.Foods;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SignItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

import static com.nhoryzon.mc.farmersdelight.item.ModItemSettings.base;
import static com.nhoryzon.mc.farmersdelight.item.ModItemSettings.food;
import static com.nhoryzon.mc.farmersdelight.item.ModItemSettings.noStack;

public enum ItemsRegistry {

    /* Block Items */

    STOVE("stove", () -> new ModBlockItem(BlocksRegistry.STOVE.get())),
    COOKING_POT("cooking_pot", () -> new ModBlockItem(BlocksRegistry.COOKING_POT.get(), noStack())),
    SKILLET("skillet", SkilletItem::new),
    BASKET("basket", () -> new ModBlockItem(BlocksRegistry.BASKET.get())),
    CUTTING_BOARD("cutting_board", () -> new ModBlockItem(BlocksRegistry.CUTTING_BOARD.get())),

    SANDY_SHRUB("sandy_shrub", () -> new ModBlockItem(BlocksRegistry.SANDY_SHRUB.get())),
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
    OAK_CABINET("oak_cabinet", () -> new ModBlockItem(BlocksRegistry.OAK_CABINET.get()), 300),
    BIRCH_CABINET("birch_cabinet", () -> new ModBlockItem(BlocksRegistry.BIRCH_CABINET.get()), 300),
    SPRUCE_CABINET("spruce_cabinet", () -> new ModBlockItem(BlocksRegistry.SPRUCE_CABINET.get()), 300),
    JUNGLE_CABINET("jungle_cabinet", () -> new ModBlockItem(BlocksRegistry.JUNGLE_CABINET.get()), 300),
    ACACIA_CABINET("acacia_cabinet", () -> new ModBlockItem(BlocksRegistry.ACACIA_CABINET.get()), 300),
    DARK_OAK_CABINET("dark_oak_cabinet", () -> new ModBlockItem(BlocksRegistry.DARK_OAK_CABINET.get()), 300),
    MANGROVE_CABINET("mangrove_cabinet", () -> new ModBlockItem(BlocksRegistry.MANGROVE_CABINET.get()), 300),
    CRIMSON_CABINET("crimson_cabinet", () -> new ModBlockItem(BlocksRegistry.CRIMSON_CABINET.get())),
    WARPED_CABINET("warped_cabinet", () -> new ModBlockItem(BlocksRegistry.WARPED_CABINET.get())),
    CANVAS_RUG("canvas_rug", () -> new ModBlockItem(BlocksRegistry.CANVAS_RUG.get()), 200),
    TATAMI("tatami", () -> new ModBlockItem(BlocksRegistry.TATAMI.get()), 400),
    FULL_TATAMI_MAT("full_tatami_mat", () -> new ModBlockItem(BlocksRegistry.FULL_TATAMI_MAT.get()), 200),
    HALF_TATAMI_MAT("half_tatami_mat", () -> new ModBlockItem(BlocksRegistry.HALF_TATAMI_MAT.get()), 100),

    CANVAS_SIGN("canvas_sign", () -> new SignItem(base(),
            BlocksRegistry.CANVAS_SIGN.get(), BlocksRegistry.CANVAS_WALL_SIGN.get())),
    WHITE_CANVAS_SIGN("white_canvas_sign", () -> new SignItem(base(),
            BlocksRegistry.WHITE_CANVAS_SIGN.get(), BlocksRegistry.WHITE_CANVAS_WALL_SIGN.get())),
    ORANGE_CANVAS_SIGN("orange_canvas_sign", () -> new SignItem(base(),
            BlocksRegistry.ORANGE_CANVAS_SIGN.get(), BlocksRegistry.ORANGE_CANVAS_WALL_SIGN.get())),
    MAGENTA_CANVAS_SIGN("magenta_canvas_sign", () -> new SignItem(base(),
            BlocksRegistry.MAGENTA_CANVAS_SIGN.get(), BlocksRegistry.MAGENTA_CANVAS_WALL_SIGN.get())),
    LIGHT_BLUE_CANVAS_SIGN("light_blue_canvas_sign", () -> new SignItem(base(),
            BlocksRegistry.LIGHT_BLUE_CANVAS_SIGN.get(), BlocksRegistry.LIGHT_BLUE_CANVAS_WALL_SIGN.get())),
    YELLOW_CANVAS_SIGN("yellow_canvas_sign", () -> new SignItem(base(),
            BlocksRegistry.YELLOW_CANVAS_SIGN.get(), BlocksRegistry.YELLOW_CANVAS_WALL_SIGN.get())),
    LIME_CANVAS_SIGN("lime_canvas_sign", () -> new SignItem(base(),
            BlocksRegistry.LIME_CANVAS_SIGN.get(), BlocksRegistry.LIME_CANVAS_WALL_SIGN.get())),
    PINK_CANVAS_SIGN("pink_canvas_sign", () -> new SignItem(base(),
            BlocksRegistry.PINK_CANVAS_SIGN.get(), BlocksRegistry.PINK_CANVAS_WALL_SIGN.get())),
    GRAY_CANVAS_SIGN("gray_canvas_sign", () -> new SignItem(base(),
            BlocksRegistry.GRAY_CANVAS_SIGN.get(), BlocksRegistry.GRAY_CANVAS_WALL_SIGN.get())),
    LIGHT_GRAY_CANVAS_SIGN("light_gray_canvas_sign", () -> new SignItem(base(),
            BlocksRegistry.LIGHT_GRAY_CANVAS_SIGN.get(), BlocksRegistry.LIGHT_GRAY_CANVAS_WALL_SIGN.get())),
    CYAN_CANVAS_SIGN("cyan_canvas_sign", () -> new SignItem(base(),
            BlocksRegistry.CYAN_CANVAS_SIGN.get(), BlocksRegistry.CYAN_CANVAS_WALL_SIGN.get())),
    PURPLE_CANVAS_SIGN("purple_canvas_sign", () -> new SignItem(base(),
            BlocksRegistry.PURPLE_CANVAS_SIGN.get(), BlocksRegistry.PURPLE_CANVAS_WALL_SIGN.get())),
    BLUE_CANVAS_SIGN("blue_canvas_sign", () -> new SignItem(base(),
            BlocksRegistry.BLUE_CANVAS_SIGN.get(), BlocksRegistry.BLUE_CANVAS_WALL_SIGN.get())),
    BROWN_CANVAS_SIGN("brown_canvas_sign", () -> new SignItem(base(),
            BlocksRegistry.BROWN_CANVAS_SIGN.get(), BlocksRegistry.BROWN_CANVAS_WALL_SIGN.get())),
    GREEN_CANVAS_SIGN("green_canvas_sign", () -> new SignItem(base(),
            BlocksRegistry.GREEN_CANVAS_SIGN.get(), BlocksRegistry.GREEN_CANVAS_WALL_SIGN.get())),
    RED_CANVAS_SIGN("red_canvas_sign", () -> new SignItem(base(),
            BlocksRegistry.RED_CANVAS_SIGN.get(), BlocksRegistry.RED_CANVAS_WALL_SIGN.get())),
    BLACK_CANVAS_SIGN("black_canvas_sign", () -> new SignItem(base(),
            BlocksRegistry.BLACK_CANVAS_SIGN.get(), BlocksRegistry.BLACK_CANVAS_WALL_SIGN.get())),

    ORGANIC_COMPOST("organic_compost", () -> new ModBlockItem(BlocksRegistry.ORGANIC_COMPOST.get())),
    RICH_SOIL("rich_soil", () -> new ModBlockItem(BlocksRegistry.RICH_SOIL.get())),
    RICH_SOIL_FARMLAND("rich_soil_farmland", () -> new ModBlockItem(BlocksRegistry.RICH_SOIL_FARMLAND.get())),

    APPLE_PIE("apple_pie", () -> new ModBlockItem(BlocksRegistry.APPLE_PIE.get())),
    SWEET_BERRY_CHEESECAKE("sweet_berry_cheesecake", () -> new ModBlockItem(BlocksRegistry.SWEET_BERRY_CHEESECAKE.get())),
    CHOCOLATE_PIE("chocolate_pie", () -> new ModBlockItem(BlocksRegistry.CHOCOLATE_PIE.get())),

    ROAST_CHICKEN_BLOCK("roast_chicken_block", () -> new ModBlockItem(BlocksRegistry.ROAST_CHICKEN_BLOCK.get(), noStack())),
    STUFFED_PUMPKIN_BLOCK("stuffed_pumpkin_block", () -> new ModBlockItem(BlocksRegistry.STUFFED_PUMPKIN_BLOCK.get(), noStack())),
    HONEY_GLAZED_HAM_BLOCK("honey_glazed_ham_block", () -> new ModBlockItem(BlocksRegistry.HONEY_GLAZED_HAM_BLOCK.get(), noStack())),
    SHEPHERDS_PIE_BLOCK("shepherds_pie_block", () -> new ModBlockItem(BlocksRegistry.SHEPHERDS_PIE_BLOCK.get(), noStack())),
    RICE_ROLL_MEDLEY_BLOCK("rice_roll_medley_block", () -> new ModBlockItem(BlocksRegistry.RICE_ROLL_MEDLEY_BLOCK.get(), noStack())),

    /* Items */

    FLINT_KNIFE("flint_knife", () -> new KnifeItem(com.nhoryzon.mc.farmersdelight.item.enumeration.ToolMaterials.FLINT)),
    IRON_KNIFE("iron_knife", () -> new KnifeItem(ToolMaterials.IRON)),
    GOLDEN_KNIFE("golden_knife", () -> new KnifeItem(ToolMaterials.GOLD)),
    DIAMOND_KNIFE("diamond_knife", () -> new KnifeItem(ToolMaterials.DIAMOND)),
    NETHERITE_KNIFE("netherite_knife", () -> new KnifeItem(ToolMaterials.NETHERITE)),

    STRAW("straw", () -> new Item(base()), 100),
    CANVAS("canvas", () -> new Item(base()), 400),
    TREE_BARK("tree_bark", () -> new Item(base()), 200),

    CABBAGE("cabbage", () -> new Item(food(Foods.CABBAGE))),
    CABBAGE_SEEDS("cabbage_seeds", () -> new AliasedBlockItem(BlocksRegistry.CABBAGE_CROP.get(), base())),
    TOMATO("tomato", () -> new Item(food(Foods.TOMATO))),
    TOMATO_SEEDS("tomato_seeds", () -> new AliasedBlockItem(BlocksRegistry.BUDDING_TOMATO_CROP.get(), base())),
    ONION("onion", () -> new AliasedBlockItem(BlocksRegistry.ONION_CROP.get(), food(Foods.ONION))),
    RICE_PANICLE("rice_panicle", () -> new Item(base())),
    RICE("rice", () -> new AliasedBlockItem(BlocksRegistry.RICE_CROP.get(), base())),
    ROTTEN_TOMATO("rotten_tomato", RottenTomatoItem::new),

    FRIED_EGG("fried_egg", () -> new Item(food(Foods.FRIED_EGG))),
    MILK_BOTTLE("milk_bottle", MilkBottleItem::new),
    HOT_COCOA("hot_cocoa", HotCocoaItem::new),
    APPLE_CIDER("apple_cider", () -> new DrinkableItem(food(Foods.APPLE_CIDER, Items.GLASS_BOTTLE, 16), true)),
    MELON_JUICE("melon_juice", MelonJuiceItem::new),
    TOMATO_SAUCE("tomato_sauce", () -> new ConsumableItem(food(Foods.TOMATO_SAUCE, Items.BOWL, 64), false)),
    WHEAT_DOUGH("wheat_dough", () -> new Item(food(Foods.WHEAT_DOUGH))),
    RAW_PASTA("raw_pasta", () -> new Item(food(Foods.RAW_PASTA))),
    PUMPKIN_SLICE("pumpkin_slice", () -> new Item(food(Foods.PUMPKIN_SLICE))),
    CABBAGE_LEAF("cabbage_leaf", () -> new Item(food(Foods.CABBAGE_LEAF))),
    MINCED_BEEF("minced_beef", () -> new Item(food(Foods.MINCED_BEEF))),
    BEEF_PATTY("beef_patty", () -> new Item(food(Foods.BEEF_PATTY))),
    CHICKEN_CUTS("chicken_cuts", () -> new Item(food(Foods.CHICKEN_CUTS))),
    COOKED_CHICKEN_CUTS("cooked_chicken_cuts", () -> new Item(food(Foods.COOKED_CHICKEN_CUTS))),
    BACON("bacon", () -> new Item(food(Foods.BACON))),
    COOKED_BACON("cooked_bacon", () -> new Item(food(Foods.COOKED_BACON))),
    COD_SLICE("cod_slice", () -> new Item(food(Foods.COD_SLICE))),
    COOKED_COD_SLICE("cooked_cod_slice", () -> new Item(food(Foods.COOKED_COD_SLICE))),
    SALMON_SLICE("salmon_slice", () -> new Item(food(Foods.SALMON_SLICE))),
    COOKED_SALMON_SLICE("cooked_salmon_slice", () -> new Item(food(Foods.COOKED_SALMON_SLICE))),
    MUTTON_CHOPS("mutton_chops", () -> new Item(food(Foods.MUTTON_CHOP))),
    COOKED_MUTTON_CHOPS("cooked_mutton_chops", () -> new Item(food(Foods.COOKED_MUTTON_CHOP))),
    HAM("ham", () -> new Item(food(Foods.HAM))),
    SMOKED_HAM("smoked_ham", () -> new Item(food(Foods.SMOKED_HAM))),

    PIE_CRUST("pie_crust", () -> new Item(food(Foods.PIE_CRUST))),
    CAKE_SLICE("cake_slice", () -> new Item(food(Foods.CAKE_SLICE))),
    APPLE_PIE_SLICE("apple_pie_slice", () -> new Item(food(Foods.PIE_SLICE))),
    SWEET_BERRY_CHEESECAKE_SLICE("sweet_berry_cheesecake_slice", () -> new Item(food(Foods.PIE_SLICE))),
    CHOCOLATE_PIE_SLICE("chocolate_pie_slice", () -> new Item(food(Foods.PIE_SLICE))),
    SWEET_BERRY_COOKIE("sweet_berry_cookie", () -> new Item(food(Foods.COOKIES))),
    HONEY_COOKIE("honey_cookie", () -> new Item(food(Foods.COOKIES))),
    MELON_POPSICLE("melon_popsicle", () -> new Item(food(Foods.POPSICLE))),
    GLOW_BERRY_CUSTARD("glow_berry_custard", () -> new ConsumableItem(food(Foods.GLOW_BERRY_CUSTARD, Items.GLASS_BOTTLE, 16), true)),
    FRUIT_SALAD("fruit_salad", () -> new ConsumableItem(food(Foods.FRUIT_SALAD, Items.BOWL, 16), true)),

    MIXED_SALAD("mixed_salad", () -> new ConsumableItem(food(Foods.MIXED_SALAD, Items.BOWL, 16), true)),
    NETHER_SALAD("nether_salad", () -> new ConsumableItem(food(Foods.NETHER_SALAD, Items.BOWL, 16), false)),
    BARBECUE_STICK("barbecue_stick", () -> new Item(food(Foods.BARBECUE_STICK))),
    EGG_SANDWICH("egg_sandwich", () -> new Item(food(Foods.EGG_SANDWICH))),
    CHICKEN_SANDWICH("chicken_sandwich", () -> new Item(food(Foods.CHICKEN_SANDWICH))),
    HAMBURGER("hamburger", () -> new Item(food(Foods.HAMBURGER))),
    BACON_SANDWICH("bacon_sandwich", () -> new Item(food(Foods.BACON_SANDWICH))),
    MUTTON_WRAP("mutton_wrap", () -> new Item(food(Foods.MUTTON_WRAP))),
    DUMPLINGS("dumplings", () -> new Item(food(Foods.DUMPLINGS))),
    STUFFED_POTATO("stuffed_potato", () -> new Item(food(Foods.STUFFED_POTATO))),
    CABBAGE_ROLLS("cabbage_rolls", () -> new Item(food(Foods.CABBAGE_ROLLS))),
    SALMON_ROLL("salmon_roll", () -> new Item(food(Foods.SALMON_ROLL))),
    COD_ROLL("cod_roll", () -> new Item(food(Foods.COD_ROLL))),
    KELP_ROLL("kelp_roll", KelpRollItem::new),
    KELP_ROLL_SLICE("kelp_roll_slice", () -> new Item(food(Foods.KELP_ROLL_SLICE))),

    COOKED_RICE("cooked_rice", () -> new ConsumableItem(food(Foods.COOKED_RICE, Items.BOWL, 16), true)),
    BONE_BROTH("bone_broth", () -> new DrinkableItem(food(Foods.BONE_BROTH, Items.BOWL, 16), true)),
    BEEF_STEW("beef_stew", () -> new ConsumableItem(food(Foods.BEEF_STEW, Items.BOWL, 16), true)),
    CHICKEN_SOUP("chicken_soup", () -> new ConsumableItem(food(Foods.CHICKEN_SOUP, Items.BOWL, 16), true)),
    VEGETABLE_SOUP("vegetable_soup", () -> new ConsumableItem(food(Foods.VEGETABLE_SOUP, Items.BOWL, 16), true)),
    FISH_STEW("fish_stew", () -> new ConsumableItem(food(Foods.FISH_STEW, Items.BOWL, 16), true)),
    FRIED_RICE("fried_rice", () -> new ConsumableItem(food(Foods.FRIED_RICE, Items.BOWL, 16), true)),
    PUMPKIN_SOUP("pumpkin_soup", () -> new ConsumableItem(food(Foods.PUMPKIN_SOUP, Items.BOWL, 16), true)),
    BAKED_COD_STEW("baked_cod_stew", () -> new ConsumableItem(food(Foods.BAKED_COD_STEW, Items.BOWL, 16), true)),
    NOODLE_SOUP("noodle_soup", () -> new ConsumableItem(food(Foods.NOODLE_SOUP, Items.BOWL, 16), true)),

    BACON_AND_EGGS("bacon_and_eggs", () -> new ConsumableItem(food(Foods.BACON_AND_EGGS, Items.BOWL, 16), true)),
    PASTA_WITH_MEATBALLS("pasta_with_meatballs", () -> new ConsumableItem(food(Foods.PASTA_WITH_MEATBALLS, Items.BOWL, 16), true)),
    PASTA_WITH_MUTTON_CHOP("pasta_with_mutton_chop", () -> new ConsumableItem(food(Foods.PASTA_WITH_MUTTON_CHOP, Items.BOWL, 16), true)),
    MUSHROOM_RICE("mushroom_rice", () -> new ConsumableItem(food(Foods.MUSHROOM_RICE, Items.BOWL, 16), true)),
    ROASTED_MUTTON_CHOPS("roasted_mutton_chops", () -> new ConsumableItem(food(Foods.ROASTED_MUTTON_CHOPS, Items.BOWL, 16), true)),
    VEGETABLE_NOODLES("vegetable_noodles", () -> new ConsumableItem(food(Foods.VEGETABLE_NOODLES, Items.BOWL, 16), true)),
    STEAK_AND_POTATOES("steak_and_potatoes", () -> new ConsumableItem(food(Foods.STEAK_AND_POTATOES, Items.BOWL, 16), true)),
    RATATOUILLE("ratatouille", () -> new ConsumableItem(food(Foods.RATATOUILLE, Items.BOWL, 16), true)),
    SQUID_INK_PASTA("squid_ink_pasta", () -> new ConsumableItem(food(Foods.SQUID_INK_PASTA, Items.BOWL, 16), true)),
    GRILLED_SALMON("grilled_salmon", () -> new ConsumableItem(food(Foods.GRILLED_SALMON, Items.BOWL, 16), true)),

    ROAST_CHICKEN("roast_chicken", () -> new ConsumableItem(food(Foods.ROAST_CHICKEN, Items.BOWL, 16), true)),
    STUFFED_PUMPKIN("stuffed_pumpkin", () -> new ConsumableItem(food(Foods.STUFFED_PUMPKIN, Items.BOWL, 16), true)),
    HONEY_GLAZED_HAM("honey_glazed_ham", () -> new ConsumableItem(food(Foods.HONEY_GLAZED_HAM, Items.BOWL, 16), true)),
    SHEPHERDS_PIE("shepherds_pie", () -> new ConsumableItem(food(Foods.SHEPHERDS_PIE, Items.BOWL, 16), true)),

    DOG_FOOD("dog_food", DogFoodItem::new),
    HORSE_FEED("horse_feed", HorseFeedItem::new);

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