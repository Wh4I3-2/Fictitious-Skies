package com.wh4i3.fictitiousskies.init;

import com.wh4i3.fictitiousskies.FictitiousSkies;
import com.wh4i3.fictitiousskies.item.SkyDiskItem;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FictitiousSkies.MODID);
    public static final List<DeferredItem<BlockItem>> BLOCK_ITEMS = new ArrayList<>();

    public static final DeferredItem<SkyDiskItem> SKY_DISK = ITEMS.registerItem(
        "sky_disk",
        SkyDiskItem::new,
        new Item.Properties()
            .setId(itemId("sky_disk"))
            .rarity(Rarity.UNCOMMON)
            .useCooldown(0.4F)
            .stacksTo(1)
    );
    public static final DeferredItem<SkyDiskItem> EMPTY_SKY_DISK = ITEMS.registerItem(
        "empty_sky_disk", 
        SkyDiskItem::new,
        new Item.Properties()
            .setId(itemId("empty_sky_disk"))
            .rarity(Rarity.COMMON)
            .useCooldown(0.4F)
            .stacksTo(64)
    );

    public static <T extends Block> DeferredItem<BlockItem> registerSimpleBlockItem(String id, DeferredBlock<T> block, Rarity rarity){
        DeferredItem<BlockItem> simpleBlockItem = ITEMS.registerSimpleBlockItem(id, block, new Item.Properties().rarity(rarity));
        BLOCK_ITEMS.add(simpleBlockItem);
        return simpleBlockItem;
    }

    public static <T extends Block> DeferredItem<BlockItem> registerSimpleBlockItem(String id, DeferredBlock<T> block){
        return registerSimpleBlockItem(id, block, Rarity.COMMON);
    }

    private static ResourceKey<Item> itemId(String name) {
        return ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FictitiousSkies.MODID, name));
    }
}