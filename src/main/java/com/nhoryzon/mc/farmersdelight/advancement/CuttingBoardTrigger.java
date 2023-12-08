package com.nhoryzon.mc.farmersdelight.advancement;

import com.google.gson.JsonObject;
import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class CuttingBoardTrigger extends AbstractCriterion<CuttingBoardTrigger.Instance> {
    public static final Identifier ID = new Identifier(FarmersDelightMod.MOD_ID, "use_cutting_board");

    @Override
    protected Instance conditionsFromJson(JsonObject obj, Optional<LootContextPredicate> playerPredicate,
            AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new CuttingBoardTrigger.Instance(playerPredicate);
    }

    public void trigger(ServerPlayerEntity player) {
        trigger(player, Instance::test);
    }

    public static class Instance extends AbstractCriterionConditions {

        public Instance(Optional<LootContextPredicate> playerPredicate) {
            super(playerPredicate);
        }

        public boolean test() {
            return true;
        }

    }
}