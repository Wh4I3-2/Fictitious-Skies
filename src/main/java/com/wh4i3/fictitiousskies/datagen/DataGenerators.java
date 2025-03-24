package com.wh4i3.fictitiousskies.datagen;

import com.wh4i3.fictitiousskies.FictitiousSkies;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = FictitiousSkies.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        event.createProvider(ModLanguageProvider::new);
        event.createProvider(ModModelProvider::new);
        event.createProvider((output, lookupProvider) -> new LootTableProvider(
                output, Set.of(), List.of(new LootTableProvider.SubProviderEntry(ModBlockLootTableProvider::new, LootContextParamSets.BLOCK)), lookupProvider
        ));

        event.createProvider(ModRecipeProvider.Runner::new);

        ModBlockTagProvider modBlockTagProvider = event.createProvider(ModBlockTagProvider::new);

        event.createProvider((output, lookupProvider)
                -> new ModItemTagProvider(output, lookupProvider, modBlockTagProvider.contentsGetter()));
    }
}
