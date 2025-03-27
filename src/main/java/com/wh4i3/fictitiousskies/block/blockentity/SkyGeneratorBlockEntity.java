package com.wh4i3.fictitiousskies.block.blockentity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.wh4i3.fictitiousskies.init.ModBlockEntities;
import com.wh4i3.fictitiousskies.init.ModDataComponentType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.ticks.ContainerSingleItem;


public class SkyGeneratorBlockEntity extends BlockEntity implements RandomizableContainer, ContainerSingleItem.BlockContainerSingleItem {
	private ItemStack item;
	@Nullable
	protected ResourceKey<LootTable> lootTable;
	protected long lootTableSeed;

	public ModDataComponentType.Skybox getSkybox() {
		if (!item.has(ModDataComponentType.SKYBOX.get())) {
			return ModDataComponentType.Skybox.EMPTY;
		}
		return item.get(ModDataComponentType.SKYBOX.get());
	}

	public SkyGeneratorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.SKY_GENERATOR_BLOCK_ENTITY.get(), pos, state);
		this.item = ItemStack.EMPTY;
	}

	@Override
	protected void saveAdditional(@Nonnull CompoundTag p_272957_, @Nonnull HolderLookup.Provider p_323719_) {
	   	super.saveAdditional(p_272957_, p_323719_);
	   	if (!this.trySaveLootTable(p_272957_) && !this.item.isEmpty()) {
	    	p_272957_.put("item", this.item.save(p_323719_));
	   	}
	}

	@Override
	protected void loadAdditional(@Nonnull CompoundTag p_338486_, @Nonnull HolderLookup.Provider p_338310_) {
      	super.loadAdditional(p_338486_, p_338310_);
      	if (!this.tryLoadLootTable(p_338486_)) {
         	if (p_338486_.contains("item", 10)) {
            	this.item = (ItemStack)ItemStack.parse(p_338310_, p_338486_.getCompound("item")).orElse(ItemStack.EMPTY);
         	} else {
            	this.item = ItemStack.EMPTY;
        	}
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

	@Nullable
	public ResourceKey<LootTable> getLootTable() {
	   	return this.lootTable;
	}

	public void setLootTable(@Nullable ResourceKey<LootTable> p_336080_) {
	   	this.lootTable = p_336080_;
	}

	public long getLootTableSeed() {
	   	return this.lootTableSeed;
	}

	public void setLootTableSeed(long p_309580_) {
		this.lootTableSeed = p_309580_;
	}

	protected void collectImplicitComponents(@Nonnull DataComponentMap.Builder p_338608_) {
		super.collectImplicitComponents(p_338608_);
		p_338608_.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(this.item)));
	}

	protected void applyImplicitComponents(@Nonnull BlockEntity.DataComponentInput p_338521_) {
	   	super.applyImplicitComponents(p_338521_);
	   	this.item = ((ItemContainerContents)p_338521_.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)).copyOne();
	}

	public ItemStack getTheItem() {
      	this.unpackLootTable(null);
      	return this.item;
	}

	@Override
	public void setTheItem(@Nonnull ItemStack item) {
		this.unpackLootTable(null);
		this.item = item;
	}

	@Override
	public BlockEntity getContainerBlockEntity() {
		return this;
	}
}