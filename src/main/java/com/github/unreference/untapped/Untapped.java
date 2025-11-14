package com.github.unreference.untapped;

import com.github.unreference.untapped.core.cauldron.UntappedCauldronInteraction;
import com.github.unreference.untapped.core.component.UntappedDataComponents;
import com.github.unreference.untapped.world.inventory.UntappedMenuTypes;
import com.github.unreference.untapped.world.item.UntappedItems;
import com.github.unreference.untapped.world.level.block.UntappedBlocks;
import com.github.unreference.untapped.world.level.block.entity.UntappedBlockEntityType;
import net.fabricmc.api.ModInitializer;

public class Untapped implements ModInitializer {
  public static final String MOD_ID = "untapped";

  @Override
  public void onInitialize() {
    UntappedDataComponents.initialize();
    UntappedMenuTypes.initialize();
    UntappedBlocks.initialize();
    UntappedItems.initialize();
    UntappedBlockEntityType.initialize();
    UntappedCauldronInteraction.initialize();
  }
}
