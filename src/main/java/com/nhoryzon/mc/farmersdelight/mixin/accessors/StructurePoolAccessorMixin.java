package com.nhoryzon.mc.farmersdelight.mixin.accessors;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(StructurePool.class)
public interface StructurePoolAccessorMixin {

    @Accessor
    ObjectArrayList<StructurePoolElement> getElements();

    @Accessor
    List<Pair<StructurePoolElement, Integer>> getElementCounts();

    @Accessor
    @Mutable
    void setElementCounts(List<Pair<StructurePoolElement, Integer>> elementCounts);

}
