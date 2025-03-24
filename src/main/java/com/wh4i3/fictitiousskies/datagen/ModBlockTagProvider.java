package com.wh4i3.fictitiousskies.datagen;

import com.wh4i3.fictitiousskies.FictitiousSkies;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

/* Handles Data Generation for Block Tags of the Wotr mod */
public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, FictitiousSkies.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
    }

}
