package com.wh4i3.fictitiousskies.item;

import java.util.List;

import javax.annotation.Nonnull;


import com.wh4i3.fictitiousskies.FictitiousSkies;
import com.wh4i3.fictitiousskies.block.SkyboxBlock;
import com.wh4i3.fictitiousskies.block.blockentity.SkyboxBlockEntity;
import com.wh4i3.fictitiousskies.init.ModDataComponentType;
import com.wh4i3.fictitiousskies.init.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class SkyDiskItem extends Item {
    public SkyDiskItem(Properties properties) {
        super(properties);
    }

    public static ItemStack createSkyDiskItem(ResourceLocation skyboxLocation, boolean blur, int fallbackColor, ResourceLocation modelLocation, String credit) {
        return createSkyDiskItem(skyboxLocation, blur, fallbackColor, modelLocation, credit, 0x7492bf, 0x82ddee);
    }

    public static ItemStack createSkyDiskItem(ResourceLocation skyboxLocation, boolean blur, int fallbackColor, ResourceLocation modelLocation, String credit, int color) {
        return createSkyDiskItem(skyboxLocation, blur, fallbackColor, modelLocation, credit, color, color);
    }

    public static ItemStack createSkyDiskItem(ResourceLocation skyboxLocation, boolean blur, int fallbackColor, ResourceLocation modelLocation, String credit, int color0, int color1) {
        ItemStack stack = new ItemStack(ModItems.SKY_DISK.get());
        stack.set(ModDataComponentType.SKYBOX, new ModDataComponentType.Skybox(skyboxLocation, blur, fallbackColor));
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(), List.of(), List.of(color0, color1)));
        if (modelLocation == null) {
            modelLocation = FictitiousSkies.id("sky_disk");
        }
        stack.set(DataComponents.ITEM_MODEL, modelLocation);
        stack.set(ModDataComponentType.CREDIT, credit);
        return stack;
    }

    public static ItemStack empty() {
        return createSkyDiskItem(null, false, 0xFF_FFFFFF, null, null);
    }

    public static boolean isEmpty(ItemStack itemStack) {
        return itemStack.getItem() == ModItems.EMPTY_SKY_DISK.get() || !itemStack.has(ModDataComponentType.SKYBOX.get());
    }

    boolean usedOnBlock = false;

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        if (usedOnBlock) {
            usedOnBlock = false;
            return InteractionResult.SUCCESS;
        }
        return super.use(level, player, interactionHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState blockState = level.getBlockState(pos);
        Block block = blockState.getBlock();

        if (block instanceof SkyboxBlock) {
            if (isEmpty(context.getItemInHand())) {
                ModDataComponentType.Skybox skybox = ModDataComponentType.Skybox.EMPTY;
                BlockEntity targetEntity = level.getBlockEntity(pos);

                if (targetEntity instanceof SkyboxBlockEntity skyboxBlockEntity) {
                    if (skyboxBlockEntity.getSkyboxLocation() == skybox.skyboxLocation()) {
                        return InteractionResult.TRY_WITH_EMPTY_HAND;
                    }

                    skyboxBlockEntity.setSkyboxLocation(skybox.skyboxLocation());
                    skyboxBlockEntity.setBlur(skybox.blur());
                    skyboxBlockEntity.setFallbackColor(skybox.fallbackColor());

                    targetEntity.setChanged();
                    level.gameEvent(GameEvent.BLOCK_CHANGE, targetEntity.getBlockPos(), GameEvent.Context.of(targetEntity.getBlockState()));
                    level.scheduleTick(pos, targetEntity.getBlockState().getBlock(), 1);

                    blockState.setValue(SkyboxBlock.HAS_SKY, !blockState.getValue(SkyboxBlock.HAS_SKY));
                }

                usedOnBlock = true;
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            }

            ModDataComponentType.Skybox skybox = context.getItemInHand().get(ModDataComponentType.SKYBOX.get());
            BlockEntity targetEntity = level.getBlockEntity(pos);

            if (targetEntity instanceof SkyboxBlockEntity skyboxBlockEntity) {
                if (skybox != null) {
                    if (skyboxBlockEntity.getSkyboxLocation() == skybox.skyboxLocation()) {
                        return InteractionResult.TRY_WITH_EMPTY_HAND;
                    }
                    skyboxBlockEntity.setSkyboxLocation(skybox.skyboxLocation());
                    skyboxBlockEntity.setBlur(skybox.blur());
                    skyboxBlockEntity.setFallbackColor(skybox.fallbackColor());
                } else {
                    return InteractionResult.TRY_WITH_EMPTY_HAND;
                }

                targetEntity.setChanged();
                level.gameEvent(GameEvent.BLOCK_CHANGE, targetEntity.getBlockPos(), GameEvent.Context.of(targetEntity.getBlockState()));
                level.scheduleTick(pos, targetEntity.getBlockState().getBlock(), 1);

                blockState.setValue(SkyboxBlock.HAS_SKY, !blockState.getValue(SkyboxBlock.HAS_SKY));
            }
            usedOnBlock = true;
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        return super.useOn(context);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Item.TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        if (!stack.has(ModDataComponentType.CREDIT)) return;
        Component text = Component.literal(stack.get(ModDataComponentType.CREDIT)).withColor(0xAAAAAA);
        tooltipComponents.add(text);
    }
}
