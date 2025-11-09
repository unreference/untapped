package com.github.unreference.untapped.core.cauldron;

import com.github.unreference.untapped.tags.UntappedItemTagsProvider;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public final class UntappedCauldronWashableHolder {
  public static final Map<TagKey<Item>, Item> WASHABLE =
      Util.make(
          Maps.newHashMap(),
          map -> {
            map.put(ItemTags.WOOL, Items.WHITE_WOOL);
            map.put(ItemTags.WOOL_CARPETS, Items.WHITE_CARPET);
            map.put(ItemTags.BEDS, Items.WHITE_BED);
            map.put(ItemTags.CANDLES, Items.CANDLE);
            map.put(ItemTags.TERRACOTTA, Items.TERRACOTTA);
            map.put(UntappedItemTagsProvider.STAINED_GLASS, Items.GLASS);
            map.put(UntappedItemTagsProvider.STAINED_GLASS_PANES, Items.GLASS_PANE);
          });

  public static final Map<Item, Item> WASHABLE_GLAZED_TERRACOTTA =
      Util.make(
          Maps.newHashMap(),
          map -> {
            for (DyeColor color : DyeColor.values()) {
              final Item glazedTerracotta =
                  BuiltInRegistries.ITEM.getValue(
                      ResourceLocation.withDefaultNamespace(
                          color.getName() + "_glazed_terracotta"));

              final Item terracotta =
                  BuiltInRegistries.ITEM.getValue(
                      ResourceLocation.withDefaultNamespace(color.getName() + "_terracotta"));

              map.put(glazedTerracotta, terracotta);
            }
          });
}
