package com.nhoryzon.mc.farmersdelight.integration.rei;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.recipe.ingredient.ChanceResult;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Formatting;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class ChanceArrayIngredient extends AbstractList<EntryStack<?>> implements EntryIngredient, RandomAccess {

    private final EntryStack<?>[] array;

    private final float chance;

    public ChanceArrayIngredient(ChanceResult chanceResult) {
        this(new EntryStack[]{EntryStacks.of(Objects.requireNonNull(chanceResult.stack()))}, chanceResult.chance());
    }

    public ChanceArrayIngredient(EntryStack<?>[] array, float chance) {
        this.array = Objects.requireNonNull(array);
        this.chance = chance;
        Arrays.stream(this.array).forEach(entryStack -> entryStack.tooltip(FarmersDelightMod.i18n("rei.chance",
                chance < 0.01 ? "<1" : (int) (chance * 100)).formatted(Formatting.GOLD)));
    }

    public float getChance() {
        return chance;
    }

    @Override
    public int size() {
        return array.length;
    }

    @Override
    public Object[] toArray() {
        return toArray(new Object[0]);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        int size = size();
        if (a.length < size) {
            return Arrays.copyOf(this.array, size, (Class<? extends T[]>) a.getClass());
        }
        System.arraycopy(this.array, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }

    @Override
    public EntryStack<?> get(int index) {
        return array[index];
    }

    @Override
    public EntryStack<?> set(int index, EntryStack<?> element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        EntryStack<?>[] a = this.array;
        if (o == null) {
            for (int i = 0; i < a.length; i++) {
                if (a[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < a.length; i++) {
                if (o.equals(a[i])) {
                    return i;
                }
            }
        }

        return -1;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public Spliterator<EntryStack<?>> spliterator() {
        return Spliterators.spliterator(array, Spliterator.ORDERED | Spliterator.IMMUTABLE);
    }

    @Override
    public void forEach(Consumer<? super EntryStack<?>> action) {
        Objects.requireNonNull(action);
        for (EntryStack<?> stack : array) {
            action.accept(stack);
        }
    }

    @Override
    public void replaceAll(UnaryOperator<EntryStack<?>> operator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sort(Comparator<? super EntryStack<?>> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NbtList save() {
        NbtList listTag = new NbtList();
        for (EntryStack<?> stack : array) {
            listTag.add(stack.save());
        }

        return listTag;
    }

    @Override
    public EntryIngredient filter(Predicate<EntryStack<?>> filter) {
        return EntryIngredient.of(stream().filter(filter).toArray(EntryStack[]::new));
    }

    @Override
    public EntryIngredient map(UnaryOperator<EntryStack<?>> transformer) {
        EntryStack<?>[] out = new EntryStack[array.length];
        for (int i = 0; i < array.length; i++) {
            out[i] = transformer.apply(array[i]);
        }

        return new ChanceArrayIngredient(out, chance);
    }

}
