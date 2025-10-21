package com.wh4i3.fictitiousskies.client.render;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.wh4i3.fictitiousskies.FictitiousSkies;
import com.wh4i3.fictitiousskies.block.SkyboxBlock;
import com.wh4i3.fictitiousskies.block.blockentity.SkyboxBlockEntity;
import com.wh4i3.fictitiousskies.client.ModShaders;
import com.wh4i3.fictitiousskies.init.ModDataComponentType;
import com.wh4i3.fictitiousskies.init.ModDataComponentType.Skybox;
import com.wh4i3.fictitiousskies.init.ModDataComponentType.SkyboxFallback;

import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.BlockLightEngine;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.model.data.ModelData;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class SkyboxBlockRenderer<T extends SkyboxBlockEntity> implements BlockEntityRenderer<T> {
	private final BlockEntityRendererProvider.Context context;

	public SkyboxBlockRenderer(BlockEntityRendererProvider.Context context) {
		this.context = context;
	}

	public void render(@Nonnull T blockEntity, float partialTick, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int p_112654_, int p_112655_) {
		boolean shouldFallback = false;

		try {
			if (net.irisshaders.iris.api.v0.IrisApi.getInstance().isShaderPackInUse()) {
				shouldFallback = true;
			}
		} catch (NoClassDefFoundError ignored) {}


		if (blockEntity.isRemoved()) return;
		Level level = blockEntity.getLevel();
		if (level == null) return;

		Skybox skybox = blockEntity.getSkybox();
		if (skybox == null) return;

		if (skybox.skyboxLocation() == null) return;
		if (skybox.skyboxLocation() == Skybox.EMPTY.skyboxLocation()) return;
		if (!ResourceLocation.isValidNamespace(skybox.skyboxLocation().getNamespace())) return;
		if (!ResourceLocation.isValidPath(skybox.skyboxLocation().getPath())) return;
		if (!blockEntity.getBlockState().getValue(SkyboxBlock.HAS_SKY)) return;

		Pose pose = poseStack.last();

		if (skybox.fallback().isPresent()) {
			if (skybox.fallback().get().forceFallback().orElse(false)) {
				shouldFallback = true;
			}
		}

		if (shouldFallback && skybox.fallback().isPresent()) {
			SkyboxFallback fallback = skybox.fallback().get();

			switch (fallback.type()) {
				case SkyboxFallback.SkyboxFallbackType.COLOR: {
					int fallbackColor = fallback.color().orElse(0);
					this.renderCube(blockEntity, pose, buffer.getBuffer(SkyGeneratorRenderType.FALLBACK), fallbackColor + 0xFF_000000);
					break;
				}
				case SkyboxFallback.SkyboxFallbackType.TEXTURE: {
					ResourceLocation texture = fallback.texture().orElse(ResourceLocation.withDefaultNamespace("block/stone"));
					this.renderCube(blockEntity, pose, buffer.getBuffer(RenderType.guiTextured(texture)), 0xFF_FFFFFF);
					break;
				}
				case SkyboxFallback.SkyboxFallbackType.BLOCK: {
					BlockState blockState = fallback.block().orElse(Blocks.STONE.defaultBlockState());
					BakedModel model = context.getBlockRenderDispatcher().getBlockModel(blockState);
					context.getBlockRenderDispatcher().getModelRenderer().tesselateWithoutAO(
							blockEntity.getLevel(),
							model,
							blockState,
							blockEntity.getBlockPos(),
							poseStack,
							buffer.getBuffer(RenderType.CUTOUT),
							true,
							RandomSource.create(),
							blockState.getSeed(blockEntity.getBlockPos()),
							0,
							ModelData.EMPTY, 
							RenderType.CUTOUT
							/*BlockAndTintGetter level,
							BakedModel model,
							BlockState state,
							BlockPos pos,
							PoseStack poseStack,
							VertexConsumer consumer,
							boolean checkSides,
							RandomSource random,
							long seed,
							int packedOverlay,
							ModelData modelData,
							RenderType renderType
					*/);
					break;
				}
			}

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

				float fovModifier = Mth.lerp(partialTick, Minecraft.getInstance().gameRenderer.oldFovModifier, Minecraft.getInstance().gameRenderer.fovModifier);

				float fovVal = ClientHooks.getFieldOfView(
						Minecraft.getInstance().gameRenderer,
						Minecraft.getInstance().gameRenderer.getMainCamera(),
						partialTick,
						Minecraft.getInstance().options.fov().get(),
						true
				) * fovModifier;

				Uniform fov = shader.getUniform("FOV");
				if (fov != null) {
					fov.set(
							fovVal
					);
				}

				shader.apply();
			}
		}

		this.renderCube(blockEntity, pose, buffer.getBuffer(SkyGeneratorRenderType.skybox(skybox.skyboxLocation(), skybox.blur())), 0xFF_FFFFFF);
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
