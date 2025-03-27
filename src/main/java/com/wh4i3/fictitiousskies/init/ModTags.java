package com.wh4i3.fictitiousskies.init;

import com.wh4i3.fictitiousskies.FictitiousSkies;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> B_ROLL = createTag("b_roll");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(FictitiousSkies.id(name));
        }
    }
}
