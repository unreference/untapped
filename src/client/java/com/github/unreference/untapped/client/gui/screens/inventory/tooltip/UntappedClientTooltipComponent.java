package com.github.unreference.untapped.client.gui.screens.inventory.tooltip;

import com.github.unreference.untapped.core.component.UntappedQuiverContents;
import com.github.unreference.untapped.world.inventory.tooltip.UntappedQuiverTooltip;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

public final class UntappedClientTooltipComponent {
  public static void initialize() {
    TooltipComponentCallback.EVENT.register(
        tooltipComponent -> {
          if (!(tooltipComponent
              instanceof UntappedQuiverTooltip(UntappedQuiverContents quiverContents))) {
            return null;
          }

          return new UntappedClientQuiverTooltip(quiverContents);
        });
  }
}
