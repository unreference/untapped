package com.github.unreference.untapped.client.gui.screens;

import com.github.unreference.untapped.client.gui.screens.inventory.UntappedQuiverScreen;
import com.github.unreference.untapped.world.inventory.UntappedMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;

public final class UntappedMenuScreens {
  public static void initialize() {
    MenuScreens.register(UntappedMenuTypes.QUIVER, UntappedQuiverScreen::new);
  }
}
