package com.github.unreference.untapped.world.inventory;

import com.github.unreference.untapped.resources.UntappedResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public final class UntappedMenuTypes {
  public static final MenuType<UntappedQuiverMenu> QUIVER =
      register(
          "quiver",
          (id, inventory) ->
              new UntappedQuiverMenu(
                  id, inventory, new UntappedQuiverContainer(inventory.player.getMainHandItem())));

  private static <T extends AbstractContainerMenu> MenuType<T> register(
      String name, MenuType.MenuSupplier<T> factory) {
    return Registry.register(
        BuiltInRegistries.MENU,
        UntappedResourceLocation.withDefaultNamespace(name),
        new MenuType<>(factory, FeatureFlags.VANILLA_SET));
  }

  public static void initialize() {}
}
