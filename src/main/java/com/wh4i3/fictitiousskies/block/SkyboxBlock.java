package com.wh4i3.fictitiousskies.block;

import com.mojang.serialization.MapCodec;
import com.wh4i3.fictitiousskies.FictitiousSkies;
import com.wh4i3.fictitiousskies.block.blockentity.SkyboxBlockEntity;
import com.wh4i3.fictitiousskies.init.ModDataComponentType.Skybox;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.ToIntFunction;

public class SkyboxBlock extends BaseEntityBlock {
	public static final MapCodec<SkyboxBlock> CODEC = simpleCodec(SkyboxBlock::new);
	public static final BooleanProperty HAS_SKY = BooleanProperty.create("has_sky");
	public static final ToIntFunction<BlockState> LIGHT_EMISSION = (state) -> state.getValue(HAS_SKY) ? 15 : 0;

	public @Nonnull MapCodec<SkyboxBlock> codec() {
		return CODEC;
	}

	public SkyboxBlock(BlockBehaviour.Properties properties) {
		super(properties);

		this.registerDefaultState(stateDefinition.any()
				.setValue(HAS_SKY, false)
		);
	}

	@Override
	protected void tick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
		super.tick(state, level, pos, random);

		BlockEntity entity = level.getBlockEntity(pos);
		if (entity == null) return;

		SkyboxBlockEntity skyboxEntity = (SkyboxBlockEntity)entity;

		updateSky(skyboxEntity.getSkybox(), state, level, pos);

		boolean empty = true;
		if (skyboxEntity.getSkybox() != null) {
			empty = skyboxEntity.getSkybox().skyboxLocation() == Skybox.EMPTY.skyboxLocation();
		}

		level.setBlockAndUpdate(pos, state.setValue(HAS_SKY, !empty));
	}

	@Override
	protected @Nonnull RenderShape getRenderShape(@Nonnull BlockState state) {
		return state.getValue(HAS_SKY) ? RenderShape.INVISIBLE : RenderShape.MODEL;
	}

	@Nullable
	public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
		return new SkyboxBlockEntity(pos, state);
	}

	public void updateSky(Skybox skybox, BlockState state, Level level, BlockPos pos) {
		BlockPos[] positions = {pos.west(), pos.north(), pos.east(), pos.south(), pos.above(), pos.below()};
		for (BlockPos checkedPos : positions) {
			BlockEntity checkedEntity = level.getBlockEntity(checkedPos);
			if (level.getBlockState(checkedPos).getBlock() != state.getBlock()) continue;
			if (checkedEntity == null) continue;
			if (!(checkedEntity instanceof SkyboxBlockEntity)) continue;
			if (skybox.skyboxLocation() == ((SkyboxBlockEntity)checkedEntity).getSkybox().skyboxLocation()) continue;

			((SkyboxBlockEntity) checkedEntity).setSkybox(skybox);
			BlockState checkedState = level.getBlockState(checkedPos);
			level.setBlockAndUpdate(checkedPos, checkedState.setValue(HAS_SKY, !checkedState.getValue(HAS_SKY)));
			level.scheduleTick(checkedPos, checkedEntity.getBlockState().getBlock(), 1);
			level.gameEvent(GameEvent.BLOCK_CHANGE, checkedPos, GameEvent.Context.of(checkedEntity.getBlockState()));

			if (level instanceof ServerLevel serverlevel) {
				serverlevel.sendParticles(ParticleTypes.WAX_OFF, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, 0.5F, 0.5F, 0.5F, 0.1F);
			}
		}
	}
	
	@Override
	protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HAS_SKY);
	}
}
