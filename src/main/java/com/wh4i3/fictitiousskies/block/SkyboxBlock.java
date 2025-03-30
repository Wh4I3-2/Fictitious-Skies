package com.wh4i3.fictitiousskies.block;

import com.mojang.serialization.MapCodec;
import com.wh4i3.fictitiousskies.block.blockentity.SkyboxBlockEntity;
import com.wh4i3.fictitiousskies.init.ModDataComponentType;
import com.wh4i3.fictitiousskies.init.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

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

		updateSky(((SkyboxBlockEntity)entity).getSkyboxLocation(), ((SkyboxBlockEntity)entity).getBlur(), state, level, pos);
		
		boolean empty = ((SkyboxBlockEntity)entity).getSkyboxLocation() == ModDataComponentType.Skybox.EMPTY.skyboxLocation();
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

	@Override
	protected InteractionResult useItemOn(@Nonnull ItemStack stack, @Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
		if (stack == ItemStack.EMPTY) return InteractionResult.TRY_WITH_EMPTY_HAND;
		if (stack.getItem() == ModItems.EMPTY_SKY_DISK.get()) {
			ModDataComponentType.Skybox skybox = ModDataComponentType.Skybox.EMPTY;
			BlockEntity targetEntity = level.getBlockEntity(pos);
			if (targetEntity instanceof SkyboxBlockEntity) {
				if (skybox != null) {
					((SkyboxBlockEntity)targetEntity).setSkyboxLocation(skybox.skyboxLocation());
					((SkyboxBlockEntity)targetEntity).setBlur(skybox.blur());;
				}
				targetEntity.setChanged();
				level.gameEvent(GameEvent.BLOCK_CHANGE, targetEntity.getBlockPos(), GameEvent.Context.of(targetEntity.getBlockState()));
				level.scheduleTick(pos, targetEntity.getBlockState().getBlock(), 1);
			}
			return InteractionResult.SUCCESS;
		}
		if (stack.has(ModDataComponentType.SKYBOX.get())) {
			ModDataComponentType.Skybox skybox = stack.get(ModDataComponentType.SKYBOX.get());
			BlockEntity targetEntity = level.getBlockEntity(pos);
			if (targetEntity instanceof SkyboxBlockEntity) {
				if (skybox != null) {
					((SkyboxBlockEntity)targetEntity).setSkyboxLocation(skybox.skyboxLocation());;
					((SkyboxBlockEntity)targetEntity).setBlur(skybox.blur());;
				}
				targetEntity.setChanged();
				level.gameEvent(GameEvent.BLOCK_CHANGE, targetEntity.getBlockPos(), GameEvent.Context.of(targetEntity.getBlockState()));
				level.scheduleTick(pos, targetEntity.getBlockState().getBlock(), 1);
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.TRY_WITH_EMPTY_HAND;
	}

	public void updateSky(ResourceLocation skyboxLocation, boolean blur, BlockState state, Level level, BlockPos pos) {
		BlockPos[] positions = {pos.west(), pos.north(), pos.east(), pos.south(), pos.above(), pos.below()};
		for (BlockPos checkedPos : positions) {
			BlockEntity checkedEntity = level.getBlockEntity(checkedPos);
			if (level.getBlockState(checkedPos).getBlock() != state.getBlock()) continue;
			if (checkedEntity == null) continue;
			if (!(checkedEntity instanceof SkyboxBlockEntity)) continue;
			if (skyboxLocation == ((SkyboxBlockEntity)checkedEntity).getSkyboxLocation()) continue;

			((SkyboxBlockEntity) checkedEntity).setSkyboxLocation(skyboxLocation);
			((SkyboxBlockEntity) checkedEntity).setBlur(blur);
			level.scheduleTick(checkedPos, checkedEntity.getBlockState().getBlock(), 1);
			level.gameEvent(GameEvent.BLOCK_CHANGE, checkedPos, GameEvent.Context.of(checkedEntity.getBlockState()));

			if (level instanceof ServerLevel serverlevel) {
				serverlevel.sendParticles(ParticleTypes.WAX_OFF, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 2, 0.5F, 0.5F, 0.5F, 0.1F);
			}
		}
	}
	
	@Override
	protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HAS_SKY);
	}
}
