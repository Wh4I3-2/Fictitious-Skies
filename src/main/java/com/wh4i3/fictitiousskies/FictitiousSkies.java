package com.wh4i3.fictitiousskies;

import com.mojang.logging.LogUtils;
import com.wh4i3.fictitiousskies.block.blockentity.SkyboxBlockRenderer;
import com.wh4i3.fictitiousskies.client.ModShaders;
import com.wh4i3.fictitiousskies.config.ClientConfig;
import com.wh4i3.fictitiousskies.init.ModBlockEntities;
import com.wh4i3.fictitiousskies.init.ModBlocks;
import com.wh4i3.fictitiousskies.init.ModCreativeTabs;
import com.wh4i3.fictitiousskies.init.ModItems;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(FictitiousSkies.MODID)
public class FictitiousSkies {
    public static final String MODID = "fictitious_skies";
    public static final Logger LOGGER = LogUtils.getLogger();

    public FictitiousSkies(IEventBus modEventBus, ModContainer modContainer) {
        // Register things
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (DimensionDelvers) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        //NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    /**
     * Helper method to get a {@code ResourceLocation} with our Mod Id and a passed in name
     *
     * @param name the name to create the {@code ResourceLocation} with
     * @return A {@code ResourceLocation} with the given name
     */
    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }

    /**
     * Helper method to get a {@code TagKey} with our Mod Id and a passed in name
     *
     * @param name the name to create the {@code TagKey} with
     * @return A {@code TagKey} with the given name
     */
    public static <T> TagKey<T> tagId(ResourceKey<? extends Registry<T>> registry, String name) {
        return TagKey.create(registry, id(name));
    }
    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.SKYBOX_BLOCK_ENTITY.get(), SkyboxBlockRenderer::new);
        }

        @SubscribeEvent
        public static void registerShaderPrograms(RegisterShadersEvent event) {
            event.registerShader(ModShaders.SKYBOX);
        }
    }
}
