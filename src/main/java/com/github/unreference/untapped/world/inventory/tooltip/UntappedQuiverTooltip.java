package com.github.unreference.untapped.world.inventory.tooltip;

import com.github.unreference.untapped.core.component.UntappedQuiverContents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record UntappedQuiverTooltip(UntappedQuiverContents contents) implements TooltipComponent {}
