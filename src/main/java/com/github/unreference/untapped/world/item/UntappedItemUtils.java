package com.github.unreference.untapped.world.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class UntappedItemUtils {
  public static ItemStack createFilledResults(
      ItemStack fromStack, Player player, ItemStack toStack, boolean isDuplicatable, int amount) {
    final boolean isCreative = player.hasInfiniteMaterials();

    if (isDuplicatable && isCreative) {
      if (!player.getInventory().contains(toStack)) {
        player.getInventory().add(toStack);
      }

      return fromStack;
    } else {
      fromStack.consume(amount, player);

      if (fromStack.isEmpty()) {
        return toStack;
      } else {
        if (!player.getInventory().add(toStack)) {
          player.drop(toStack, false);
        }

        return fromStack;
      }
    }
  }
}
