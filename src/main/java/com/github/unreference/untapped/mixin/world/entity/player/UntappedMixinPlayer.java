package com.github.unreference.untapped.mixin.world.entity.player;

import com.github.unreference.untapped.core.component.UntappedDataComponents;
import com.github.unreference.untapped.core.component.UntappedQuiverContents;
import com.github.unreference.untapped.world.item.UntappedItems;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class UntappedMixinPlayer {
  @Inject(method = "getProjectile", at = @At("HEAD"), cancellable = true)
  private void getProjectile(ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir) {
    final Player player = (Player) (Object) this;
    final Item weaponItem = itemStack.getItem();

    final boolean isBow = weaponItem instanceof BowItem;
    final boolean isCrossbow = weaponItem instanceof CrossbowItem;
    if (!isBow && !isCrossbow) {
      return;
    }

    final Inventory inventory = player.getInventory();

    ItemStack quiverStack = ItemStack.EMPTY;
    for (int i = 0; i < inventory.getContainerSize(); i++) {
      final ItemStack inventoryStack = inventory.getItem(i);
      if (inventoryStack.is(UntappedItems.QUIVER)) {
        quiverStack = inventoryStack;
        break;
      }
    }

    if (quiverStack.isEmpty()) {
      return;
    }

    final UntappedQuiverContents quiverContents =
        quiverStack.getOrDefault(
            UntappedDataComponents.QUIVER_CONTENTS, UntappedQuiverContents.EMPTY);
    if (quiverContents.empty()) {
      return;
    }

    ItemStack chosenStack = quiverContents.items().getFirst();
    if (chosenStack.isEmpty()) {
      return;
    }

    boolean isValidAmmo = false;
    if (isCrossbow) {
      if (UntappedQuiverContents.ammo(chosenStack)) {
        isValidAmmo = true;
      }
    } else {
      if (UntappedQuiverContents.arrow(chosenStack)) {
        isValidAmmo = true;
      }
    }

    if (!isValidAmmo) {
      return;
    }

    final ItemStack projectileStack = chosenStack.copyWithCount(1);
    projectileStack.set(UntappedDataComponents.FROM_QUIVER, true);
    cir.setReturnValue(projectileStack);
  }
}
