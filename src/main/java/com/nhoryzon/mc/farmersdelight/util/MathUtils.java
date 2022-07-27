package com.nhoryzon.mc.farmersdelight.util;

import com.nhoryzon.mc.farmersdelight.item.inventory.ItemHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public final class MathUtils {
    public static final Random RAND = new Random();

    private MathUtils() { }

    public static int calcRedstoneFromItemHandler(ItemHandler handler) {
        if (handler == null) {
            return 0;
        } else {
            int i = 0;
            float f = .0f;

            for (int j = 0; j < handler.size(); i++) {
                ItemStack itemStack = handler.getStack(j);
                if (!itemStack.isEmpty()) {
                    f += (float) itemStack.getCount() / (float) Math.min(handler.getMaxCountForSlot(j), itemStack.getMaxCount());
                    i++;
                }
            }

            if (handler.size() > 0) {
                f = f / (float) handler.size();
            }

            return MathHelper.floor(f * 14.f) + (i > 0 ? 1 : 0);
        }
    }

}