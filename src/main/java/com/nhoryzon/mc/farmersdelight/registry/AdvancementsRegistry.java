package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.advancement.CuttingBoardTrigger;
import net.fabricmc.fabric.api.object.builder.v1.advancement.CriterionRegistry;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;

import java.util.function.Supplier;

public enum AdvancementsRegistry {
    CUTTING_BOARD(CuttingBoardTrigger::new);

    private final Supplier<Criterion<? extends CriterionConditions>> criterionSupplier;
    private Criterion<? extends CriterionConditions> criterion;

    AdvancementsRegistry(Supplier<Criterion<? extends CriterionConditions>> criterionSupplier) {
        this.criterionSupplier = criterionSupplier;
    }

    public static void registerAll() {
        for (AdvancementsRegistry value : values()) {
            CriterionRegistry.register(value.get());
        }
    }

    public Criterion<? extends CriterionConditions> get() {
        if (criterion == null) {
            criterion = criterionSupplier.get();
        }

        return criterion;
    }

}