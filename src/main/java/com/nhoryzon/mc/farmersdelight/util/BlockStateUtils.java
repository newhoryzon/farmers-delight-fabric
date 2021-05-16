package com.nhoryzon.mc.farmersdelight.util;

@SuppressWarnings({"deprecation", "unused"})
public class BlockStateUtils {
    /**
     * Calls
     * {@link net.minecraft.block.Block#neighborUpdate(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.Block, net.minecraft.util.math.BlockPos, boolean)}
     * neighborChanged} on surrounding blocks (with isMoving as false). Also updates comparator output state.
     */
    public static final int NOTIFY_NEIGHBORS     = (1);
    /**
     * Calls {@link net.minecraft.world.World#updateListeners(net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState, net.minecraft.block.BlockState, int)}.<br>
     * Server-side, this updates all the path-finding navigators.
     */
    public static final int BLOCK_UPDATE         = (1 << 1);
    /**
     * Stops the blocks from being marked for a render update
     */
    public static final int NO_RERENDER          = (1 << 2);
    /**
     * Makes the block be re-rendered immediately, on the main thread.
     * If NO_RERENDER is set, then this will be ignored
     */
    public static final int RERENDER_MAIN_THREAD = (1 << 3);
    /**
     * Causes neighbor updates to be sent to all surrounding blocks (including
     * diagonals).
     */
    public static final int UPDATE_NEIGHBORS     = (1 << 4);

    /**
     * Prevents neighbor changes from spawning item drops, used by
     * {@link net.minecraft.block.Block#replace(net.minecraft.block.BlockState, net.minecraft.block.BlockState, net.minecraft.world.WorldAccess, net.minecraft.util.math.BlockPos, int)}.
     */
    public static final int NO_NEIGHBOR_DROPS    = (1 << 5);

    /**
     * Tell the block being changed that it was moved, rather than removed/replaced,
     * the boolean value is eventually passed to
     * {@link net.minecraft.block.Block#onStateReplaced(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState, boolean)}
     * as the last parameter.
     */
    public static final int IS_MOVING            = (1 << 6);

    public static final int DEFAULT = NOTIFY_NEIGHBORS | BLOCK_UPDATE;
    public static final int DEFAULT_AND_RERENDER = DEFAULT | RERENDER_MAIN_THREAD;


    private BlockStateUtils() {
    }
}