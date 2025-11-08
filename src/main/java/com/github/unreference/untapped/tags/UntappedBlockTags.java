package com.github.unreference.untapped.tags;

import com.github.unreference.untapped.resources.UntappedResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class UntappedBlockTags {
  public static final TagKey<Block> STAINED_GLASS = create("stained_glass");
  public static final TagKey<Block> STAINED_GLASS_PANES = create("stained_glass_panes");

  private static TagKey<Block> create(String name) {
    return TagKey.create(Registries.BLOCK, UntappedResourceLocation.withDefaultNamespace(name));
  }
}
