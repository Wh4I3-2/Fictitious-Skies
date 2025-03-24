package com.wh4i3.fictitiousskies.block;

import com.mojang.serialization.MapCodec;
import com.wh4i3.fictitiousskies.FictitiousSkies;
import com.wh4i3.fictitiousskies.block.blockentity.SkyboxBlockEntity;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SkyboxBlock extends BaseEntityBlock {

	public enum Sky implements StringRepresentable {
		CLOUDS("clouds", FictitiousSkies.id("textures/environment/skybox.png"), true),
		SATARA_NIGHT_NO_LAMPS("satara_night_no_lamps", FictitiousSkies.id("textures/environment/satara_night_no_lamps.png"), true),
		FURRY_CLOUDS("furry_clouds", FictitiousSkies.id("textures/environment/furry_clouds.png"), true),
		WILDFLOWER_FIELD("wildflower_field", FictitiousSkies.id("textures/environment/wildflower_field.png"), true),
		STARMAP_2020("starmap_2020", FictitiousSkies.id("textures/environment/starmap_2020.png"), true),
		  FISH("fish",   FictitiousSkies.id("textures/environment/fish.png"),   false),
		RANDOM("random", FictitiousSkies.id("textures/environment/random.png"), false),
		CATDOG("catdog", FictitiousSkies.id("textures/environment/catdog.png"), false),
		 PIZZA("pizza",  FictitiousSkies.id("textures/environment/pizza.png"),  false),
		 KITTY("kitty",  FictitiousSkies.id("textures/environment/kitty.png"),  false),
		BUCEES("bucees", FictitiousSkies.id("textures/environment/buc_ees.png"),false);

		public static final MapCodec<ClickEvent.Action> UNSAFE_CODEC = StringRepresentable.fromEnum(ClickEvent.Action::values).fieldOf("sky");
		public static final MapCodec<ClickEvent.Action> CODEC = UNSAFE_CODEC.validate(ClickEvent.Action::filterForSerialization);
		private final String name;
		private final ResourceLocation skyLocation;
		private final boolean blur;

		Sky(String name, ResourceLocation skyLocation, boolean blur) {
			this.name = name;
			this.skyLocation = skyLocation;
			this.blur = blur;
		}

		public @NotNull String getSerializedName() {
			return this.name;
		}

		public @NotNull ResourceLocation getSkyLocation() {
			return this.skyLocation;
		}

		public @NotNull boolean getBlur() {
			return this.blur;
		}
	}

	public static final MapCodec<SkyboxBlock> CODEC = simpleCodec(SkyboxBlock::new);
	public static final EnumProperty<Sky> SKY = EnumProperty.create("sky", Sky.class);
	private static int MAX_COOLDOWN = 10;
	private int cooldown = 0;

	public @NotNull MapCodec<SkyboxBlock> codec() {
		return CODEC;
	}

	public SkyboxBlock(BlockBehaviour.Properties properties) {
		super(properties);

		registerDefaultState(stateDefinition.any()
				.setValue(SKY, Sky.CLOUDS)
		);

		this.cooldown = 0;
	}

	public boolean shouldUpdateSky;

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		super.tick(state, level, pos, random);
		this.cooldown--;

		FictitiousSkies.LOGGER.debug(this.cooldown + "");
		
		if (shouldUpdateSky) {
			updateSky(state.getValue(SKY), state, level, pos);
			shouldUpdateSky = false;
		}
		if (this.cooldown > 0) {
			level.scheduleTick(pos, state.getBlock(), 1);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(SKY);
	}

	@Override
	protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Nullable
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new SkyboxBlockEntity(pos, state);
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!player.isShiftKeyDown() && stack.getItem() == Items.DEBUG_STICK) {
			level.scheduleTick(pos, state.getBlock(), 1);
		}
		return InteractionResult.TRY_WITH_EMPTY_HAND;
	}

	public void resetCooldown() {
		this.cooldown = MAX_COOLDOWN;
		this.shouldUpdateSky = true;
	}

	public int getCooldown() {
		return this.cooldown;
	}

	public void updateSky(Sky sky, BlockState state, Level level, BlockPos pos) {
		level.setBlockAndUpdate(pos, state.setValue(SKY, sky));

		Sky prev = Util.findPreviousInIterable(SKY.getPossibleValues(), sky);

		BlockPos[] positions = {pos.west(), pos.north(), pos.east(), pos.south(), pos.above(), pos.below()};
		for (BlockPos checkedPos : positions) {
			BlockState checkedState = level.getBlockState(checkedPos);
			if (!(checkedState.getBlock() instanceof SkyboxBlock)) {
				continue;
			}
			if (checkedState.getValue(SKY) != prev) {
				continue;
			}
			if (((SkyboxBlock) checkedState.getBlock()).getCooldown() > 0) {
				continue;
			}

			((SkyboxBlock)checkedState.getBlock()).resetCooldown();
			level.setBlockAndUpdate(checkedPos, checkedState.setValue(SKY, sky));
		}
	}
}
