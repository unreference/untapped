package com.github.unreference.untapped.world.item;

import com.github.unreference.untapped.core.component.UntappedDataComponents;
import com.github.unreference.untapped.core.component.UntappedQuiverContents;
import com.github.unreference.untapped.world.inventory.UntappedQuiverContainer;
import com.github.unreference.untapped.world.inventory.UntappedQuiverMenu;
import com.github.unreference.untapped.world.inventory.tooltip.UntappedQuiverTooltip;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;

public final class UntappedQuiverItem extends Item {
  private static final int FULL_BAR_COLOR = ARGB.colorFromFloat(1.0f, 0.44f, 0.53f, 1.0f);
  private static final int BAR_COLOR = ARGB.colorFromFloat(1.0f, 1.0f, 0.33f, 0.33f);

  public UntappedQuiverItem(Properties properties) {
    super(properties);
  }

  public static void broadcastChangesOnContainerMenu(Player player) {
    final AbstractContainerMenu abstractContainerMenu = player.containerMenu;
    abstractContainerMenu.slotsChanged(player.getInventory());
  }

  private static void playerInsertFailSound(Player player) {
    player.playSound(SoundEvents.BUNDLE_INSERT_FAIL);
  }

  private static void playInsertSound(Player player) {
    player.playSound(
        SoundEvents.BUNDLE_INSERT, 0.8f, 0.8f + player.level().getRandom().nextInt() * 0.4f);
  }

  private static void playRemoveOneSound(Player player) {
    player.playSound(
        SoundEvents.BUNDLE_REMOVE_ONE, 0.8f, 0.8f + player.level().getRandom().nextFloat() * 0.4f);
  }

  @Override
  public boolean overrideStackedOnOther(
      ItemStack itemStack, Slot slot, ClickAction clickAction, Player player) {
    if (player.containerMenu instanceof UntappedQuiverMenu) {
      return false;
    }

    final UntappedQuiverContents quiverContents =
        itemStack.getOrDefault(
            UntappedDataComponents.QUIVER_CONTENTS, UntappedQuiverContents.EMPTY);
    final UntappedQuiverContents.Mutable mutable =
        new UntappedQuiverContents.Mutable(quiverContents);

    if (clickAction.equals(ClickAction.PRIMARY)) {
      final ItemStack arrowStack = slot.getItem();

      if (!UntappedQuiverContents.ammo(arrowStack)) {
        return false;
      }

      final int added = mutable.tryInsert(arrowStack);
      if (added > 0) {
        playInsertSound(player);
        itemStack.set(UntappedDataComponents.QUIVER_CONTENTS, mutable.toImmutable());
        broadcastChangesOnContainerMenu(player);
        return true;
      }

      playerInsertFailSound(player);
      return false;
    } else if (clickAction.equals(ClickAction.SECONDARY)) {
      final ItemStack arrowStack = slot.getItem();
      if (!arrowStack.isEmpty()) {
        return false;
      }

      final ItemStack removedStack = mutable.removeOneStack();
      if (removedStack != null) {
        playRemoveOneSound(player);
        slot.set(removedStack);
        itemStack.set(UntappedDataComponents.QUIVER_CONTENTS, mutable.toImmutable());
        broadcastChangesOnContainerMenu(player);
        return true;
      }

      return false;
    }

    return false;
  }

  @Override
  public boolean overrideOtherStackedOnMe(
      ItemStack itemStack,
      ItemStack itemStack2,
      Slot slot,
      ClickAction clickAction,
      Player player,
      SlotAccess slotAccess) {
    if (player.containerMenu instanceof UntappedQuiverMenu) {
      return false;
    }

    if (!slot.allowModification(player)) {
      return false;
    }

    final UntappedQuiverContents quiverContents =
        itemStack.getOrDefault(
            UntappedDataComponents.QUIVER_CONTENTS, UntappedQuiverContents.EMPTY);
    final UntappedQuiverContents.Mutable mutable =
        new UntappedQuiverContents.Mutable(quiverContents);

    if (clickAction.equals(ClickAction.PRIMARY)) {
      if (!UntappedQuiverContents.ammo(itemStack2)) {
        return false;
      }

      final int added = mutable.tryInsert(itemStack2);
      if (added > 0) {
        playInsertSound(player);
        itemStack.set(UntappedDataComponents.QUIVER_CONTENTS, mutable.toImmutable());
        broadcastChangesOnContainerMenu(player);
        return true;
      }

      playerInsertFailSound(player);
      return false;
    } else if (clickAction.equals(ClickAction.SECONDARY)) {
      if (!itemStack2.isEmpty()) {
        return false;
      }

      final ItemStack removedStack = mutable.removeOneStack();
      if (removedStack != null) {
        playRemoveOneSound(player);
        slotAccess.set(removedStack);
      }
    } else {
      return false;
    }

    itemStack.set(UntappedDataComponents.QUIVER_CONTENTS, mutable.toImmutable());
    broadcastChangesOnContainerMenu(player);
    return true;
  }

  @Override
  public @NotNull InteractionResult use(
      Level level, Player player, InteractionHand interactionHand) {
    final ItemStack itemStack = player.getItemInHand(interactionHand);

    if (!level.isClientSide()) {
      final MenuProvider menuProvider =
          new SimpleMenuProvider(
              (id, inventory, player1) ->
                  new UntappedQuiverMenu(id, inventory, new UntappedQuiverContainer(itemStack)),
              itemStack.getHoverName());

      player.openMenu(menuProvider);
    }

    return InteractionResult.SUCCESS;
  }

  @Override
  public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {
    final TooltipDisplay tooltipDisplay =
        itemStack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
    return !tooltipDisplay.shows(UntappedDataComponents.QUIVER_CONTENTS)
        ? Optional.empty()
        : Optional.ofNullable(itemStack.get(UntappedDataComponents.QUIVER_CONTENTS))
            .map(UntappedQuiverTooltip::new);
  }

  @Override
  public boolean isBarVisible(ItemStack itemStack) {
    final UntappedQuiverContents quiverContents =
        itemStack.getOrDefault(
            UntappedDataComponents.QUIVER_CONTENTS, UntappedQuiverContents.EMPTY);
    return !quiverContents.empty();
  }

  @Override
  public int getBarWidth(ItemStack itemStack) {
    final UntappedQuiverContents quiverContents =
        itemStack.getOrDefault(
            UntappedDataComponents.QUIVER_CONTENTS, UntappedQuiverContents.EMPTY);
    final float fill =
        (float) quiverContents.occupancy() / (float) UntappedQuiverContents.MAX_CAPACITY;
    return Math.min(1 + Mth.mulAndTruncate(Fraction.getFraction(fill), 12), 13);
  }

  @Override
  public int getBarColor(ItemStack itemStack) {
    final UntappedQuiverContents quiverContents =
        itemStack.getOrDefault(
            UntappedDataComponents.QUIVER_CONTENTS, UntappedQuiverContents.EMPTY);

    if (quiverContents.occupancy() >= UntappedQuiverContents.MAX_CAPACITY) {
      return FULL_BAR_COLOR;
    }

    return BAR_COLOR;
  }
}
