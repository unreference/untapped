package com.github.unreference.underutilized;

import com.github.unreference.underutilized.world.level.block.UnderutilizedBlocks;
import com.github.unreference.underutilized.world.level.block.entity.UnderutilizedBlockEntityType;
import net.fabricmc.api.ModInitializer;

public class Underutilized implements ModInitializer {
  public static final String MOD_ID = "underutilized";

  @Override
  public void onInitialize() {
    UnderutilizedBlocks.initialize();
    UnderutilizedBlockEntityType.initialize();
  }
}
