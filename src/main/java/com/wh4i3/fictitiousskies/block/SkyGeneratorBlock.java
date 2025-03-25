package com.wh4i3.fictitiousskies.block;

import com.mojang.serialization.MapCodec;
import com.wh4i3.fictitiousskies.block.blockentity.SkyGeneratorBlockEntity;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class SkyGeneratorBlock extends BaseEntityBlock {
	public static final MapCodec<SkyGeneratorBlock> CODEC = simpleCodec(SkyGeneratorBlock::new);

	public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public SkyGeneratorBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH).setValue(POWERED, false));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED);
	}

	protected BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (state.getValue(POWERED) && !level.hasNeighborSignal(pos)) {
			level.setBlock(pos, state.setValue(POWERED, false), 2);
		} else if (!state.getValue(POWERED) && level.hasNeighborSignal(pos)) {
			level.setBlock(pos, state.setValue(POWERED, true), 2);

			BlockPos targetPos = pos.relative(state.getValue(FACING).getOpposite());
			BlockState targetState = level.getBlockState(targetPos);
			if (targetState.getBlock() instanceof SkyboxBlock) {
				level.scheduleTick(targetPos, targetState.getBlock(), 1);
				level.setBlockAndUpdate(targetPos, targetState.setValue(SkyboxBlock.SKY, Util.findNextInIterable(Arrays.stream(SkyboxBlock.Sky.values()).toList(), targetState.getValue(SkyboxBlock.SKY))));
			}
		}
	}

	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation p_364297_, boolean p_55671_) {
		if (!level.isClientSide) {
			level.scheduleTick(pos, this, 1);
		}
	}


	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite().getOpposite());
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SkyGeneratorBlockEntity(pos, state);
	}
}
