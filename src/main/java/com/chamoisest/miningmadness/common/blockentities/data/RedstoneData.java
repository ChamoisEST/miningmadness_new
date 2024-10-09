package com.chamoisest.miningmadness.common.blockentities.data;

import com.chamoisest.miningmadness.common.blockentities.interfaces.RedstoneControlledBE;
import com.chamoisest.miningmadness.common.blockentities.interfaces.StatusBE;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerData;

public class RedstoneData implements ContainerData {
    private final RedstoneControlledBE blockEntity;
    public static int DATA_SLOTS = 1;

    public RedstoneData(RedstoneControlledBE blockEntity){
        this.blockEntity = blockEntity;
    }
    @Override
    public int get(int index) {
        return switch (index) {
            case 0 -> blockEntity.getRedstoneStatus().getNumericalValue();
            default -> throw new IllegalArgumentException("Invalid index: " + index);
        };
    }

    @Override
    public void set(int index, int value) {
        switch (index) {
            case 0 -> blockEntity.setRedstoneStatus(RedstoneStatus.getEnumValue(value));
            default -> throw new IllegalArgumentException("Invalid index: " + index);
        };
    }

    @Override
    public int getCount() {
        return DATA_SLOTS;
    }

    public RedstoneStatus getRedstoneStatus() {
        return RedstoneStatus.getEnumValue(get(0));
    }

    public enum RedstoneStatus {
        HIGH,
        LOW,
        PULSE,
        IGNORE;

        public int getNumericalValue() {
            return switch (this) {
                case HIGH -> 0;
                case LOW -> 1;
                case PULSE -> 2;
                case IGNORE -> 3;
            };
        }

        public static RedstoneStatus getEnumValue(int value) {
            return switch (value) {
                case 0 -> HIGH;
                case 1 -> LOW;
                case 2 -> PULSE;
                case 3 -> IGNORE;
                default -> throw new IllegalStateException("Unexpected value: " + value);
            };
        }

        public Component getTranslated() {
            return switch (this) {
                case HIGH -> Component.translatable("miningmadness.redstone_status.high");
                case LOW -> Component.translatable("miningmadness.redstone_status.low");
                case PULSE -> Component.translatable("miningmadness.redstone_status.pulse");
                case IGNORE -> Component.translatable("miningmadness.redstone_status.ignore");
            };
        }
    }
}
