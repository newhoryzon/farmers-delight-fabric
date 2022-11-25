package com.nhoryzon.mc.farmersdelight.integration.modmenu;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

@Environment(value= EnvType.CLIENT)
public enum Category {

    SETTINGS("Game settings", false,
            Entry.bool("enableVanillaCropCrates", () -> FarmersDelightMod.CONFIG.isEnableVanillaCropCrates(),
                    newValue -> FarmersDelightMod.CONFIG.setEnableVanillaCropCrates(newValue), true,
                    "Farmer's Delight adds crates (3x3) for vanilla crops, similar to",
                    "Quark and Thermal Cultivation. Should they be craftable?"),
            Entry.bool("farmersBuyFDCrops", () -> FarmersDelightMod.CONFIG.isFarmersBuyFDCrops(),
                    newValue -> FarmersDelightMod.CONFIG.setFarmersBuyFDCrops(newValue), true,
                    "Should Novice and Apprentice Farmers buy this mod's crops? (May",
                    "reduce chances of other trades appearing)"),
            Entry.bool("wanderingTraderSellsFDItems", () -> FarmersDelightMod.CONFIG.isWanderingTraderSellsFDItems(),
                    newValue -> FarmersDelightMod.CONFIG.setWanderingTraderSellsFDItems(newValue), true,
                    "Should the Wandering Trader sell some of this mod's items? (Currently",
                    "includes crop seeds and onions)"),
            Entry.doubl("richSoilBoostChance", () -> FarmersDelightMod.CONFIG.getRichSoilBoostChance(),
                    newValue -> FarmersDelightMod.CONFIG.setRichSoilBoostChance(newValue), 0.2, 0.0, 1.0,
                    "How often (in percentage) should Rich Soil succeed in boosting a plant's",
                    "growth at each random tick? Set it to 0.0 to disable this."),
            Entry.doubl("cuttingBoardFortuneBonus", () -> FarmersDelightMod.CONFIG.getCuttingBoardFortuneBonus(),
                    newValue -> FarmersDelightMod.CONFIG.setCuttingBoardFortuneBonus(newValue), 0.1, 0.0, 1.0,
                    "How much of a bonus (in percentage) should each level of Fortune grant to",
                    "Cutting Board chances? Set it to 0.0 to disable this."),
            Entry.bool("enableRopeReeling", () -> FarmersDelightMod.CONFIG.isEnableRopeReeling(),
                    newValue -> FarmersDelightMod.CONFIG.setEnableRopeReeling(newValue), true,
                    "Should players be able to reel back rope, bottom to top, when sneak-using with",
                    "an empty hand on them?"),
            Entry.list("canvasSignDarkBackgroundList", () -> FarmersDelightMod.CONFIG.getCanvasSignDarkBackgroundList(),
                    newValue -> FarmersDelightMod.CONFIG.setCanvasSignDarkBackgroundList(newValue), List.of("gray", "purple", "blue", "brown", "green", "red", "black"),
                    "A list of dye colors that, when used as the background of a Canvas Sign,",
                    "should default to white text when placed.",
                    "Dyes: [\"white\", \"orange\", \"magenta\", \"light_blue\", \"yellow\", \"lime\", \"pink\", \"gray\",",
                    "        \"light_gray\", \"cyan\", \"purple\", \"blue\", \"brown\", \"green\", \"red\", \"black\"]")),

    FARMING("Farming", false,
            Entry.str("defaultTomatoVineRope", () -> FarmersDelightMod.CONFIG.getDefaultTomatoVineRope(),
                    newValue -> FarmersDelightMod.CONFIG.setDefaultTomatoVineRope(newValue), "farmersdelight:rope",
                    "Which rope should Tomato Vines leave behind when mined by hand?"),
            Entry.bool("enableTomatoVineClimbingTaggedRopes", () -> FarmersDelightMod.CONFIG.isEnableTomatoVineClimbingTaggedRopes(),
                    newValue -> FarmersDelightMod.CONFIG.setEnableTomatoVineClimbingTaggedRopes(newValue), true,
                    "Should tomato vines be able to climb any rope tagged as farmersdelight:ropes?",
                    "Beware: this will convert these blocks into the block specified in defaultTomatoVineRope.")),

    OVERRIDES("Vanilla item overrides", false,
            Entry.bool("vanillaSoupExtraEffects", () -> FarmersDelightMod.CONFIG.isVanillaSoupExtraEffects(),
                    newValue -> FarmersDelightMod.CONFIG.setVanillaSoupExtraEffects(newValue), true,
                    "Should soups and stews from vanilla Minecraft grant additional effects,",
                    "like meals from this mod?"),
            Entry.bool("rabbitStewJumpBoost", () -> FarmersDelightMod.CONFIG.isRabbitStewJumpBoost(),
                    newValue -> FarmersDelightMod.CONFIG.setRabbitStewJumpBoost(newValue), true,
                    "Should Rabbit Stew grant users the jumping prowess of a rabbit when eaten?"),
            Entry.bool("dispenserUsesToolsOnCuttingBoard", () -> FarmersDelightMod.CONFIG.isDispenserToolsCuttingBoard(),
                    newValue -> FarmersDelightMod.CONFIG.setDispenserToolsCuttingBoard(newValue), true,
                    "Should the Dispenser be able to operate a Cutting Board in front of it?")),

    STACK_SIZE("Stack size overrides", false,
            Entry.bool("enableStackableSoupItems", () -> FarmersDelightMod.CONFIG.isEnableStackableSoupSize(),
                    newValue -> FarmersDelightMod.CONFIG.setEnableStackableSoupSize(newValue), true,
                    "Should SoupItems in the following list become stackable to 16, much like ",
                    "Farmer's Delight's meals?"),
            Entry.list("soupItemList", () -> FarmersDelightMod.CONFIG.getSoupItemList(),
                    newValue -> FarmersDelightMod.CONFIG.setSoupItemList(newValue), List.of("minecraft:mushroom_stew", "minecraft:beetroot_soup", "minecraft:rabbit_stew"),
                    "List of SoupItems. Default: vanilla soups and stews."),
            Entry.bool("overrideAllSoupItems", () -> FarmersDelightMod.CONFIG.isOverrideAllSoupItems(),
                    newValue -> FarmersDelightMod.CONFIG.setOverrideAllSoupItems(newValue), false,
                    "Toggle this setting to instead make ALL SoupItems stackable, except the ones on ",
                    "the list (deny-list). This affects items from other mods, so be careful!")),

    WILD_CABBAGES("Wild Cabbage generation", true,
            Entry.bool("generateWildCabbages", () -> FarmersDelightMod.CONFIG.isGenerateWildCabbages(),
                    newValue -> FarmersDelightMod.CONFIG.setGenerateWildCabbages(newValue), true,
                    "Generate wild cabbages on beaches"),
            Entry.inte("chanceWildCabbages", () -> FarmersDelightMod.CONFIG.getChanceWildCabbages(),
                    newValue -> FarmersDelightMod.CONFIG.setChanceWildCabbages(newValue), 30, 0, Integer.MAX_VALUE,
                    "Chance of generating clusters. Smaller value = more frequent.")),
    WILD_BEETROOTS("Sea Beet generation", true,
            Entry.bool("generateWildBeetroots", () -> FarmersDelightMod.CONFIG.isGenerateWildBeetroots(),
                    newValue -> FarmersDelightMod.CONFIG.setGenerateWildBeetroots(newValue), true,
                    "Generate sea beets on beaches"),
            Entry.inte("chanceWildBeetroots", () -> FarmersDelightMod.CONFIG.getChanceWildBeetroots(),
                    newValue -> FarmersDelightMod.CONFIG.setChanceWildBeetroots(newValue), 30, 0, Integer.MAX_VALUE,
                    "Chance of generating clusters. Smaller value = more frequent.")),
    WILD_POTATOES("Wild Potato generation", true,
            Entry.bool("generateWildPotatoes", () -> FarmersDelightMod.CONFIG.isGenerateWildPotatoes(),
                    newValue -> FarmersDelightMod.CONFIG.setGenerateWildPotatoes(newValue), true,
                    "Generate wild potatoes on cold biomes (temperature between 0.0 and 0.3)"),
            Entry.inte("chanceWildPotatoes", () -> FarmersDelightMod.CONFIG.getChanceWildPotatoes(),
                    newValue -> FarmersDelightMod.CONFIG.setChanceWildPotatoes(newValue), 100, 0, Integer.MAX_VALUE,
                    "Chance of generating clusters. Smaller value = more frequent.")),
    WILD_CARROTS("Wild Carrot generation", true,
            Entry.bool("generateWildCarrots", () -> FarmersDelightMod.CONFIG.isGenerateWildCarrots(),
                    newValue -> FarmersDelightMod.CONFIG.setGenerateWildCarrots(newValue), true,
                    "Generate wild carrots on temperate biomes (temperature between 0.4 and 0.9)"),
            Entry.inte("chanceWildCarrots", () -> FarmersDelightMod.CONFIG.getChanceWildCarrots(),
                    newValue -> FarmersDelightMod.CONFIG.setChanceWildCarrots(newValue), 120, 0, Integer.MAX_VALUE,
                    "Chance of generating clusters. Smaller value = more frequent.")),
    WILD_ONIONS("Wild Onion generation", true,
            Entry.bool("generateWildOnions", () -> FarmersDelightMod.CONFIG.isGenerateWildOnions(),
                    newValue -> FarmersDelightMod.CONFIG.setGenerateWildOnions(newValue), true,
                    "Generate wild onions on temperate biomes (temperature between 0.4 and 0.9)"),
            Entry.inte("chanceWildOnions", () -> FarmersDelightMod.CONFIG.getChanceWildOnions(),
                    newValue -> FarmersDelightMod.CONFIG.setChanceWildOnions(newValue), 120, 0, Integer.MAX_VALUE,
                    "Chance of generating clusters. Smaller value = more frequent.")),
    WILD_TOMATOES("Tomato Vines generation", true,
            Entry.bool("generateWildTomatoes", () -> FarmersDelightMod.CONFIG.isGenerateWildTomatoes(),
                    newValue -> FarmersDelightMod.CONFIG.setGenerateWildTomatoes(newValue), true,
                    "Generate tomato vines on arid biomes (temperature 1.0 or higher)"),
            Entry.inte("chanceWildTomatoes", () -> FarmersDelightMod.CONFIG.getChanceWildTomatoes(),
                    newValue -> FarmersDelightMod.CONFIG.setChanceWildTomatoes(newValue), 100, 0, Integer.MAX_VALUE,
                    "Chance of generating clusters. Smaller value = more frequent.")),
    WILD_RICE("Wild Rice generation", true,
            Entry.bool("generateWildRice", () -> FarmersDelightMod.CONFIG.isGenerateWildRice(),
                    newValue -> FarmersDelightMod.CONFIG.setGenerateWildRice(newValue), true,
                    "Generate wild rice on swamps and jungles"),
            Entry.inte("chanceWildRice", () -> FarmersDelightMod.CONFIG.getChanceWildRice(),
                    newValue -> FarmersDelightMod.CONFIG.setChanceWildRice(newValue), 20, 0, Integer.MAX_VALUE,
                    "Chance of generating clusters. Smaller value = more frequent.")),

    WORLD("World generation", false, new Category[]{ WILD_CABBAGES, WILD_BEETROOTS, WILD_POTATOES, WILD_CARROTS, WILD_ONIONS, WILD_TOMATOES, WILD_RICE },
            Entry.bool("generateFDChestLoot", () -> FarmersDelightMod.CONFIG.isGenerateFDChestLoot(),
                    newValue -> FarmersDelightMod.CONFIG.setGenerateFDChestLoot(newValue), true,
                    "Should this mod add some of its items (ropes, seeds, knives, meals etc.)",
                    "as extra chest loot across Minecraft?"),
            Entry.bool("generateVillageCompostHeaps", () -> FarmersDelightMod.CONFIG.isGenerateVillageCompostHeaps(),
                    newValue -> FarmersDelightMod.CONFIG.setGenerateVillageCompostHeaps(newValue), true,
                    "Generate Compost Heaps across all village biomes")),

    CLIENT("Client settings", false,
            Entry.bool("nourishedHungerOverlay", () -> FarmersDelightMod.CONFIG.isNourishedHungerOverlay(),
                    newValue -> FarmersDelightMod.CONFIG.setNourishedHungerOverlay(newValue), true,
                    "Should the hunger bar have a gilded overlay when the player has the Nourishment effect?"),
            Entry.bool("comfortHealthOverlay", () -> FarmersDelightMod.CONFIG.isComfortHealthOverlay(),
                    newValue -> FarmersDelightMod.CONFIG.setComfortHealthOverlay(newValue), true,
                    "Should the health bar have a silver sheen when the player has the Comfort effect?"),
            Entry.bool("foodEffectTooltip", () -> FarmersDelightMod.CONFIG.isFoodEffectTooltip(),
                    newValue -> FarmersDelightMod.CONFIG.setFoodEffectTooltip(newValue), true,
                    "Should meal and drink tooltips display which effects they provide?"));


    private final String text;
    private final Entry<?>[] entries;
    private final Category[] children;
    private final boolean isChild;

    Category(String text, boolean isChild, Entry<?>... entries) {
        this.text = text;
        this.entries = entries;
        this.children = new Category[0];
        this.isChild = isChild;
    }

    Category(String text, boolean isChild, Category[] children, Entry<?>... entries) {
        this.text = text;
        this.entries = entries;
        this.children = children;
        this.isChild = isChild;
    }

    public String text() {
        return text;
    }

    public Entry<?>[] entries() {
        return entries;
    }

    public Category[] children() {
        return children;
    }

    public boolean isChild() {
        return isChild;
    }

}
