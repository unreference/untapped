package com.github.unreference.untapped.world.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class UntappedItemUtils {
  public static ItemStack convertItemInHand(
      Player player, ItemStack fromStack, ItemStack toStack, int shrinkCount) {
    if (player.hasInfiniteMaterials()) {
      final ItemStack toStackCopy = toStack.copy();
      if (!player.getInventory().contains(toStackCopy)) {
        player.getInventory().add(toStackCopy);
      }

      return fromStack;

    } else {
      fromStack.shrink(shrinkCount);
      if (fromStack.isEmpty()) {
        return toStack.copy();
      } else {
        if (!player.getInventory().add(toStack.copy())) {
          player.drop(toStack.copy(), false);
        }

        return fromStack;
      }
    }
  }

  public static ItemStack convertItemInHand(Player player, ItemStack fromStack, ItemStack toStack) {
    if (player.hasInfiniteMaterials()) {
      final ItemStack toStackCopy = toStack.copy();
      if (!player.getInventory().contains(toStackCopy)) {
        player.getInventory().add(toStackCopy);
      }

      return fromStack;

    } else {
      fromStack.shrink(1);
      if (fromStack.isEmpty()) {
        return toStack.copy();
      } else {
        if (!player.getInventory().add(toStack.copy())) {
          player.drop(toStack.copy(), false);
        }

        return fromStack;
      }
    }
  }
}
