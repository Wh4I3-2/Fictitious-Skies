package com.wh4i3.fictitiousskies.block;

import com.mojang.serialization.MapCodec;
import com.wh4i3.fictitiousskies.block.blockentity.SkyboxBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SkyboxBlock extends BaseEntityBlock {
	public static final MapCodec<SkyboxBlock> CODEC = simpleCodec(SkyboxBlock::new);

	public @Nonnull MapCodec<SkyboxBlock> codec() {
		return CODEC;
	}

	public SkyboxBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	protected void tick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
		super.tick(state, level, pos, random);

		BlockEntity entity = level.getBlockEntity(pos);
		if (entity == null) return;

		updateSky(((SkyboxBlockEntity)entity).getSkyboxLocation(), ((SkyboxBlockEntity)entity).getBlur(), state, level, pos);
	}

	@Override
	protected @Nonnull RenderShape getRenderShape(@Nonnull BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Nullable
	public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
		return new SkyboxBlockEntity(pos, state);
	}

	@Override
	protected InteractionResult useItemOn(@Nonnull ItemStack stack, @Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
		if (!player.isShiftKeyDown() && stack.getItem() == Items.DEBUG_STICK) {
			level.scheduleTick(pos, state.getBlock(), 1);
		}
		return InteractionResult.TRY_WITH_EMPTY_HAND;
	}

	public void updateSky(ResourceLocation skyboxLocation, boolean blur, BlockState state, Level level, BlockPos pos) {
		BlockPos[] positions = {pos.west(), pos.north(), pos.east(), pos.south(), pos.above(), pos.below()};
		for (BlockPos checkedPos : positions) {
			BlockEntity checkedEntity = level.getBlockEntity(checkedPos);
			if (checkedEntity == null) continue;
			if (!(checkedEntity instanceof SkyboxBlockEntity)) continue;
			if (skyboxLocation == ((SkyboxBlockEntity)checkedEntity).getSkyboxLocation()) continue;

			((SkyboxBlockEntity) checkedEntity).setSkyboxLocation(skyboxLocation);
			((SkyboxBlockEntity) checkedEntity).setBlur(blur);
			level.scheduleTick(checkedPos, checkedEntity.getBlockState().getBlock(), 1);
		}
	}
}
