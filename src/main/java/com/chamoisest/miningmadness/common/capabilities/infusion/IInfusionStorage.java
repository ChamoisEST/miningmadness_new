package com.chamoisest.miningmadness.common.capabilities.infusion;

import com.chamoisest.miningmadness.common.capabilities.infusion.infusions.base.Infusion;
import com.google.common.collect.ClassToInstanceMap;

import java.util.SortedMap;

public interface IInfusionStorage {


    <T extends Infusion> Infusion getInfusion(T infusion);
    <T extends Infusion> boolean hasInfusion(T infusion);

    <T extends Infusion> void setInfusionData(T infusion, int tier, int tierPoints);

    void setChanged();
    <T extends Infusion> void setChanged(T infusion);

    void setContainedInfusions(ClassToInstanceMap<Infusion> containedInfusions);
    ClassToInstanceMap<Infusion> getContainedInfusions();
    SortedMap<String, Infusion> getSortedInfusions();
    int getInfusionCount();

}
