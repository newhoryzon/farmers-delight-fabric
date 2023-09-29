package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import net.fabricmc.fabric.impl.tag.convention.TagRegistration;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
public class TagsRegistry {

    public static final TagKey<Item> KNIVES = TagRegistration.ITEM_TAG_REGISTRATION.registerCommon("tools/knives");

    public static final TagKey<Block> WILD_CROPS = create("wild_crops", RegistryKeys.BLOCK);
    public static final TagKey<Block> ROPES = create("ropes", RegistryKeys.BLOCK);

    public static final TagKey<Block> HEAT_SOURCES = create("heat_sources", RegistryKeys.BLOCK);
    public static final TagKey<Block> HEAT_CONDUCTORS = create("heat_conductors", RegistryKeys.BLOCK);
    public static final TagKey<Block> TRAY_HEAT_SOURCES = create("tray_heat_sources", RegistryKeys.BLOCK);
    public static final TagKey<Block> COMPOST_ACTIVATORS = create("compost_activators", RegistryKeys.BLOCK);
    public static final TagKey<Block> UNAFFECTED_BY_RICH_SOIL = create("unaffected_by_rich_soil", RegistryKeys.BLOCK);
    public static final TagKey<Block> MUSHROOM_COLONY_GROWABLE_ON = create("mushroom_colony_growable_on", RegistryKeys.BLOCK);
    public static final TagKey<Block> MINABLE_KNIFE = create("mineable/knife", RegistryKeys.BLOCK);
    public static final TagKey<Block> DROPS_CAKE_SLICE = create("drops_cake_slice", RegistryKeys.BLOCK);

    public static final TagKey<Item> WILD_CROPS_ITEM = create("wild_crops", RegistryKeys.ITEM);
    public static final TagKey<Item> STRAW_HARVESTERS = create("straw_harvesters", RegistryKeys.ITEM);
    public static final TagKey<Item> STEW_ITEMS = create("stew_items", RegistryKeys.ITEM);
    public static final TagKey<Item> COMFORT_FOODS = create("comfort_foods", RegistryKeys.ITEM);
    public static final TagKey<Item> WOLF_PREY = create("wolf_prey", RegistryKeys.ITEM);
    public static final TagKey<Item> CABBAGE_ROLL_INGREDIENTS = create("cabbage_roll_ingredients", RegistryKeys.ITEM);
    public static final TagKey<Item> OFFHAND_EQUIPMENT = create("offhand_equipment", RegistryKeys.ITEM);

    public static final TagKey<EntityType<?>> DOG_FOOD_USERS = create("dog_food_users", RegistryKeys.ENTITY_TYPE);
    public static final TagKey<EntityType<?>> HORSE_FEED_USERS = create("horse_feed_users", RegistryKeys.ENTITY_TYPE);

    private static <E> TagKey<E> create(String pathName, RegistryKey<? extends Registry<E>> registry) {
        return TagKey.of(registry, new Identifier(FarmersDelightMod.MOD_ID, pathName));
    }

    private TagsRegistry() throws InstantiationException {
        throw new InstantiationException("Constant class cannot be instantiate");
    }

}
