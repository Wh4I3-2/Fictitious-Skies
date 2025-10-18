package com.wh4i3.fictitiousskies.block.blockentity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.wh4i3.fictitiousskies.FictitiousSkies;
import com.wh4i3.fictitiousskies.block.SkyGeneratorBlock;
import com.wh4i3.fictitiousskies.init.ModBlockEntities;
import com.wh4i3.fictitiousskies.init.ModBlocks;
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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ContainerSingleItem;


public class SkyGeneratorBlockEntity extends BlockEntity implements ContainerSingleItem.BlockContainerSingleItem {
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
	   	if (!this.item.isEmpty()) {
	    	p_272957_.put("item", this.item.save(p_323719_));
	   	}
	}

	@Override
	protected void loadAdditional(@Nonnull CompoundTag p_338486_, @Nonnull HolderLookup.Provider p_338310_) {
      	super.loadAdditional(p_338486_, p_338310_);
        if (p_338486_.contains("item", 10)) {
        	this.item = (ItemStack)ItemStack.parse(p_338310_, p_338486_.getCompound("item")).orElse(ItemStack.EMPTY);
        } else {
        	this.item = ItemStack.EMPTY;
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

	protected void collectImplicitComponents(@Nonnull DataComponentMap.Builder p_338608_) {
		super.collectImplicitComponents(p_338608_);
		p_338608_.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(this.item)));
	}

	protected void applyImplicitComponents(@Nonnull BlockEntity.DataComponentInput p_338521_) {
	   	super.applyImplicitComponents(p_338521_);
	   	this.item = ((ItemContainerContents)p_338521_.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)).copyOne();
	}

	public ItemStack getTheItem() {
      	return this.item;
	}

	@Override
	public void setTheItem(@Nonnull ItemStack item) {
		this.item = item;
        boolean flag = !this.item.isEmpty();
		this.notifyItemChanged(flag);
	}

    @Override
    public ItemStack splitTheItem(int p_304604_) {
        ItemStack itemstack = this.item;
        this.setTheItem(ItemStack.EMPTY);
        return itemstack;
    }

    private void notifyItemChanged(boolean hasItem) {
        Level level = this.level;
        if (level == null) return;
        if (level.getBlockState(this.getBlockPos()) == this.getBlockState()) {
            level.setBlock(this.getBlockPos(), this.getBlockState().setValue(SkyGeneratorBlock.HAS_ITEM, Boolean.valueOf(hasItem)), 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(this.getBlockState()));
        }
    }

	@Override
	public BlockEntity getContainerBlockEntity() {
		return this;
	}

    public void popOutTheItem() {
        Level level = this.level;
        if (level == null) return;
        if (!level.isClientSide()) {
            BlockPos blockpos = this.getBlockPos();
            ItemStack itemstack = this.getTheItem();
            if (!itemstack.isEmpty()) {
				this.removeTheItem();
                Vec3 vec3 = Vec3.atLowerCornerWithOffset(blockpos, 0.5, 1.01, 0.5).offsetRandom(level.random, 0.7F);
                ItemStack itemstack1 = itemstack.copy();
                ItemEntity itementity = new ItemEntity(level, vec3.x(), vec3.y(), vec3.z(), itemstack1);
                itementity.setDefaultPickUpDelay();
                level.addFreshEntity(itementity);
            }
        }
    }
    public static InteractionResult tryInsert(Level level, BlockPos pos, ItemStack stack, Player player) {
        ModDataComponentType.Skybox skybox = stack.get(ModDataComponentType.SKYBOX);
        if (skybox == null) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        } else {
            BlockState blockstate = level.getBlockState(pos);
            if (blockstate.is(ModBlocks.SKY_GENERATOR) && !blockstate.getValue(SkyGeneratorBlock.HAS_ITEM)) {
                if (!level.isClientSide) {
                    ItemStack itemstack = stack.consumeAndReturn(1, player);
                    if (level.getBlockEntity(pos) instanceof SkyGeneratorBlockEntity skyGeneratorBlockEntity) {
                        skyGeneratorBlockEntity.setTheItem(itemstack);
                        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, blockstate));
                    }
                }

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            }
        }
	}
}