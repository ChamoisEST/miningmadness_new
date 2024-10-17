package com.chamoisest.miningmadness.common.blockentities.data;

import com.chamoisest.miningmadness.common.blockentities.interfaces.StatusBE;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerData;

import java.util.ArrayList;
import java.util.List;

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
        CONFLICTING_INFUSION,
        FINISHED,
        INVERTORY_FULL,
        BLACKLISTED_DIMENSION;

        public int getNumericalValue() {
            return this.ordinal();
        }

        public static Status getEnumValue(int value) {
            return values()[value];
        }

        public Component getTranslated() {
            return switch (this) {
                case ACTIVE -> Component.translatable("miningmadness.status.active");
                case INACTIVE -> Component.translatable("miningmadness.status.inactive");
                case NOT_ENOUGH_FE -> Component.translatable("miningmadness.status.not_enough_fe");
                case CANT_GAIN_INFUSION -> Component.translatable("miningmadness.status.cant_gain_infusion");
                case CONFLICTING_INFUSION -> Component.translatable("miningmadness.status.conflicting_infusion");
                case FINISHED -> Component.translatable("miningmadness.status.finished");
                case INVERTORY_FULL -> Component.translatable("miningmadness.status.invertory_full");
                case BLACKLISTED_DIMENSION -> Component.translatable("miningmadness.status.blacklisted_dimension");
            };
        }

        public int getColor() {
            return switch (this){
                case ACTIVE -> 0xff4CFF00;
                case INACTIVE, BLACKLISTED_DIMENSION -> 0xff7F0000;
                case NOT_ENOUGH_FE, CANT_GAIN_INFUSION, CONFLICTING_INFUSION, FINISHED, INVERTORY_FULL -> 0xffFFD800;
            };
        }

        public static List<Status> getActiveStatuses(){
            List<Status> activeStatuses = new ArrayList<>();
            activeStatuses.add(Status.ACTIVE);
            activeStatuses.add(Status.NOT_ENOUGH_FE);
            activeStatuses.add(Status.FINISHED);
            activeStatuses.add(Status.INVERTORY_FULL);
            return activeStatuses;
        }
    }
}
