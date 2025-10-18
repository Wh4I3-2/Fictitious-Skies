package com.wh4i3.fictitiousskies.init;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wh4i3.fictitiousskies.FictitiousSkies;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class ModDataComponentType {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, FictitiousSkies.MODID);

    public record Skybox(
            ResourceLocation skyboxLocation,
            boolean blur,
            Optional<SkyboxFallback> fallback
    ) {
        public static final Skybox EMPTY = new Skybox(ResourceLocation.withDefaultNamespace(""), true, Optional.of(SkyboxFallback.DEFAULT));

        public static Codec<Skybox> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                ResourceLocation.CODEC.fieldOf("skyboxLocation").forGetter(Skybox::skyboxLocation),
                Codec.BOOL.fieldOf("blur").forGetter(Skybox::blur),
                SkyboxFallback.CODEC.lenientOptionalFieldOf("fallback").forGetter(Skybox::fallback)
        ).apply(inst, Skybox::new));

        public boolean isEmpty() {
            return Objects.equals(skyboxLocation, ResourceLocation.withDefaultNamespace(""));
        }
    }

    public record SkyboxFallback(
            SkyboxFallbackType type,
            Optional<Integer> color,
            Optional<ResourceLocation> texture,
            Optional<BlockState> block
    ) {
        public enum SkyboxFallbackType {
            COLOR,
            TEXTURE,
            BLOCK,
        }

        public static final SkyboxFallback DEFAULT = new SkyboxFallback(
                SkyboxFallbackType.COLOR,
                Optional.of(0xFFFFFF),
                Optional.empty(), Optional.empty()
        );

        public static final Codec<SkyboxFallback> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.stringResolver(
                        SkyboxFallbackType::name,
                        SkyboxFallbackType::valueOf
                ).fieldOf("type").forGetter(SkyboxFallback::type),
                Codec.INT.lenientOptionalFieldOf("color").forGetter(SkyboxFallback::color),
                ResourceLocation.CODEC.lenientOptionalFieldOf("texture").forGetter(SkyboxFallback::texture),
                BlockState.CODEC.lenientOptionalFieldOf("block").forGetter(SkyboxFallback::block)
        ).apply(inst, SkyboxFallback::new));
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
