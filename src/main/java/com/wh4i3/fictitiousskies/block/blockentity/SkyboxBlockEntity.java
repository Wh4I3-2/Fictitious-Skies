package com.wh4i3.fictitiousskies.block.blockentity;

import javax.annotation.Nonnull;

import com.mojang.serialization.DataResult;
import com.wh4i3.fictitiousskies.FictitiousSkies;
import com.wh4i3.fictitiousskies.init.ModBlockEntities;
import com.wh4i3.fictitiousskies.init.ModDataComponentType.Skybox;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


public class SkyboxBlockEntity extends BlockEntity {
	@Getter @Setter
	private Skybox skybox;

	public SkyboxBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.SKYBOX_BLOCK_ENTITY.get(), pos, state);

		this.skybox = Skybox.EMPTY;
	}

	@Override
	protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
	   	super.saveAdditional(tag, registries);
		DataResult<Tag> result = Skybox.CODEC.encode(this.skybox, NbtOps.INSTANCE, new CompoundTag());

		if (result.isSuccess()) {
			tag.put("skybox", result.getOrThrow());
		}
	}

	@Override
	protected void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
      	super.loadAdditional(tag, registries);
		CompoundTag skyboxTag = (CompoundTag) tag.get("skybox");
		DataResult<Skybox> result = Skybox.CODEC.parse(NbtOps.INSTANCE, skyboxTag);

		if (result.isSuccess()) {
			this.skybox = result.getOrThrow();
			return;
		}

		this.skybox = Skybox.EMPTY;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

	@Override
	public CompoundTag getUpdateTag(@Nonnull HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
	}

    @Override
    public void onDataPacket(@Nonnull Connection connection, @Nonnull ClientboundBlockEntityDataPacket packet, @Nonnull HolderLookup.Provider registries) {
        super.onDataPacket(connection, packet, registries);
    }

	@Override
    public void handleUpdateTag(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
    }

	public boolean shouldRenderFace(Direction face) {
		if (this.level == null)  {
			return false;
		}
		return Block.shouldRenderFace(this.level, this.worldPosition, this.getBlockState(), this.level.getBlockState(this.getBlockPos().relative(face)), face);
	}
}