package com.nhoryzon.mc.farmersdelight.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.nhoryzon.mc.farmersdelight.recipe.ingredient.ChanceResult;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;

import java.util.Optional;

public class CuttingBoardRecipeSerializer implements RecipeSerializer<CuttingBoardRecipe> {
    public static final int MAX_RESULTS = 4;

    private static final Codec<CuttingBoardRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codecs.createStrictOptionalFieldCodec(Codec.STRING, "group", "").forGetter(CuttingBoardRecipe::getGroup),
            Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf("ingredients").flatXmap(ingredients -> {
                if (ingredients.isEmpty()) {
                    return DataResult.error(() -> "No ingredients for cutting recipe");
                }
                if (ingredients.size() > 1) {
                    return DataResult.error(() -> "Too many ingredients for cutting recipe! Please define only one ingredient");
                }
                DefaultedList<Ingredient> nonNullList = DefaultedList.of();
                nonNullList.add(ingredients.get(0));
                return DataResult.success(ingredients.get(0));
            }, ingredient -> {
                DefaultedList<Ingredient> nonNullList = DefaultedList.of();
                nonNullList.add(ingredient);
                return DataResult.success(nonNullList);
            }).forGetter(cuttingBoardRecipe -> cuttingBoardRecipe.getIngredients().get(0)),
            Ingredient.ALLOW_EMPTY_CODEC.fieldOf("tool").forGetter(CuttingBoardRecipe::getTool),
            Codec.list(ChanceResult.CODEC).fieldOf("result").flatXmap(chanceResults -> {
                if (chanceResults.size() > 4) {
                    return DataResult.error(() -> "Too many results for cutting recipe! The maximum quantity of unique results is " + MAX_RESULTS);
                }
                DefaultedList<ChanceResult> nonNullList = DefaultedList.of();
                nonNullList.addAll(chanceResults);
                return DataResult.success(nonNullList);
            }, DataResult::success).forGetter(CuttingBoardRecipe::getRollableResults),
            Codecs.createStrictOptionalFieldCodec(Registries.SOUND_EVENT.getCodec(), "sound").forGetter(CuttingBoardRecipe::getSoundEvent)
    ).apply(inst, CuttingBoardRecipe::new));

    @Override
    public Codec<CuttingBoardRecipe> codec() {
        return CODEC;
    }

    @Override
    public CuttingBoardRecipe read(PacketByteBuf buf) {
        String groupIn = buf.readString(32767);
        Ingredient input = Ingredient.fromPacket(buf);
        Ingredient tool = Ingredient.fromPacket(buf);

        int resultSize = buf.readVarInt();
        DefaultedList<ChanceResult> resultList = DefaultedList.ofSize(resultSize, ChanceResult.EMPTY);
        resultList.replaceAll(ignored -> ChanceResult.read(buf));
        Optional<SoundEvent> soundEvent = Optional.empty();
        if (buf.readBoolean()) {
            Optional<RegistryEntry.Reference<SoundEvent>> holder = Registries.SOUND_EVENT.getEntry(buf.readRegistryKey(RegistryKeys.SOUND_EVENT));
            if (holder.isPresent() && holder.get().hasKeyAndValue()) {
                soundEvent = Optional.of(holder.get().value());
            }
        }

        return new CuttingBoardRecipe(groupIn, input, tool, resultList, soundEvent);
    }

    @Override
    public void write(PacketByteBuf buf, CuttingBoardRecipe recipe) {
        buf.writeString(recipe.getGroup());
        recipe.getIngredients().get(0).write(buf);
        recipe.getTool().write(buf);

        buf.writeVarInt(recipe.getRollableResults().size());
        for (ChanceResult result : recipe.getRollableResults()) {
            result.write(buf);
        }

        if (recipe.getSoundEvent().isPresent()) {
            Optional<RegistryKey<SoundEvent>> resourceKey = Registries.SOUND_EVENT.getKey(recipe.getSoundEvent().get());
            resourceKey.ifPresentOrElse(rk -> {
                buf.writeBoolean(true);
                buf.writeRegistryKey(rk);
            }, () -> buf.writeBoolean(false));
        } else {
            buf.writeBoolean(false);
        }
    }
}