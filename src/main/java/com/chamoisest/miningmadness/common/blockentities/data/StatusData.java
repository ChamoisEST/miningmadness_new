package com.chamoisest.miningmadness.common.blockentities.data;

import com.chamoisest.miningmadness.common.blockentities.interfaces.StatusBE;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerData;

public class StatusData implements ContainerData {
    private final StatusBE blockEntity;
    public static int DATA_SLOTS = 2;

    public StatusData(StatusBE blockEntity){
        this.blockEntity = blockEntity;
    }
    @Override
    public int get(int index) {
        return switch (index) {
            case 0 -> blockEntity.getStatus().getNumericalValue();
            case 1 -> blockEntity.addButton() ? 1 : 0;
            default -> throw new IllegalArgumentException("Invalid index: " + index);
        };
    }

    @Override
    public void set(int index, int value) {
        switch (index) {
            case 0 -> blockEntity.setStatus(Status.getEnumValue(value));
            case 1 -> {}
            default -> throw new IllegalArgumentException("Invalid index: " + index);
        };
    }

    @Override
    public int getCount() {
        return DATA_SLOTS;
    }

    public Status getStatus() {
        return Status.getEnumValue(get(0));
    }

    public boolean getAddButton() {
        return get(1) == 1;
    }

    public enum Status {
        ACTIVE,
        INACTIVE,
        NOT_ENOUGH_FE,
        CANT_GAIN_INFUSION,
        CONFLICTING_INFUSION;

        public int getNumericalValue() {
            return switch (this) {
                case ACTIVE -> 0;
                case INACTIVE -> 1;
                case NOT_ENOUGH_FE -> 2;
                case CANT_GAIN_INFUSION -> 3;
                case CONFLICTING_INFUSION -> 4;
            };
        }

        public static Status getEnumValue(int value) {
            return switch (value) {
                case 0 -> ACTIVE;
                case 1 -> INACTIVE;
                case 2 -> NOT_ENOUGH_FE;
                case 3 -> CANT_GAIN_INFUSION;
                case 4 -> CONFLICTING_INFUSION;
                default -> throw new IllegalStateException("Unexpected value: " + value);
            };
        }

        public Component getTranslated() {
            return switch (this) {
                case ACTIVE -> Component.translatable("miningmadness.status.active");
                case INACTIVE -> Component.translatable("miningmadness.status.inactive");
                case NOT_ENOUGH_FE -> Component.translatable("miningmadness.status.not_enough_fe");
                case CANT_GAIN_INFUSION -> Component.translatable("miningmadness.status.cant_gain_infusion");
                case CONFLICTING_INFUSION -> Component.translatable("miningmadness.status.conflicting_infusion");
            };
        }

        public int getColor() {
            return switch (this){
                case ACTIVE -> 0xff4CFF00;
                case INACTIVE -> 0xff7F0000;
                case NOT_ENOUGH_FE, CANT_GAIN_INFUSION, CONFLICTING_INFUSION -> 0xffFFD800;
            };
        }
    }
}
