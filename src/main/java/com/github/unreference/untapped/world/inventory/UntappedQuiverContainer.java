package com.github.unreference.untapped.world.inventory;

import com.github.unreference.untapped.core.component.UntappedDataComponents;
import com.github.unreference.untapped.core.component.UntappedQuiverContents;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class UntappedQuiverContainer implements Container {
  public static final int CONTAINER_SIZE = UntappedQuiverContents.MAX_CAPACITY / 64;

  private final ItemStack quiverStack;
  private final NonNullList<ItemStack> items;

  public UntappedQuiverContainer(ItemStack quiverStack) {
    this.quiverStack = quiverStack;
    this.items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);

    final UntappedQuiverContents quiverContents =
        quiverStack.getOrDefault(
            UntappedDataComponents.QUIVER_CONTENTS, UntappedQuiverContents.EMPTY);

    final List<ItemStack> itemCopies = quiverContents.itemsCopyStream().toList();
    for (int i = 0; i < Math.min(itemCopies.size(), CONTAINER_SIZE); i++) {
      this.items.set(i, itemCopies.get(i));
    }
  }

  @Override
  public int getContainerSize() {
    return CONTAINER_SIZE;
  }

  @Override
  public boolean isEmpty() {
    for (ItemStack itemStack : this.items) {
      if (!itemStack.isEmpty()) {
        return false;
      }
    }

    return true;
  }

  @Override
  public @NotNull ItemStack getItem(int i) {
    return this.items.get(i);
  }

  @Override
  public @NotNull ItemStack removeItem(int i, int j) {
    final ItemStack itemStack = this.items.get(i);
    if (itemStack.isEmpty()) {
      return ItemStack.EMPTY;
    }

    return itemStack.split(j);
  }

  @Override
  public @NotNull ItemStack removeItemNoUpdate(int i) {
    final ItemStack itemStack = this.items.get(i);
    if (itemStack.isEmpty()) {
      return ItemStack.EMPTY;
    }

    this.items.set(i, ItemStack.EMPTY);
    return itemStack;
  }

  @Override
  public void setItem(int i, ItemStack itemStack) {
    if (UntappedQuiverContents.ammo(itemStack) || itemStack.isEmpty()) {
      this.items.set(i, itemStack);
    }
  }

  @Override
  public void setChanged() {
    final List<ItemStack> newItems =
        this.items.stream().filter(itemStack -> !itemStack.isEmpty()).toList();

    final UntappedQuiverContents newContents = new UntappedQuiverContents(newItems);
    this.quiverStack.set(UntappedDataComponents.QUIVER_CONTENTS, newContents);
  }

  @Override
  public boolean stillValid(Player player) {
    return player.getMainHandItem().is(this.quiverStack.getItem())
        || player.getOffhandItem().is(this.quiverStack.getItem());
  }

  @Override
  public void clearContent() {
    this.items.clear();
  }
}
