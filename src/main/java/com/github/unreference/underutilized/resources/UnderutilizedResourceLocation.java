package com.github.unreference.underutilized.resources;

import com.github.unreference.underutilized.Underutilized;
import net.minecraft.resources.ResourceLocation;

public final class UnderutilizedResourceLocation {
  public static ResourceLocation withDefaultNamespace(String name) {
    return ResourceLocation.fromNamespaceAndPath(Underutilized.MOD_ID, name);
  }
}
