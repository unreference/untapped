package com.github.unreference.untapped.client;

import com.github.unreference.untapped.client.gui.screens.UntappedMenuScreens;
import com.github.unreference.untapped.client.gui.screens.inventory.tooltip.UntappedClientTooltipComponent;
import com.github.unreference.untapped.client.renderer.UntappedItemBlockRenderTypes;
import com.github.unreference.untapped.client.renderer.blockentity.UntappedBlockEntityRenderers;
import com.github.unreference.untapped.world.item.UntappedCreativeModeTabs;
import net.fabricmc.api.ClientModInitializer;

public class UntappedClient implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    UntappedItemBlockRenderTypes.initialize();
    UntappedBlockEntityRenderers.initialize();
    UntappedClientTooltipComponent.initialize();
    UntappedClientTooltipComponent.initialize();
    UntappedCreativeModeTabs.initialize();
    UntappedMenuScreens.initialize();
  }
}
