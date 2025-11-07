package com.github.unreference.underutilized.core.cauldron;

import com.github.unreference.underutilized.tags.UnderutilizedItemTags;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public final class UnderutilizedCauldronWashableHolder {
  public static final Map<TagKey<Item>, Item> WASHABLE =
      Util.make(
          Maps.newHashMap(),
          map -> {
            map.put(ItemTags.WOOL, Items.WHITE_WOOL);
            map.put(ItemTags.WOOL_CARPETS, Items.WHITE_CARPET);
            map.put(ItemTags.BEDS, Items.WHITE_BED);
            map.put(ItemTags.CANDLES, Items.CANDLE);
            map.put(ItemTags.TERRACOTTA, Items.TERRACOTTA);
            map.put(UnderutilizedItemTags.STAINED_GLASS, Items.GLASS);
            map.put(UnderutilizedItemTags.STAINED_GLASS_PANES, Items.GLASS_PANE);
          });
}
