package com.github.unreference.untapped.world.inventory;

import com.github.unreference.untapped.core.component.UntappedQuiverContents;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class UntappedQuiverSlot extends Slot {
  public UntappedQuiverSlot(Container container, int i, int j, int k) {
    super(container, i, j, k);
  }

  @Override
  public boolean mayPlace(ItemStack itemStack) {
    return UntappedQuiverContents.ammo(itemStack);
  }
}
