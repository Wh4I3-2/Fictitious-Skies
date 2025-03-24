package com.wh4i3.fictitiousskies.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.wh4i3.fictitiousskies.FictitiousSkies;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.ShaderProgram;

public class ModShaders {
	public static ShaderProgram SKYBOX = new ShaderProgram(FictitiousSkies.id("skybox"), DefaultVertexFormat.BLOCK, ShaderDefines.EMPTY);
}