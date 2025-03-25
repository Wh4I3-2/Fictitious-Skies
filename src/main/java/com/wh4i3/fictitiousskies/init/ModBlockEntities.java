package com.wh4i3.fictitiousskies.init;

import com.wh4i3.fictitiousskies.FictitiousSkies;
import com.wh4i3.fictitiousskies.block.blockentity.SkyGeneratorBlockEntity;
import com.wh4i3.fictitiousskies.block.blockentity.SkyboxBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, FictitiousSkies.MODID);

    public static final Supplier<BlockEntityType<SkyboxBlockEntity>> SKYBOX_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "skybox_block_entity",
            // The block entity type.
            () -> new BlockEntityType<>(
                    SkyboxBlockEntity::new,
                    Set.of(
                            ModBlocks.SKYBOX_BLOCK.get()
                    )
            )
    );
    public static final Supplier<BlockEntityType<SkyGeneratorBlockEntity>> SKY_GENERATOR_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "sky_generator_block_entity",
            // The block entity type.
            () -> new BlockEntityType<>(
                    SkyGeneratorBlockEntity::new,
                    Set.of(
                            ModBlocks.SKY_GENERATOR.get()
                    )
            )
    );
}
