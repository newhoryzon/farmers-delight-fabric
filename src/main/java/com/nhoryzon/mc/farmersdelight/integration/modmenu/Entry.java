package com.nhoryzon.mc.farmersdelight.integration.modmenu;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(value= EnvType.CLIENT)
public abstract class Entry<T> {

    private final String text;
    private final String[] tooltip;
    private final Supplier<T> current;
    private final Consumer<T> saver;
    private final T defaultValue;

    private final T min;
    private final T max;

    public static Entry<Boolean> bool(String name, Supplier<Boolean> current, Consumer<Boolean> saver, Boolean defaultValue, String... tooltip) {
        return new Entry<>(name, current, saver, defaultValue, tooltip) {
            @Override
            TooltipListEntry<Boolean> build(ConfigEntryBuilder builder) {
                return builder.startBooleanToggle(Text.literal(getText()), getCurrent().get())
                        .setSaveConsumer(getSaver())
                        .setTooltip(Arrays.stream(getTooltip()).map(Text::literal).toArray(Text[]::new))
                        .setDefaultValue(getDefaultValue())
                        .build();
            }
        };
    }

    public static Entry<Double> doubl(String name, Supplier<Double> current, Consumer<Double> saver, Double defaultValue, Double min, Double max, String... tooltip) {
        return new Entry<>(name, current, saver, defaultValue, min, max, tooltip) {
            @Override
            TooltipListEntry<Double> build(ConfigEntryBuilder builder) {
                return builder.startDoubleField(Text.literal(getText()), getCurrent().get())
                        .setSaveConsumer(getSaver())
                        .setTooltip(Arrays.stream(getTooltip()).map(Text::literal).toArray(Text[]::new))
                        .setDefaultValue(getDefaultValue()).setMin(getMin()).setMax(getMax())
                        .build();
            }
        };
    }

    public static Entry<Integer> inte(String name, Supplier<Integer> current, Consumer<Integer> saver, Integer defaultValue, Integer min, Integer max, String... tooltip) {
        return new Entry<>(name, current, saver, defaultValue, min, max, tooltip) {
            @Override
            TooltipListEntry<Integer> build(ConfigEntryBuilder builder) {
                return builder.startIntField(Text.literal(getText()), getCurrent().get())
                        .setSaveConsumer(getSaver())
                        .setTooltip(Arrays.stream(getTooltip()).map(Text::literal).toArray(Text[]::new))
                        .setDefaultValue(getDefaultValue()).setMin(getMin()).setMax(getMax())
                        .build();
            }
        };
    }

    public static Entry<List<String>> list(String name, Supplier<List<String>> current, Consumer<List<String>> saver, List<String> defaultValue, String... tooltip) {
        return new Entry<>(name, current, saver, defaultValue, tooltip) {
            @Override
            TooltipListEntry<List<String>> build(ConfigEntryBuilder builder) {
                return builder.startStrList(Text.literal(getText()), getCurrent().get())
                        .setSaveConsumer(getSaver())
                        .setTooltip(Arrays.stream(getTooltip()).map(Text::literal).toArray(Text[]::new))
                        .setDefaultValue(getDefaultValue())
                        .build();
            }
        };
    }

    public static Entry<String> str(String name, Supplier<String> current, Consumer<String> saver, String defaultValue, String... tooltip) {
        return new Entry<>(name, current, saver, defaultValue, tooltip) {
            @Override
            TooltipListEntry<String> build(ConfigEntryBuilder builder) {
                return builder.startStrField(Text.literal(getText()), getCurrent().get())
                        .setSaveConsumer(getSaver())
                        .setTooltip(Arrays.stream(getTooltip()).map(Text::literal).toArray(Text[]::new))
                        .setDefaultValue(getDefaultValue())
                        .build();
            }
        };
    }

    private Entry(String name, Supplier<T> current, Consumer<T> saver, T defaultValue, String... tooltip) {
        this(name, current, saver, defaultValue, null, null, tooltip);
    }

    private Entry(String name, Supplier<T> current, Consumer<T> saver, T defaultValue, T min, T max, String... tooltip) {
        this.text = name;
        this.current = current;
        this.saver = saver;
        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;
        this.tooltip = tooltip;
    }

    abstract TooltipListEntry<T> build(ConfigEntryBuilder builder);

    public String getText() {
        return text;
    }

    public String[] getTooltip() {
        return tooltip;
    }

    public Supplier<T> getCurrent() {
        return current;
    }

    public Consumer<T> getSaver() {
        return saver;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

}
