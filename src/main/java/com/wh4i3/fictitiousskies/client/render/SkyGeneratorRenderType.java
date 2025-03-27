package com.wh4i3.fictitiousskies.client.render;

import java.util.function.BiFunction;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.wh4i3.fictitiousskies.client.ModShaders;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class SkyGeneratorRenderType {
    public static final RenderStateShard.ShaderStateShard SKYBOX_SHADER_STATE = new RenderStateShard.ShaderStateShard(
        ModShaders.SKYBOX
    );

    public static final BiFunction<ResourceLocation, Boolean, RenderType> SKYBOX = Util.memoize(
        (tex, blur) -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                    .setShaderState(SKYBOX_SHADER_STATE)
                    .setTextureState(new RenderStateShard.MultiTextureStateShard.Builder()
                        .add(tex, blur, false)
                        .build())
                    .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                    .setOverlayState(RenderStateShard.NO_OVERLAY).createCompositeState(false);
            return RenderType.create("skybox", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 1536, true, false, rendertype$compositestate);
        }
    );

    public static RenderType skybox(ResourceLocation texture, boolean blur) {
        return SKYBOX.apply(texture, blur);
    }

    private SkyGeneratorRenderType() {
    }
}