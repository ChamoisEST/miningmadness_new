package com.chamoisest.miningmadness.setup;

import com.chamoisest.miningmadness.MiningMadness;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSetup {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MiningMadness.MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB_MININGMADNESS = CREATIVE_MODE_TABS.register(MiningMadness.MODID, () -> CreativeModeTab.builder()
            .title(Component.translatable("miningmadness.creative_tab.title"))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> new ItemStack(Registration.QUARRY.get()))
            .displayItems((parameters, output) -> {
                Registration.ITEMS.getEntries().forEach(entry -> {
                    Item item = entry.get();
                    output.accept(item);
                });
                Registration.BLOCKS.getEntries().forEach(entry -> {
                    Block block = entry.get();
                    output.accept(block);
                });
            })
            .build());
}
