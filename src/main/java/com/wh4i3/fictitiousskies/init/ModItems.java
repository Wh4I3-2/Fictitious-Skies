package com.wh4i3.fictitiousskies.init;

import com.wh4i3.fictitiousskies.FictitiousSkies;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FictitiousSkies.MODID);
    public static final List<DeferredItem<BlockItem>> BLOCK_ITEMS = new ArrayList<>();

    public static <T extends Block> DeferredItem<BlockItem> registerSimpleBlockItem(String id, DeferredBlock<T> block){
        DeferredItem<BlockItem> simpleBlockItem = ITEMS.registerSimpleBlockItem(id, block);
        BLOCK_ITEMS.add(simpleBlockItem);
        return simpleBlockItem;
    }

}