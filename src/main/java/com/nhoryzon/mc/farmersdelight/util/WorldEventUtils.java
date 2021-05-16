package com.nhoryzon.mc.farmersdelight.util;

@SuppressWarnings("unused")
public class WorldEventUtils {

    public static final int DISPENSER_DISPENSE_SOUND        = 1000;
    public static final int DISPENSER_FAIL_SOUND            = 1001;
    /**
     * Like DISPENSER_DISPENSE_SOUND, but for items that are fired (arrows, eggs, snowballs)
     */
    public static final int DISPENSER_LAUNCH_SOUND          = 1002;
    public static final int ENDEREYE_LAUNCH_SOUND           = 1003;
    public static final int FIREWORK_SHOOT_SOUND            = 1004;
    public static final int IRON_DOOR_OPEN_SOUND            = 1005;
    public static final int WOODEN_DOOR_OPEN_SOUND          = 1006;
    public static final int WOODEN_TRAPDOOR_OPEN_SOUND      = 1007;
    public static final int FENCE_GATE_OPEN_SOUND           = 1008;
    public static final int FIRE_EXTINGUISH_SOUND           = 1009;
    /**
     * {@code data} is the item ID of the record you want to play
     */
    public static final int PLAY_RECORD_SOUND               = 1010;
    public static final int IRON_DOOR_CLOSE_SOUND           = 1011;
    public static final int WOODEN_DOOR_CLOSE_SOUND         = 1012;
    public static final int WOODEN_TRAPDOOR_CLOSE_SOUND     = 1013;
    public static final int FENCE_GATE_CLOSE_SOUND          = 1014;
    public static final int GHAST_WARN_SOUND                = 1015;
    public static final int GHAST_SHOOT_SOUND               = 1016;
    public static final int ENDERDRAGON_SHOOT_SOUND         = 1017;
    public static final int BLAZE_SHOOT_SOUND               = 1018;
    public static final int ZOMBIE_ATTACK_DOOR_WOOD_SOUND   = 1019;
    public static final int ZOMBIE_ATTACK_DOOR_IRON_SOUND   = 1020;
    public static final int ZOMBIE_BREAK_DOOR_WOOD_SOUND    = 1021;
    public static final int WITHER_BREAK_BLOCK_SOUND        = 1022;
    public static final int WITHER_BREAK_BLOCK              = 1023;
    public static final int WITHER_SHOOT_SOUND              = 1024;
    public static final int BAT_TAKEOFF_SOUND               = 1025;
    public static final int ZOMBIE_INFECT_SOUND             = 1026;
    public static final int ZOMBIE_VILLAGER_CONVERTED_SOUND = 1027;
    public static final int ANVIL_DESTROYED_SOUND           = 1029;
    public static final int ANVIL_USE_SOUND                 = 1030;
    public static final int ANVIL_LAND_SOUND                = 1031;
    public static final int PORTAL_TRAVEL_SOUND             = 1032;
    public static final int CHORUS_FLOWER_GROW_SOUND        = 1033;
    public static final int CHORUS_FLOWER_DEATH_SOUND       = 1034;
    public static final int BREWING_STAND_BREW_SOUND        = 1035;
    public static final int IRON_TRAPDOOR_CLOSE_SOUND       = 1036;
    public static final int IRON_TRAPDOOR_OPEN_SOUND        = 1037;
    public static final int PHANTOM_BITE_SOUND              = 1039;
    public static final int ZOMBIE_CONVERT_TO_DROWNED_SOUND = 1040;
    public static final int HUSK_CONVERT_TO_ZOMBIE_SOUND    = 1041;
    public static final int GRINDSTONE_USE_SOUND            = 1042;
    public static final int ITEM_BOOK_TURN_PAGE_SOUND       = 1043;
    /**
     * Spawns the composter particles and plays the sound event sound event<br>
     * {@code data} is bigger than 0 when the composter can still be filled up, and is smaller or equal to 0 when the composter is full. (This only effects the sound event)
     */
    public static final int COMPOSTER_FILLED_UP             = 1500;
    public static final int LAVA_EXTINGUISH                 = 1501;
    public static final int REDSTONE_TORCH_BURNOUT          = 1502;
    public static final int END_PORTAL_FRAME_FILL           = 1503;
    /**
     * {@code data} is the {@link net.minecraft.util.math.Direction#getId()} of the direction the smoke is to come out of.
     */
    public static final int DISPENSER_SMOKE                 = 2000;
    public static final int BREAK_BLOCK_EFFECTS             = 2001;
    /**
     * {@code data} is the rgb color int that should be used for the potion particles<br>
     * This is the same as {@link WorldEventUtils#POTION_IMPACT} but uses the particle type {@link net.minecraft.particle.ParticleTypes#EFFECT}
     */
    public static final int POTION_IMPACT_INSTANT           = 2002;
    public static final int ENDER_EYE_SHATTER               = 2003;
    public static final int MOB_SPAWNER_PARTICLES           = 2004;
    /**
     * {@code data} is the amount of particles to spawn. If {@code data} is 0 then there will be 15 particles spawned
     */
    public static final int BONEMEAL_PARTICLES              = 2005;
    public static final int DRAGON_FIREBALL_HIT             = 2006;
    /**
     * {@code data} is the rgb color int that should be used for the potion particles<br>
     * This is the same as {@link WorldEventUtils#POTION_IMPACT_INSTANT} but uses the particle type {@link net.minecraft.particle.ParticleTypes#INSTANT_EFFECT}
     */
    public static final int POTION_IMPACT                   = 2007;
    public static final int SPAWN_EXPLOSION_PARTICLE        = 2008;
    public static final int GATEWAY_SPAWN_EFFECTS           = 3000;
    public static final int ENDERMAN_GROWL_SOUND            = 3001;

    private WorldEventUtils() {
    }
}