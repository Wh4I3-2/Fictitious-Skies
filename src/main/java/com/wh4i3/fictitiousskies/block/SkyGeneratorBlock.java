package com.wh4i3.fictitiousskies.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import com.wh4i3.fictitiousskies.block.blockentity.SkyGeneratorBlockEntity;
import com.wh4i3.fictitiousskies.block.blockentity.SkyboxBlockEntity;

import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.ToIntFunction;

public class SkyGeneratorBlock extends BaseEntityBlock {
	public static final MapCodec<SkyGeneratorBlock> CODEC = simpleCodec(SkyGeneratorBlock::new);

	public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final BooleanProperty HAS_ITEM = BooleanProperty.create("has_item");
	public static final ToIntFunction<BlockState> LIGHT_EMISSION = (state) -> {
		if (state.getValue(POWERED)) return 12;
		return state.getValue(HAS_ITEM) ? 8 : 0;
	};

	public SkyGeneratorBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
			.setValue(FACING, Direction.SOUTH)
			.setValue(POWERED, false)
			.setValue(HAS_ITEM, false)
		);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}


    @Override
    protected InteractionResult useWithoutItem(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull BlockHitResult result) {
        if (state.getValue(HAS_ITEM) && level.getBlockEntity(pos) instanceof SkyGeneratorBlockEntity skyGeneratorBlockEntity) {
            skyGeneratorBlockEntity.popOutTheItem();
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    protected InteractionResult useItemOn(@Nonnull ItemStack stack, @Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos blockPos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult result) {
        if (state.getValue(HAS_ITEM)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        } else {
            ItemStack itemstack = player.getItemInHand(hand);
			if (itemstack == ItemStack.EMPTY) return InteractionResult.TRY_WITH_EMPTY_HAND;
            InteractionResult interactionresult = SkyGeneratorBlockEntity.tryInsert(level, blockPos, itemstack, player);
            return (InteractionResult)(!interactionresult.consumesAction() ? InteractionResult.TRY_WITH_EMPTY_HAND : interactionresult);
        }
    }

	protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, HAS_ITEM);
	}

	protected BlockState rotate(@Nonnull BlockState state, @Nonnull Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	protected BlockState mirror(@Nonnull BlockState state, @Nonnull Mirror mirror) {
		return this.rotate(state, mirror.getRotation(state.getValue(FACING)));
	}

	protected void tick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
		if (level.isClientSide) return;
		if (state.getValue(POWERED) && !level.hasNeighborSignal(pos)) {
			level.setBlock(pos, state.setValue(POWERED, false), 2);
		} else if (!state.getValue(POWERED) && level.hasNeighborSignal(pos)) {
			level.setBlock(pos, state.setValue(POWERED, true), 2);

			BlockPos targetPos = pos.relative(state.getValue(FACING).getOpposite());
			BlockEntity targetEntity = level.getBlockEntity(targetPos);
			if (targetEntity instanceof SkyboxBlockEntity) {
				SkyGeneratorBlockEntity entity = (SkyGeneratorBlockEntity)level.getBlockEntity(pos);
				if (entity != null) {
					((SkyboxBlockEntity)targetEntity).setSkyboxLocation(entity.getSkybox().skyboxLocation());;
					((SkyboxBlockEntity)targetEntity).setBlur(entity.getSkybox().blur());;
					targetEntity.setChanged();
					level.gameEvent(GameEvent.BLOCK_CHANGE, targetEntity.getBlockPos(), GameEvent.Context.of(targetEntity.getBlockState()));
					level.scheduleTick(targetPos, targetEntity.getBlockState().getBlock(), 1);
				}
			}
		}
	}

	protected void neighborChanged(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Block block, @Nullable Orientation p_364297_, boolean p_55671_) {
		if (!level.isClientSide) {
			level.scheduleTick(pos, this, 1);
		}
	}


	public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
		return new SkyGeneratorBlockEntity(pos, state);
	}
}
