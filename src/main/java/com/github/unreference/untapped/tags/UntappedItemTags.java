package com.github.unreference.untapped.tags;

import com.github.unreference.untapped.resources.UntappedResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class UntappedItemTags {
  public static final TagKey<Item> STAINED_GLASS = bind("stained_glass");
  public static final TagKey<Item> STAINED_GLASS_PANES = bind("stained_glass_panes");

  private static TagKey<Item> bind(String name) {
    return TagKey.create(Registries.ITEM, UntappedResourceLocation.withDefaultNamespace(name));
  }
}
