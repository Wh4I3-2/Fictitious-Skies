package com.wh4i3.fictitiousskies.datagen;

import com.wh4i3.fictitiousskies.FictitiousSkies;
import com.wh4i3.fictitiousskies.init.ModBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, FictitiousSkies.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
        blockModels.createTrivialCube(ModBlocks.SKYBOX_BLOCK.get());
        createObserverLikeBlock(blockModels, ModBlocks.SKY_GENERATOR.get());
    }

    public static void createObserverLikeBlock(BlockModelGenerators blockModels, Block observerLike) {
        ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(observerLike);
        ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(observerLike, "_on");
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(observerLike).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.POWERED, resourcelocation1, resourcelocation)).with(BlockModelGenerators.createFacingDispatch()));
    }
}
