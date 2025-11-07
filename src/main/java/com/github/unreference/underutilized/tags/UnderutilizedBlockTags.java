package com.github.unreference.underutilized.tags;

import com.github.unreference.underutilized.Underutilized;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class UnderutilizedBlockTags {
  public static final TagKey<Block> STAINED_GLASS = create("stained_glass");
  public static final TagKey<Block> STAINED_GLASS_PANES = create("stained_glass_panes");

  private static TagKey<Block> create(String name) {
    return TagKey.create(
        Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Underutilized.MOD_ID, name));
  }
}
