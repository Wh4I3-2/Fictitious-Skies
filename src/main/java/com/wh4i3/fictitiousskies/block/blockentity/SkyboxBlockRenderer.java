package com.wh4i3.fictitiousskies.block.blockentity;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.wh4i3.fictitiousskies.FictitiousSkies;
import com.wh4i3.fictitiousskies.block.SkyboxBlock;
import com.wh4i3.fictitiousskies.client.ModShaders;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
public class SkyboxBlockRenderer<T extends SkyboxBlockEntity> implements BlockEntityRenderer<T> {
	private static final RenderStateShard.ShaderStateShard SKYBOX_SHADER = new RenderStateShard.ShaderStateShard(ModShaders.SKYBOX);

	public static final HashMap<SkyboxBlock.Sky, RenderType> RENDER_TYPES = HashMap.newHashMap(SkyboxBlock.Sky.values().length);

	public SkyboxBlockRenderer(BlockEntityRendererProvider.Context context) {
		for (SkyboxBlock.Sky value : SkyboxBlock.Sky.values()) {
			RENDER_TYPES.put(value, createRenderType(value));
		}
	}

	public void render(@NotNull T blockEntity, float p_112651_, PoseStack poseStack, @NotNull MultiBufferSource buffer, int p_112654_, int p_112655_) {
		Matrix4f matrix4f = poseStack.last().pose();

		CompiledShaderProgram shader = RenderSystem.setShader(ModShaders.SKYBOX);
		if (shader != null) {
			Uniform screenSize = shader.getUniform("ScreenSize");
			if (screenSize != null) {
				screenSize.set((float)Minecraft.getInstance().getWindow().getWidth(), (float)Minecraft.getInstance().getWindow().getHeight());
			}

			Uniform view = shader.getUniform("View");
			if (view != null) {
				float x = Minecraft.getInstance().cameraEntity.getXRot();
				if (Minecraft.getInstance().options.getCameraType() == CameraType.THIRD_PERSON_FRONT) {
					x *= -1;
				}
				float y = Mth.wrapDegrees(Minecraft.getInstance().cameraEntity.getYRot());
				view.set(x, y);
			}

			shader.apply();
		}
		this.renderCube(blockEntity, matrix4f, buffer.getBuffer(RENDER_TYPES.get(blockEntity.getBlockState().getValue(SkyboxBlock.SKY))));
	}

	private void renderCube(T blockEntity, Matrix4f pose, VertexConsumer consumer) {
		this.renderFace(blockEntity, pose, consumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH);
		this.renderFace(blockEntity, pose, consumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH);
		this.renderFace(blockEntity, pose, consumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST);
		this.renderFace(blockEntity, pose, consumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
		this.renderFace(blockEntity, pose, consumer, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN);
		this.renderFace(blockEntity, pose, consumer, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP);
	}

	private void renderFace(
			T blockEntity,
			Matrix4f pose,
			VertexConsumer consumer,
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
		if (blockEntity.getLevel() == null) {
			return;
		}
		BlockState neighbor = blockEntity.getLevel().getBlockState(blockEntity.getBlockPos().subtract(direction.getUnitVec3i().multiply(-1)));
		if (neighbor.is(blockEntity.getBlockState().getBlock())) {
			return;
		}
		if (neighbor.isSolidRender()) {
			return;
		}

		consumer.addVertex(pose, x0, y0, z0);
		consumer.addVertex(pose, x1, y0, z1);
		consumer.addVertex(pose, x1, y1, z2);
		consumer.addVertex(pose, x0, y1, z3);
	}


	private static RenderType createRenderType(SkyboxBlock.Sky sky)  {
		return RenderType.create(
				FictitiousSkies.MODID + "_skybox_" + sky.name(),
				DefaultVertexFormat.POSITION,
				VertexFormat.Mode.QUADS,
				1536,
				false,
				false,
				RenderType.CompositeState.builder()
						.setShaderState(SKYBOX_SHADER)
						.setTextureState(RenderStateShard.MultiTextureStateShard.builder()
								.add(sky.getSkyLocation(), sky.getBlur(), false)
								.build()
						).createCompositeState(false)
		);
	}
}
