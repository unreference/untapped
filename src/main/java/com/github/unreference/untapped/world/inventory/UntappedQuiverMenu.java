package com.github.unreference.untapped.world.inventory;

import com.github.unreference.untapped.core.component.UntappedQuiverContents;
import com.github.unreference.untapped.world.item.UntappedItems;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class UntappedQuiverMenu extends AbstractContainerMenu {
  private static final int QUIVER_SLOT_COUNT = UntappedQuiverContainer.CONTAINER_SIZE;
  private static final int PLAYER_INV_SLOT_COUNT =
      InventoryMenu.INV_SLOT_END - InventoryMenu.INV_SLOT_START;
  private static final int PLAYER_HOTBAR_SLOT_COUNT =
      InventoryMenu.USE_ROW_SLOT_END - InventoryMenu.USE_ROW_SLOT_START;
  private static final int PLAYER_TOTAL_SLOT_COUNT =
      PLAYER_INV_SLOT_COUNT + PLAYER_HOTBAR_SLOT_COUNT;
  private static final int TOTAL_SLOTS = QUIVER_SLOT_COUNT + PLAYER_TOTAL_SLOT_COUNT;
  private static final int QUIVER_SLOT_START_INDEX = 0;
  private static final int PLAYER_INV_START_INDEX = QUIVER_SLOT_COUNT;
  private static final int PLAYER_INV_X_OFFSET = 8;

  private static final int[] QUIVER_SLOT_X_OFFSETS = new int[] {44, 80, 98, 116};
  private static final int QUIVER_Y_OFFSET = 17;

  private static final int PLAYER_INV_Y_OFFSET = 51;

  private final Container quiverContainer;
  private final ItemStack quiverStack;

  public UntappedQuiverMenu(int id, Inventory playerInventory, Container quiverContainer) {
    super(UntappedMenuTypes.QUIVER, id);

    checkContainerSize(quiverContainer, QUIVER_SLOT_COUNT);
    this.quiverContainer = quiverContainer;
    this.quiverStack =
        playerInventory.player.getMainHandItem().is(UntappedItems.QUIVER)
            ? playerInventory.player.getMainHandItem()
            : playerInventory.player.getOffhandItem();

    for (int i = 0; i < QUIVER_SLOT_COUNT; i++) {
      this.addSlot(
          new UntappedQuiverSlot(quiverContainer, i, QUIVER_SLOT_X_OFFSETS[i], QUIVER_Y_OFFSET));
    }

    this.addStandardInventorySlots(playerInventory, PLAYER_INV_X_OFFSET, PLAYER_INV_Y_OFFSET);
  }

  @Override
  public @NotNull ItemStack quickMoveStack(Player player, int i) {
    ItemStack itemStack = ItemStack.EMPTY;

    final Slot slot = this.slots.get(i);
    if (slot.hasItem()) {
      final ItemStack itemStackInSlot = slot.getItem();
      itemStack = itemStackInSlot.copy();

      if (i < QUIVER_SLOT_COUNT) {
        if (!this.moveItemStackTo(itemStackInSlot, PLAYER_INV_START_INDEX, TOTAL_SLOTS, true)) {
          return ItemStack.EMPTY;
        }
      } else {
        if (!UntappedQuiverContents.ammo(itemStackInSlot)) {
          return ItemStack.EMPTY;
        }

        if (!this.moveItemStackTo(
            itemStackInSlot, QUIVER_SLOT_START_INDEX, QUIVER_SLOT_COUNT, false)) {
          return ItemStack.EMPTY;
        }
      }

      if (itemStackInSlot.isEmpty()) {
        slot.set(ItemStack.EMPTY);
      } else {
        slot.setChanged();
      }
    }

    return itemStack;
  }

  @Override
  public boolean stillValid(Player player) {
    return this.quiverStack.is(UntappedItems.QUIVER)
        && (player.getMainHandItem() == this.quiverStack
            || player.getOffhandItem() == this.quiverStack);
  }

  @Override
  public void removed(Player player) {
    super.removed(player);
    this.quiverContainer.setChanged();
  }
}
