package com.wh4i3.fictitiousskies.init;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wh4i3.fictitiousskies.FictitiousSkies;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class ModDataComponentType {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, FictitiousSkies.MODID);

    public record Skybox(ResourceLocation skyboxLocation, boolean blur, int fallbackColor) {
        public static final Skybox EMPTY = new Skybox(ResourceLocation.withDefaultNamespace(""), true, 0xFF_FFFFFF);

        public static Codec<Skybox> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                ResourceLocation.CODEC.fieldOf("skyboxLocation").forGetter(Skybox::skyboxLocation),
                Codec.BOOL.fieldOf("blur").forGetter(Skybox::blur),
                Codec.INT.fieldOf("fallbackColor").forGetter(Skybox::fallbackColor)
        ).apply(inst, Skybox::new));

        public boolean isEmpty() {
            return skyboxLocation == ResourceLocation.withDefaultNamespace("");
        }
    }

    public interface ISkyboxFallback {

    }

    public static class ColorSkyboxFallback implements ISkyboxFallback {
        @Getter @Setter
        private int color;

        ColorSkyboxFallback(int color) {
            this.color = color;
        }

        public static Codec<ColorSkyboxFallback> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.INT.fieldOf("color").forGetter(ColorSkyboxFallback::getColor)
        ).apply(inst, ColorSkyboxFallback::new));
    }


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Skybox>> SKYBOX = register("skybox", Skybox.CODEC, null);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> CREDIT = register("credit", PrimitiveCodec.STRING, ByteBufCodecs.STRING_UTF8);

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, final Codec<T> codec, @Nullable final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        if (streamCodec == null) {
            return DATA_COMPONENTS.register(name, () -> DataComponentType.<T>builder().persistent(codec).build());
        } else {
            return DATA_COMPONENTS.register(name, () -> DataComponentType.<T>builder().persistent(codec).networkSynchronized(streamCodec).build());
        }
    }
}
