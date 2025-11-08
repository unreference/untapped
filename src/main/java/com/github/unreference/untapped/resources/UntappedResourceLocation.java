package com.github.unreference.untapped.resources;

import com.github.unreference.untapped.Untapped;
import net.minecraft.resources.ResourceLocation;

public final class UntappedResourceLocation {
  public static ResourceLocation withDefaultNamespace(String name) {
    return ResourceLocation.fromNamespaceAndPath(Untapped.MOD_ID, name);
  }
}
