package com.github.unreference.untapped.mixin.world.item;

import com.github.unreference.untapped.core.component.UntappedDataComponents;
import com.github.unreference.untapped.core.component.UntappedQuiverContents;
import com.github.unreference.untapped.world.item.UntappedItems;
import com.github.unreference.untapped.world.item.UntappedQuiverItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProjectileWeaponItem.class)
public abstract class UntappedMixinProjectileWeaponItem {
  @Inject(method = "useAmmo", at = @At("HEAD"), cancellable = true)
  private static void useAmmo(
      ItemStack itemStack,
      ItemStack itemStack2,
      LivingEntity livingEntity,
      boolean bl,
      CallbackInfoReturnable<ItemStack> cir) {
    if (!itemStack2.has(UntappedDataComponents.FROM_QUIVER)) {
      return;
    }

    itemStack2.remove(UntappedDataComponents.FROM_QUIVER);

    if (!(livingEntity instanceof Player player)) {
      cir.setReturnValue(itemStack2);
      return;
    }

    final Level level = livingEntity.level();
    final int ammoUsed =
        !bl && !player.hasInfiniteMaterials() && level instanceof ServerLevel serverLevel
            ? EnchantmentHelper.processAmmoUse(serverLevel, itemStack, itemStack2, 1)
            : 0;

    if (ammoUsed > 0) {
      final Inventory inventory = player.getInventory();
      ItemStack quiverStack = ItemStack.EMPTY;

      for (int j = 0; j < inventory.getContainerSize(); j++) {
        final ItemStack inventoryStack = inventory.getItem(j);
        if (inventoryStack.is(UntappedItems.QUIVER)) {
          quiverStack = inventoryStack;
          break;
        }
      }

      if (!quiverStack.isEmpty()) {
        final UntappedQuiverContents quiverContents =
            quiverStack.getOrDefault(
                UntappedDataComponents.QUIVER_CONTENTS, UntappedQuiverContents.EMPTY);
        final UntappedQuiverContents.Mutable mutable =
            new UntappedQuiverContents.Mutable(quiverContents);
        mutable.removeOne();
        quiverStack.set(UntappedDataComponents.QUIVER_CONTENTS, mutable.toImmutable());
        UntappedQuiverItem.broadcastChangesOnContainerMenu(player);
      }
    }

    cir.setReturnValue(itemStack2);
  }
}
