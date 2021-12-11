package com.nhoryzon.mc.farmersdelight.block;

import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import com.nhoryzon.mc.farmersdelight.tag.Tags;
import com.nhoryzon.mc.farmersdelight.util.BlockStateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.util.Random;

public class OrganicCompostBlock extends Block {
    public static final int MAX_COMPOSTING_STAGE = 7;
    public static final IntProperty COMPOSTING = IntProperty.of("composting", 0, MAX_COMPOSTING_STAGE);

    public OrganicCompostBlock() {
        super(FabricBlockSettings.copyOf(Blocks.DIRT).hardness(1.2f).resistance(1.2f).sounds(BlockSoundGroup.CROP));
        setDefaultState(getStateManager().getDefaultState().with(COMPOSTING, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(COMPOSTING);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    @Environment(value= EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        if (random.nextInt(10) == 0) {
            world.addParticle(ParticleTypes.MYCELIUM,
                    (double) pos.getX() + (double) random.nextFloat(),
                    pos.getY() + 1.1d,
                    (double) pos.getZ() + (double) random.nextFloat(),
                    .0d, .0d, .0d);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.isClient()) {
            return;
        }

        float chance = .0f;
        boolean hasWater = false;
        int maxLight = 0;

        for (BlockPos neighborPos : BlockPos.iterate(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            BlockState neighborState = world.getBlockState(neighborPos);
            if (neighborState.isIn(Tags.COMPOST_ACTIVATORS)) {
                chance += .02f;
            }
            if (neighborState.getFluidState().isIn(FluidTags.WATER)) {
                hasWater = true;
            }
            int light = world.getLightLevel(LightType.SKY, neighborPos.up());
            if (light > maxLight) {
                maxLight = light;
            }
        }

        chance += maxLight > 12 ? .1f : .05f;
        chance += hasWater ? .1f : .0f;

        if (world.getRandom().nextFloat() <= chance) {
            if (state.get(COMPOSTING) == MAX_COMPOSTING_STAGE) {
                world.setBlockState(pos, BlocksRegistry.RICH_SOIL.get().getDefaultState(), BlockStateUtils.BLOCK_UPDATE); // finished
            } else {
                world.setBlockState(pos, state.with(COMPOSTING, state.get(COMPOSTING) + 1), BlockStateUtils.BLOCK_UPDATE); // next stage
            }
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return (MAX_COMPOSTING_STAGE + 1 - state.get(COMPOSTING));
    }
}