package com.github.unreference.untapped.core.component;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record UntappedQuiverContents(List<ItemStack> items, int occupancy)
    implements TooltipComponent {
  public static final StreamCodec<RegistryFriendlyByteBuf, UntappedQuiverContents> STREAM_CODEC =
      ItemStack.STREAM_CODEC
          .apply(ByteBufCodecs.list())
          .map(UntappedQuiverContents::new, quiverContents -> quiverContents.items);
  public static final int MAX_CAPACITY = 256;
  public static final int MAX_STACKS = MAX_CAPACITY / 64;
  public static final Codec<UntappedQuiverContents> CODEC =
      ItemStack.CODEC
          .listOf()
          .flatXmap(
              UntappedQuiverContents::checkAndCreate,
              quiverContents -> DataResult.success(quiverContents.items));
  public static final UntappedQuiverContents EMPTY = new UntappedQuiverContents(List.of());

  public UntappedQuiverContents(List<ItemStack> items) {
    this(items, computeOccupancy(items));
  }

  private static DataResult<UntappedQuiverContents> checkAndCreate(List<ItemStack> list) {
    final int occupancy = computeOccupancy(list);
    if (occupancy > MAX_CAPACITY) {
      return DataResult.error(() -> "Quiver capacity exceeded: " + occupancy);
    }

    if (list.size() > MAX_STACKS) {
      return DataResult.error(() -> "Quiver stack limit exceeded: " + list.size());
    }

    return DataResult.success(new UntappedQuiverContents(list, occupancy));
  }

  private static int computeOccupancy(List<ItemStack> list) {
    int occupancy = 0;
    for (ItemStack itemStack : list) {
      occupancy += itemStack.getCount();
    }

    return occupancy;
  }

  public static boolean arrow(ItemStack itemStack) {
    return itemStack.getItem() instanceof ArrowItem;
  }

  public static boolean firework(ItemStack itemStack) {
    return itemStack.getItem() instanceof FireworkRocketItem;
  }

  public static boolean ammo(ItemStack itemStack) {
    if (itemStack.isEmpty()) {
      return false;
    }

    if (!itemStack.getItem().canFitInsideContainerItems()) {
      return false;
    }

    return arrow(itemStack) || firework(itemStack);
  }

  public Stream<ItemStack> itemsCopyStream() {
    return this.items.stream().map(ItemStack::copy);
  }

  public Iterable<ItemStack> itemsCopy() {
    return Lists.transform(this.items, itemStack -> itemStack != null ? itemStack.copy() : null);
  }

  public int size() {
    return this.items.size();
  }

  public boolean empty() {
    return this.items.isEmpty();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof UntappedQuiverContents(List<ItemStack> items1, int occupancy1))) {
      return false;
    }

    if (this.occupancy != occupancy1) {
      return false;
    }

    return this.items.equals(items1);
  }

  @Override
  public int hashCode() {
    return this.items.hashCode();
  }

  @Override
  public @NotNull String toString() {
    return "QuiverContents" + this.items;
  }

  public static class Mutable {
    private final List<ItemStack> items;
    private int occupancy;

    public Mutable(UntappedQuiverContents quiverContents) {
      this.items = new ArrayList<>(quiverContents.items());
      this.occupancy = quiverContents.occupancy();
    }

    private int findStackIndex(ItemStack itemStack) {
      if (!itemStack.isStackable()) {
        return -1;
      }

      for (int i = 0; i < this.items.size(); i++) {
        if (ItemStack.isSameItemSameComponents(this.items.get(i), itemStack)) {
          return i;
        }
      }

      return -1;
    }

    public int tryInsert(ItemStack itemStack) {
      if (!ammo(itemStack)) {
        return 0;
      }

      int totalAdded = 0;
      int remainingToAdd = itemStack.getCount();

      int spaceByOccupancy = MAX_CAPACITY - this.occupancy;
      if (spaceByOccupancy <= 0) {
        return 0;
      }

      remainingToAdd = Math.min(remainingToAdd, spaceByOccupancy);
      if (remainingToAdd <= 0) {
        return 0;
      }

      final int index = this.findStackIndex(itemStack);
      if (index != -1) {
        final ItemStack existingStack = this.items.get(index);
        final int spaceInStack = existingStack.getMaxStackSize() - existingStack.getCount();

        final int toAddToStack = Math.min(remainingToAdd, spaceInStack);
        if (toAddToStack > 0) {
          existingStack.grow(toAddToStack);
          remainingToAdd -= toAddToStack;
          totalAdded += toAddToStack;
        }

        this.items.remove(index);
        this.items.addFirst(existingStack);
      }

      while (remainingToAdd > 0) {
        if (this.items.size() >= MAX_STACKS) {
          break;
        }

        spaceByOccupancy = MAX_CAPACITY - (this.occupancy + totalAdded);

        final int amountForNewStack =
            Math.min(remainingToAdd, Math.min(itemStack.getMaxStackSize(), spaceByOccupancy));
        if (amountForNewStack <= 0) {
          break;
        }

        final ItemStack newStack = itemStack.copyWithCount(amountForNewStack);
        this.items.addFirst(newStack);
        remainingToAdd -= amountForNewStack;
        totalAdded += amountForNewStack;
      }

      if (totalAdded <= 0) {
        return 0;
      }

      this.occupancy += totalAdded;
      itemStack.shrink(totalAdded);

      return totalAdded;
    }

    public ItemStack removeOne() {
      if (this.items.isEmpty()) {
        return null;
      }

      final ItemStack firstStack = this.items.getFirst();
      final ItemStack arrowsToGive = firstStack.copyWithCount(1);

      firstStack.shrink(1);
      if (firstStack.isEmpty()) {
        this.items.removeFirst();
      }

      this.occupancy--;
      return arrowsToGive;
    }

    public UntappedQuiverContents toImmutable() {
      return new UntappedQuiverContents(List.copyOf(this.items), this.occupancy);
    }
  }
}
