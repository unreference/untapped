package com.github.unreference.untapped;

import com.github.unreference.untapped.world.level.block.UntappedBlocks;
import com.github.unreference.untapped.world.level.block.entity.UntappedBlockEntityType;
import net.fabricmc.api.ModInitializer;

public class Untapped implements ModInitializer {
  public static final String MOD_ID = "untapped";

  @Override
  public void onInitialize() {
    UntappedBlocks.initialize();
    UntappedBlockEntityType.initialize();
  }
}
