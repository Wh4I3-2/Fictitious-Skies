package com.wh4i3.fictitiousskies.init;

import com.wh4i3.fictitiousskies.FictitiousSkies;
import com.wh4i3.fictitiousskies.item.BRollItem;

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
                            .icon(() -> BRollItem.createBRollStack(null, true, null, "", 0x151515, 0x403f3d))
                            .displayItems((parameters, output) -> {
                                output.accept(ModItems.EMPTY_SKY_DISK.get());

                                output.accept(BRollItem.createBRollStack(
                                    FictitiousSkies.id("textures/environment/starmap_2020.png"), 
                                    true, null, "NASA - Deep Star Maps 2020",
                                    0x151515, 0x403f3d
                                ));
                                output.accept(BRollItem.createBRollStack(
                                    FictitiousSkies.id("textures/environment/glast.png"), 
                                    true, null, "NASA - Simulations of the Gamma-Ray Sky",
                                    0x160c31, 0x986af0
                                ));
                                output.accept(BRollItem.createBRollStack(
                                    FictitiousSkies.id("textures/environment/tycho_skymap.png"), 
                                    true, null, "NASA - The Tycho Catalog Skymap Version 2.0",
                                    0x181715, 0x716663
                                ));

                                output.accept(BRollItem.createBRollStack(
                                    FictitiousSkies.id("textures/environment/hilly_terrain_01.png"), 
                                    true, null, "Sergej Majboroda, Jarod Guest - Hilly Terrain 01 (Pure Sky)",
                                    0x6e98ad, 0xdcd8c6
                                ));
                                output.accept(BRollItem.createBRollStack(
                                    FictitiousSkies.id("textures/environment/overcast_soil.png"), 
                                    true, null, "Sergej Majboroda, Jarod Guest - Overcast Soil (Pure Sky)",
                                    0x9d9fa1, 0xd5d4d4
                                ));
                                output.accept(BRollItem.createBRollStack(
                                    FictitiousSkies.id("textures/environment/sunflowers.png"), 
                                    true, null, "Sergej Majboroda, Jarod Guest - Sunflowers (Pure Sky)",
                                    0x728999, 0xe0ddd5
                                ));
                                output.accept(BRollItem.createBRollStack(
                                    FictitiousSkies.id("textures/environment/mud_road.png"), 
                                    true, null, "Sergey Rudavin, Jarod Guest - Mud Road (Pure Sky)",
                                    0x7a7b81, 0xb8b8bd
                                ));
                                output.accept(BRollItem.createBRollStack(
                                    FictitiousSkies.id("textures/environment/aristea_wreck.png"), 
                                    true, null, "Greg Zaal, Jarod Guest - Aristea Wreck (Pure Sky)",
                                    0x578ab7, 0xdedfe0
                                ));
                                output.accept(BRollItem.createBRollStack(
                                    FictitiousSkies.id("textures/environment/belfast_sunset.png"), 
                                    true, null, "Dimitrios Savva, Greg Zaal, Jarod Guest - Belfast Sunset (Pure Sky)",
                                    0x898eaa, 0xf8f0d8
                                ));
                                output.accept(BRollItem.createBRollStack(
                                    FictitiousSkies.id("textures/environment/kloofendal_misty_morning.png"), 
                                    true, null, "Greg Zaal - Kloofendal Misty Morning (Pure Sky)",
                                    0x9aa1ac, 0xd1d4d8
                                ));
                                ModItems.BLOCK_ITEMS.forEach(item -> output.accept(item.get()));
                            }).build());

}
