package com.wh4i3.fictitiousskies.block.blockentity;

import com.wh4i3.fictitiousskies.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


public class SkyGeneratorBlockEntity extends BlockEntity {
	public SkyGeneratorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.SKY_GENERATOR_BLOCK_ENTITY.get(), pos, state);
	}
}