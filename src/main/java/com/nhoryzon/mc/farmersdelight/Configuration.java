package com.nhoryzon.mc.farmersdelight;

import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.DyeColor;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Configuration {

    public static final int DURATION_VANILLA_SOUP = 6000;
    public static final int DURATION_RABBIT_STEW_JUMP = 200;

    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "farmersdelight.json");

    /* Settings */
    private boolean enableVanillaCropCrates = true;
    private boolean farmersBuyFDCrops = true;
    private boolean wanderingTraderSellsFDItems = true;
    private double richSoilBoostChance = 0.2;
    private double cuttingBoardFortuneBonus = 0.1;
    private boolean enableRopeReeling = true;
    private List<String> canvasSignDarkBackgroundList = Arrays.asList("gray", "purple", "blue", "brown", "green", "red", "black");

    /* Farming */
    private String defaultTomatoVineRope = "farmersdelight:rope";
    private boolean enableTomatoVineClimbingTaggedRopes = true;

    /* Overrides */
    private boolean vanillaSoupExtraEffects = true;
    private boolean rabbitStewJumpBoost = true;
    private boolean dispenserToolsCuttingBoard = true;

    /* Stack size */

    private boolean enableStackableSoupSize = true;
    private List<String> soupItemList = Arrays.asList("minecraft:mushroom_stew", "minecraft:beetroot_soup", "minecraft:rabbit_stew");
    private boolean overrideAllSoupItems = false;

    /* World */
    private boolean generateFDChestLoot = true;
    private boolean generateVillageCompostHeaps = true;
    private boolean generateWildCabbages = true;
    private int chanceWildCabbages = 30;
    private boolean generateWildBeetroots = true;
    private int chanceWildBeetroots = 30;
    private boolean generateWildPotatoes = true;
    private int chanceWildPotatoes = 100;
    private boolean generateWildOnions = true;
    private int chanceWildOnions = 120;
    private boolean generateWildCarrots = true;
    private int chanceWildCarrots = 120;
    private boolean generateWildTomatoes = true;
    private int chanceWildTomatoes = 100;
    private boolean generateWildRice = true;
    private int chanceWildRice = 20;

    /* Client */
    /* TODO : nourishedHungerOverlay */
    private boolean nourishedHungerOverlay = true;
    /* TODO : comfortHealthOverlay */
    private boolean comfortHealthOverlay = true;
    private boolean foodEffectTooltip = true;

    public Configuration() {
    }

    public static Configuration load() {
        Configuration configuration = new Configuration();
        if (!CONFIG_FILE.exists()) {
            save(configuration);
        }

        Reader reader;
        try {
            reader = Files.newBufferedReader(CONFIG_FILE.toPath());
            configuration = (new GsonBuilder().setPrettyPrinting().create()).fromJson(reader, Configuration.class);
            reader.close();
        } catch (IOException e) {
            FarmersDelightMod.LOGGER.error("Error while trying to load configuration file. Default configuration used.", e);
        }

        return configuration;
    }

    public static void save(Configuration config) {
        try {
            Writer writer = Files.newBufferedWriter(CONFIG_FILE.toPath());
            (new GsonBuilder().setPrettyPrinting().create()).toJson(config, writer);

            writer.close();
        } catch (IOException e) {
            FarmersDelightMod.LOGGER.error("Error while trying to save configuration file.", e);
        }
    }

    public boolean isEnableVanillaCropCrates() {
        return enableVanillaCropCrates;
    }

    public void setEnableVanillaCropCrates(boolean pEnableVanillaCropCrates) {
        enableVanillaCropCrates = pEnableVanillaCropCrates;
    }

    public boolean isFarmersBuyFDCrops() {
        return farmersBuyFDCrops;
    }

    public void setFarmersBuyFDCrops(boolean pFarmersBuyFDCrops) {
        farmersBuyFDCrops = pFarmersBuyFDCrops;
    }

    public boolean isWanderingTraderSellsFDItems() {
        return wanderingTraderSellsFDItems;
    }

    public void setWanderingTraderSellsFDItems(boolean pWanderingTraderSellsFDItems) {
        wanderingTraderSellsFDItems = pWanderingTraderSellsFDItems;
    }

    public double getRichSoilBoostChance() {
        return richSoilBoostChance;
    }

    public void setRichSoilBoostChance(double pRichSoilBoostChance) {
        richSoilBoostChance = limit(0.2, 1.0, pRichSoilBoostChance);
    }

    public double getCuttingBoardFortuneBonus() {
        return cuttingBoardFortuneBonus;
    }

    public void setCuttingBoardFortuneBonus(double pCuttingBoardFortuneBonus) {
        cuttingBoardFortuneBonus = limit(0.1, 1.0, pCuttingBoardFortuneBonus);
    }

    public boolean isEnableRopeReeling() {
        return enableRopeReeling;
    }

    public void setEnableRopeReeling(boolean pEnableRopeReeling) {
        enableRopeReeling = pEnableRopeReeling;
    }

    public List<String> getCanvasSignDarkBackgroundList() {
        return canvasSignDarkBackgroundList;
    }

    public List<DyeColor> getCanvasSignDarkBackgroundDyeList() {
        return getCanvasSignDarkBackgroundList().stream()
                .map(s -> DyeColor.byName(s, null))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    public void setCanvasSignDarkBackgroundList(List<String> pCanvasSignDarkBackgroundList) {
        canvasSignDarkBackgroundList = pCanvasSignDarkBackgroundList == null ? new ArrayList<>() :
                pCanvasSignDarkBackgroundList.stream()
                        .filter(s -> DyeColor.byName(s, null) != null)
                        .collect(Collectors.toList());
    }

    public String getDefaultTomatoVineRope() {
        return defaultTomatoVineRope;
    }

    public void setDefaultTomatoVineRope(String pDefaultTomatoVineRope) {
        defaultTomatoVineRope = pDefaultTomatoVineRope;
    }

    public boolean isEnableTomatoVineClimbingTaggedRopes() {
        return enableTomatoVineClimbingTaggedRopes;
    }

    public void setEnableTomatoVineClimbingTaggedRopes(boolean pEnableTomatoVineClimbingTaggedRopes) {
        enableTomatoVineClimbingTaggedRopes = pEnableTomatoVineClimbingTaggedRopes;
    }

    public boolean isVanillaSoupExtraEffects() {
        return vanillaSoupExtraEffects;
    }

    public void setVanillaSoupExtraEffects(boolean pVanillaSoupExtraEffects) {
        vanillaSoupExtraEffects = pVanillaSoupExtraEffects;
    }

    public boolean isRabbitStewJumpBoost() {
        return rabbitStewJumpBoost;
    }

    public void setRabbitStewJumpBoost(boolean pRabbitStewJumpBoost) {
        rabbitStewJumpBoost = pRabbitStewJumpBoost;
    }

    public boolean isDispenserToolsCuttingBoard() {
        return dispenserToolsCuttingBoard;
    }

    public void setDispenserToolsCuttingBoard(boolean pDispenserToolsCuttingBoard) {
        dispenserToolsCuttingBoard = pDispenserToolsCuttingBoard;
    }

    public boolean isEnableStackableSoupSize() {
        return enableStackableSoupSize;
    }

    public void setEnableStackableSoupSize(boolean pEnableStackableSoupSize) {
        enableStackableSoupSize = pEnableStackableSoupSize;
    }

    public List<String> getSoupItemList() {
        return soupItemList;
    }

    public void setSoupItemList(List<String> pSoupItemList) {
        soupItemList = pSoupItemList;
    }

    public boolean isOverrideAllSoupItems() {
        return overrideAllSoupItems;
    }

    public void setOverrideAllSoupItems(boolean pOverrideAllSoupItems) {
        overrideAllSoupItems = pOverrideAllSoupItems;
    }

    public boolean isGenerateFDChestLoot() {
        return generateFDChestLoot;
    }

    public void setGenerateFDChestLoot(boolean pGenerateFDChestLoot) {
        generateFDChestLoot = pGenerateFDChestLoot;
    }

    public boolean isGenerateVillageCompostHeaps() {
        return generateVillageCompostHeaps;
    }

    public void setGenerateVillageCompostHeaps(boolean pGenerateVillageCompostHeaps) {
        generateVillageCompostHeaps = pGenerateVillageCompostHeaps;
    }

    public boolean isGenerateWildCabbages() {
        return generateWildCabbages;
    }

    public void setGenerateWildCabbages(boolean pGenerateWildCabbages) {
        generateWildCabbages = pGenerateWildCabbages;
    }

    public int getChanceWildCabbages() {
        return chanceWildCabbages;
    }

    public void setChanceWildCabbages(int pChanceWildCabbages) {
        chanceWildCabbages = limit(0, Integer.MAX_VALUE, pChanceWildCabbages);
    }

    public boolean isGenerateWildBeetroots() {
        return generateWildBeetroots;
    }

    public void setGenerateWildBeetroots(boolean pGenerateWildBeetroots) {
        generateWildBeetroots = pGenerateWildBeetroots;
    }

    public int getChanceWildBeetroots() {
        return chanceWildBeetroots;
    }

    public void setChanceWildBeetroots(int pChanceWildBeetroots) {
        chanceWildBeetroots = limit(0, Integer.MAX_VALUE, pChanceWildBeetroots);
    }

    public boolean isGenerateWildPotatoes() {
        return generateWildPotatoes;
    }

    public void setGenerateWildPotatoes(boolean pGenerateWildPotatoes) {
        generateWildPotatoes = pGenerateWildPotatoes;
    }

    public int getChanceWildPotatoes() {
        return chanceWildPotatoes;
    }

    public void setChanceWildPotatoes(int pChanceWildPotatoes) {
        chanceWildPotatoes = limit(0, Integer.MAX_VALUE, pChanceWildPotatoes);
    }

    public boolean isGenerateWildOnions() {
        return generateWildOnions;
    }

    public void setGenerateWildOnions(boolean pGenerateWildOnions) {
        generateWildOnions = pGenerateWildOnions;
    }

    public int getChanceWildOnions() {
        return chanceWildOnions;
    }

    public void setChanceWildOnions(int pChanceWildOnions) {
        chanceWildOnions = limit(0, Integer.MAX_VALUE, pChanceWildOnions);
    }

    public boolean isGenerateWildCarrots() {
        return generateWildCarrots;
    }

    public void setGenerateWildCarrots(boolean pGenerateWildCarrots) {
        generateWildCarrots = pGenerateWildCarrots;
    }

    public int getChanceWildCarrots() {
        return chanceWildCarrots;
    }

    public void setChanceWildCarrots(int pChanceWildCarrots) {
        chanceWildCarrots = limit(0, Integer.MAX_VALUE, pChanceWildCarrots);
    }

    public boolean isGenerateWildTomatoes() {
        return generateWildTomatoes;
    }

    public void setGenerateWildTomatoes(boolean pGenerateWildTomatoes) {
        generateWildTomatoes = pGenerateWildTomatoes;
    }

    public int getChanceWildTomatoes() {
        return chanceWildTomatoes;
    }

    public void setChanceWildTomatoes(int pChanceWildTomatoes) {
        chanceWildTomatoes = limit(0, Integer.MAX_VALUE, pChanceWildTomatoes);
    }

    public boolean isGenerateWildRice() {
        return generateWildRice;
    }

    public void setGenerateWildRice(boolean pGenerateWildRice) {
        generateWildRice = pGenerateWildRice;
    }

    public int getChanceWildRice() {
        return chanceWildRice;
    }

    public void setChanceWildRice(int pChanceWildRice) {
        chanceWildRice = limit(0, Integer.MAX_VALUE, pChanceWildRice);
    }

    public boolean isNourishedHungerOverlay() {
        return nourishedHungerOverlay;
    }

    public void setNourishedHungerOverlay(boolean pNourishedHungerOverlay) {
        nourishedHungerOverlay = pNourishedHungerOverlay;
    }

    public boolean isComfortHealthOverlay() {
        return comfortHealthOverlay;
    }

    public void setComfortHealthOverlay(boolean pComfortHealthOverlay) {
        comfortHealthOverlay = pComfortHealthOverlay;
    }

    public boolean isFoodEffectTooltip() {
        return foodEffectTooltip;
    }

    public void setFoodEffectTooltip(boolean pFoodEffectTooltip) {
        foodEffectTooltip = pFoodEffectTooltip;
    }

    private static double limit(double min, double max, double value) {
        if (value > max) {
            return max;
        }

        return Math.max(value, min);
    }

    private static int limit(int min, int max, int value) {
        if (value > max) {
            return max;
        }

        return Math.max(value, min);
    }

}
