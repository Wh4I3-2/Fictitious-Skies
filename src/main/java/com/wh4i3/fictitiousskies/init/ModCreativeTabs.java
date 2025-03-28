package com.wh4i3.fictitiousskies.init;

import com.wh4i3.fictitiousskies.FictitiousSkies;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FictitiousSkies.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WOTR_TAB =
            CREATIVE_MODE_TABS.register(FictitiousSkies.MODID,
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup." + FictitiousSkies.MODID))
                            .displayItems((parameters, output) -> {
                                output.accept(ModItems.B_ROLL.get());
                                output.accept(ModItems.createBRoll(FictitiousSkies.id("textures/environment/starmap_2020.png"), true, "starmap_2020"));
                                output.accept(ModItems.createBRoll(FictitiousSkies.id("textures/environment/skybox.png"), true, "clouds"));
                                ModItems.BLOCK_ITEMS.forEach(item -> output.accept(item.get()));
                            }).build());

}
