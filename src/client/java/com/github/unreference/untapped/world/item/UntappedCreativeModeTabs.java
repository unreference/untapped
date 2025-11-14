package com.github.unreference.untapped.world.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;

public final class UntappedCreativeModeTabs {
  public static void initialize() {
    ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT)
        .register((tab) -> tab.addAfter(Items.DIAMOND_HORSE_ARMOR, UntappedItems.QUIVER));
  }
}
