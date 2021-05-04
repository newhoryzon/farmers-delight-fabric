package com.nhoryzon.mc.farmersdelight.tag;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.util.function.Function;

@SuppressWarnings("unused")
public class Tags {

    public static final Tag<Block> WILD_CROPS = create("wild_crops", TagRegistry::block);
    public static final Tag<Block> HEAT_SOURCES = create("heat_sources", TagRegistry::block);
    public static final Tag<Block> TRAY_HEAT_SOURCES = create("tray_heat_sources", TagRegistry::block);
    public static final Tag<Block> COMPOST_ACTIVATORS = create("compost_activators", TagRegistry::block);
    public static final Tag<Block> UNAFFECTED_BY_RICH_SOIL = create("unaffected_by_rich_soil", TagRegistry::block);
    public static final Tag<Item> WILD_CROPS_ITEM = create("wild_crops", TagRegistry::item);
    public static final Tag<Item> STRAW_HARVESTERS = create("straw_harvesters", TagRegistry::item);
    public static final Tag<Item> COMFORT_FOODS = create("comfort_foods", TagRegistry::item);
    public static final Tag<Item> WOLF_PREY = create("wolf_prey", TagRegistry::item);
    public static final Tag<Item> CABBAGE_ROLL_INGREDIENTS = create("cabbage_roll_ingredients", TagRegistry::item);
    public static final Tag<Item> KNIVES = create("tools/knives", TagRegistry::item);
    public static final Tag<EntityType<?>> DOG_FOOD_USERS = create("dog_food_users", TagRegistry::entityType);
    public static final Tag<EntityType<?>> HORSE_FEED_USERS = create("horse_feed_users", TagRegistry::entityType);

    private static <E> Tag<E> create(String pathName, Function<Identifier, Tag<E>> tagCreateSupplier) {
        return tagCreateSupplier.apply(new Identifier(FarmersDelightMod.MOD_ID, pathName));
    }

}
