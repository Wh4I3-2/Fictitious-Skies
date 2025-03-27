package com.wh4i3.fictitiousskies.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.wh4i3.fictitiousskies.block.blockentity.SkyGeneratorBlockEntity;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class SkyGeneratorRenderer<T extends SkyGeneratorBlockEntity> implements BlockEntityRenderer<T> {
	private final BlockEntityRendererProvider.Context context;

	public SkyGeneratorRenderer(BlockEntityRendererProvider.Context context) {
		this.context = context;
	}

	public void render(@Nonnull T blockEntity, float partialTick, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int packedOverlay, int packedLight) {
		Level level = blockEntity.getLevel();
		if (level == null) return;
		BlockPos pos = blockEntity.getBlockPos();

		double relativeGameTime = level.getGameTime() + partialTick;
		double offset = Math.sin(relativeGameTime / 10.0) / 8.0;
		double rotation = relativeGameTime / 20.0 * 40.0;
		float scale = 0.7f;
		
		poseStack.pushPose();
		poseStack.translate(0.5, 0.5 + offset, 0.5);
		poseStack.scale(scale, scale, scale);
		poseStack.mulPose(Axis.YP.rotationDegrees((float) rotation));

		this.context.getItemRenderer().renderStatic(
			blockEntity.getTheItem(),
			ItemDisplayContext.FIXED,
			LightTexture.pack(
				15,
				level.getBrightness(LightLayer.SKY, pos)
			),
			OverlayTexture.NO_OVERLAY,
			poseStack,
			buffer,
			level,
			0
		);
		poseStack.popPose();
	}
}
