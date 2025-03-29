package com.wh4i3.fictitiousskies.datagen;

import com.wh4i3.fictitiousskies.FictitiousSkies;
import com.wh4i3.fictitiousskies.init.ModBlocks;
import com.wh4i3.fictitiousskies.init.ModItems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/* Handles Data Generation for I18n of the locale 'en_us' of the Wotr mod */
public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output) {
        super(output, FictitiousSkies.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // Helpers are available for various common object types. Every helper has two variants: an add() variant
        // for the object itself, and an addTypeHere() variant that accepts a supplier for the object.
        // The different names for the supplier variants are required due to generic type erasure.
        // All following examples assume the existence of the values as suppliers of the needed type.
        // See https://docs.neoforged.net/docs/1.21.1/resources/client/i18n/ for translation of other types.

        // Adds a block translation.
        addBlock(ModBlocks.SKYBOX_BLOCK, "Sky Panel");
        addBlock(ModBlocks.ALT_SKYBOX_BLOCK, "Shined Sky Panel");
        addBlock(ModBlocks.INDESTRUCTIBLE_SKYBOX_BLOCK, "Indestructible Sky Panel");
        addBlock(ModBlocks.ALT_INDESTRUCTIBLE_SKYBOX_BLOCK, "Shined Indestructible Sky Panel");
        addBlock(ModBlocks.SKY_GENERATOR, "Sky Projector");

        addItem(ModItems.B_ROLL, "Sky Disk");
        addItem(ModItems.EMPTY_SKY_DISK, "Empty Sky Disk");

        // Adds a generic translation
        add("itemGroup." + FictitiousSkies.MODID, "Fictitious Skies");
    }

    private static @NotNull String getTranslationString(Block block) {
        String idString = BuiltInRegistries.BLOCK.getKey(block).getPath();
        StringBuilder sb = new StringBuilder();
        for (String word : idString.toLowerCase(Locale.ROOT).split("_")) {
            sb.append(word.substring(0, 1).toUpperCase(Locale.ROOT));
            sb.append(word.substring(1));
            sb.append(" ");
        }
        return sb.toString().trim();
    }
}