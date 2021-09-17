package com.nhoryzon.mc.farmersdelight.exception;

public class SlotInvalidRangeException extends RuntimeException {

    public SlotInvalidRangeException(int slotNumber, int maxRange) {
        super("Slot " + slotNumber + " not in valid range - [0," + maxRange + ")");
    }

}
