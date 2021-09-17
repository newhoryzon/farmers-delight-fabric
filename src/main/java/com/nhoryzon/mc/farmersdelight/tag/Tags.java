package com.nhoryzon.mc.farmersdelight.tag;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.util.function.Function;

@SuppressWarnings("unused")
public class Tags {

    private Tags() {
    }

    public static final Tag.Identified<Block> WILD_CROPS = create("wild_crops", TagFactory.BLOCK);
    public static final Tag.Identified<Block> HEAT_SOURCES = create("heat_sources", TagFactory.BLOCK);
    public static final Tag.Identified<Block> TRAY_HEAT_SOURCES = create("tray_heat_sources", TagFactory.BLOCK);
    public static final Tag.Identified<Block> COMPOST_ACTIVATORS = create("compost_activators", TagFactory.BLOCK);
    public static final Tag.Identified<Block> UNAFFECTED_BY_RICH_SOIL = create("unaffected_by_rich_soil", TagFactory.BLOCK);
    public static final Tag.Identified<Block> KNIVES_CUTTABLE = create("knives_cuttable", TagFactory.BLOCK);
    public static final Tag.Identified<Item> WILD_CROPS_ITEM = create("wild_crops", TagFactory.ITEM);
    public static final Tag.Identified<Item> STRAW_HARVESTERS = create("straw_harvesters", TagFactory.ITEM);
    public static final Tag.Identified<Item> COMFORT_FOODS = create("comfort_foods", TagFactory.ITEM);
    public static final Tag.Identified<Item> WOLF_PREY = create("wolf_prey", TagFactory.ITEM);
    public static final Tag.Identified<Item> CABBAGE_ROLL_INGREDIENTS = create("cabbage_roll_ingredients", TagFactory.ITEM);
    public static final Tag.Identified<Item> KNIVES = create("tools/knives", TagFactory.ITEM);
    public static final Tag.Identified<EntityType<?>> DOG_FOOD_USERS = create("dog_food_users", TagFactory.ENTITY_TYPE);
    public static final Tag.Identified<EntityType<?>> HORSE_FEED_USERS = create("horse_feed_users", TagFactory.ENTITY_TYPE);

    private static <E> Tag.Identified<E> create(String pathName, TagFactory<E> tagFactory) {
        return tagFactory.create(new Identifier(FarmersDelightMod.MOD_ID, pathName));
    }
}