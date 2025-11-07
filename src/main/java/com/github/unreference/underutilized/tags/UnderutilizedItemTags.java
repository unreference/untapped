package com.github.unreference.underutilized.tags;

import com.github.unreference.underutilized.Underutilized;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class UnderutilizedItemTags {
  public static final TagKey<Item> STAINED_GLASS = bind("stained_glass");
  public static final TagKey<Item> STAINED_GLASS_PANES = bind("stained_glass_panes");

  private static TagKey<Item> bind(String name) {
    return TagKey.create(
        Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Underutilized.MOD_ID, name));
  }
}
