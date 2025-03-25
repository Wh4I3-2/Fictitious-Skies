package com.wh4i3.fictitiousskies.init;

import com.wh4i3.fictitiousskies.FictitiousSkies;
import com.wh4i3.fictitiousskies.block.SkyGeneratorBlock;
import com.wh4i3.fictitiousskies.block.SkyboxBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FictitiousSkies.MODID);

    public static final DeferredBlock<SkyboxBlock> SKYBOX_BLOCK = registerBlock(
            "skybox_block",
            () -> new SkyboxBlock(BlockBehaviour.Properties.of()
                    .setId(blockId("skybox_block"))
                    .strength(1.5F)
                    .sound(SoundType.METAL)
            )
    );
    public static final DeferredBlock<SkyGeneratorBlock> SKY_GENERATOR = registerBlock(
            "sky_generator",
            () -> new SkyGeneratorBlock(BlockBehaviour.Properties.of()
                    .setId(blockId("sky_generator"))
                    .strength(1.5F)
                    .sound(SoundType.METAL)
            )
    );

    private static <T extends Block> DeferredBlock<T> registerBlock(String key, Supplier<T> sup) {
        DeferredBlock<T> register = BLOCKS.register(key, sup);
        ModItems.registerSimpleBlockItem(key, register);
        return register;
    }

    private static ResourceKey<Block> blockId(String name) {
        return ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(FictitiousSkies.MODID, name));
    }
}
