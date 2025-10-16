package com.wh4i3.fictitiousskies.client.render;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.wh4i3.fictitiousskies.block.SkyboxBlock;
import com.wh4i3.fictitiousskies.block.blockentity.SkyboxBlockEntity;
import com.wh4i3.fictitiousskies.client.ModShaders;
import com.wh4i3.fictitiousskies.init.ModDataComponentType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class SkyboxBlockRenderer<T extends SkyboxBlockEntity> implements BlockEntityRenderer<T> {
	private final BlockEntityRendererProvider.Context context;

	public SkyboxBlockRenderer(BlockEntityRendererProvider.Context context) {
		this.context = context;
	}

	public void render(@Nonnull T blockEntity, float p_112651_, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int p_112654_, int p_112655_) {
		boolean shouldFallBack = false;

		try {
			if (net.irisshaders.iris.api.v0.IrisApi.getInstance().isShaderPackInUse()) {
				shouldFallBack = true;
			}
		} catch (NoClassDefFoundError ignored) {}


		if (blockEntity.isRemoved()) return;
		Level level = blockEntity.getLevel();
		if (level == null) return;
		if (blockEntity.getSkyboxLocation() == null) return;
		if (blockEntity.getSkyboxLocation() == ModDataComponentType.Skybox.EMPTY.skyboxLocation()) return;
		if (!ResourceLocation.isValidNamespace(blockEntity.getSkyboxLocation().getNamespace())) return;
		if (!ResourceLocation.isValidPath(blockEntity.getSkyboxLocation().getPath())) return;
		if (!blockEntity.getBlockState().getValue(SkyboxBlock.HAS_SKY)) return;

		Pose pose = poseStack.last();

		if (shouldFallBack) {
			int fallbackColor = blockEntity.getFallbackColor();
			this.renderCube(blockEntity, pose, buffer.getBuffer(SkyGeneratorRenderType.FALLBACK), fallbackColor + 0xFF_000000);
			return;
		} else {
			CompiledShaderProgram shader = RenderSystem.setShader(ModShaders.SKYBOX);
			if (shader != null) {
				Uniform screenSize = shader.getUniform("ScreenSize");
				if (screenSize != null) {
					screenSize.set((float)Minecraft.getInstance().getWindow().getWidth(), (float)Minecraft.getInstance().getWindow().getHeight());
				}

				Uniform view = shader.getUniform("View");
				if (view != null) {
					view.set(
							context.getBlockEntityRenderDispatcher().camera.getXRot(),
							context.getBlockEntityRenderDispatcher().camera.getYRot()
					);
				}

				shader.apply();
			}
		}

		ResourceLocation location = blockEntity.getSkyboxLocation();
		boolean blur = blockEntity.getBlur();

		this.renderCube(blockEntity, pose, buffer.getBuffer(SkyGeneratorRenderType.skybox(location, blur)), 0xFF_FFFFFF);
	}

	private void renderCube(T blockEntity, Pose pose, VertexConsumer consumer, int hexColor) {
		this.renderFace(blockEntity, pose, consumer, hexColor, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH);
		this.renderFace(blockEntity, pose, consumer, hexColor, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH);
		this.renderFace(blockEntity, pose, consumer, hexColor, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST);
		this.renderFace(blockEntity, pose, consumer, hexColor, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
		this.renderFace(blockEntity, pose, consumer, hexColor, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN);
		this.renderFace(blockEntity, pose, consumer, hexColor, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP);
	}

	private void renderFace(
			T blockEntity,
			Pose pose,

			VertexConsumer consumer,
			int hexColor,
			float x0,
			float x1,
			float y0,
			float y1,
			float z0,
			float z1,
			float z2,
			float z3,
			Direction direction
	) {
		if (pose == null) return;
		if (blockEntity == null) return;

		if (!blockEntity.shouldRenderFace(direction)) {
			return;
		}

		consumer.addVertex(pose.pose(), x0, y0, z0).setColor(hexColor).setUv(0.0f, 0.0f).setUv1(0, 0).setUv2(0, 0).setNormal(pose, direction.step());
		consumer.addVertex(pose.pose(), x1, y0, z1).setColor(hexColor).setUv(1.0f, 0.0f).setUv1(1, 0).setUv2(1, 0).setNormal(pose, direction.step());
		consumer.addVertex(pose.pose(), x1, y1, z2).setColor(hexColor).setUv(1.0f, 1.0f).setUv1(1, 1).setUv2(1, 1).setNormal(pose, direction.step());
		consumer.addVertex(pose.pose(), x0, y1, z3).setColor(hexColor).setUv(0.0f, 1.0f).setUv1(0, 1).setUv2(0, 1).setNormal(pose, direction.step());
	}
}
