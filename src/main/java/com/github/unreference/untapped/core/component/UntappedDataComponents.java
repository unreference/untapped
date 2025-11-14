package com.github.unreference.untapped.core.component;

import com.github.unreference.untapped.resources.UntappedResourceLocation;
import com.mojang.serialization.Codec;
import java.util.function.UnaryOperator;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;

public final class UntappedDataComponents {
  public static final DataComponentType<UntappedQuiverContents> QUIVER_CONTENTS =
      register(
          "quiver_contents",
          builder ->
              builder
                  .persistent(UntappedQuiverContents.CODEC)
                  .networkSynchronized(UntappedQuiverContents.STREAM_CODEC)
                  .cacheEncoding());

  public static final DataComponentType<Boolean> FROM_QUIVER =
      register(
          "from_quiver",
          builder ->
              builder
                  .persistent(Codec.BOOL)
                  .networkSynchronized(ByteBufCodecs.BOOL)
                  .cacheEncoding());

  private static <T> DataComponentType<T> register(
      String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
    return Registry.register(
        BuiltInRegistries.DATA_COMPONENT_TYPE,
        UntappedResourceLocation.withDefaultNamespace(name),
        builder.apply(DataComponentType.builder()).build());
  }

  public static void initialize() {}
}
