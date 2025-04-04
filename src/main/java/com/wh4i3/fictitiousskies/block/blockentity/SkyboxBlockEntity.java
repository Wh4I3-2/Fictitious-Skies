package com.wh4i3.fictitiousskies.block.blockentity;

import javax.annotation.Nonnull;

import com.wh4i3.fictitiousskies.init.ModBlockEntities;
import com.wh4i3.fictitiousskies.init.ModDataComponentType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


public class SkyboxBlockEntity extends BlockEntity {
	private ResourceLocation skyboxLocation;
	public void setSkyboxLocation(@Nonnull ResourceLocation skyboxLocation) {
		this.skyboxLocation = skyboxLocation;
	}
	public ResourceLocation getSkyboxLocation() {
		return this.skyboxLocation;
	}
	private boolean blur;
	public void setBlur(boolean blur) {
		this.blur = blur;
	}
	public boolean getBlur() {
		return this.blur;
	}
	public ModDataComponentType.Skybox getSkybox() {
		return new ModDataComponentType.Skybox(this.skyboxLocation, this.blur);
	}

	public SkyboxBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.SKYBOX_BLOCK_ENTITY.get(), pos, state);

		this.skyboxLocation = ModDataComponentType.Skybox.EMPTY.skyboxLocation();
		this.blur = ModDataComponentType.Skybox.EMPTY.blur();
	}

	@Override
	protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
	   	super.saveAdditional(tag, registries);
		CompoundTag skyboxTag = new CompoundTag();
		skyboxTag.putString("skyboxLocation", this.skyboxLocation.toString());
		skyboxTag.putBoolean("blur", this.blur);
		tag.put("skybox", skyboxTag);
	}

	@Override
	protected void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
      	super.loadAdditional(tag, registries);
		CompoundTag skyboxTag = (CompoundTag) tag.get("skybox");
		if (skyboxTag != null) {
			this.skyboxLocation = ResourceLocation.parse(skyboxTag.getString("skyboxLocation"));
			this.blur = skyboxTag.getBoolean("blur");
		}
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
}