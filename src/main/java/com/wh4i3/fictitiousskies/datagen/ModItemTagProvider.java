package com.wh4i3.fictitiousskies.datagen;


import com.wh4i3.fictitiousskies.FictitiousSkies;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

/* Handles Data Generation for Block Tags of the Wotr mod */
public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                              CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags, FictitiousSkies.MODID);
    }

    @Override
    protected void addTags(@Nonnull HolderLookup.Provider provider) {
    }
}