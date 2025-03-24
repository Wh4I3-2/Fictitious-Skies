package com.wh4i3.fictitiousskies.init;

import com.wh4i3.fictitiousskies.FictitiousSkies;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static class Blocks {

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(FictitiousSkies.id(name));
        }
    }

    public static class Items {
        public static final TagKey<Item> DEV_TOOLS = createTag("dev_tools");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(FictitiousSkies.id(name));
        }
    }
}
