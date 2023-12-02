package com.nhoryzon.mc.farmersdelight.registry;

import com.nhoryzon.mc.farmersdelight.advancement.CuttingBoardTrigger;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public enum AdvancementsRegistry {
    CUTTING_BOARD(CuttingBoardTrigger.ID, CuttingBoardTrigger::new);

    private final Identifier id;
    private final Supplier<Criterion<? extends CriterionConditions>> criterionSupplier;
    private Criterion<? extends CriterionConditions> criterion;

    AdvancementsRegistry(Identifier id, Supplier<Criterion<? extends CriterionConditions>> criterionSupplier) {
        this.id = id;
        this.criterionSupplier = criterionSupplier;
    }

    public static void registerAll() {
        for (AdvancementsRegistry value : values()) {
            Criteria.register(value.id.toString(), value.get());
        }
    }

    public Criterion<? extends CriterionConditions> get() {
        if (criterion == null) {
            criterion = criterionSupplier.get();
        }

        return criterion;
    }

}