package com.github.unreference.untapped.world.level.block.state.properties;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public final class UntappedBlockStateProperties {
  public static int MAX_LEVEL_4 = 4;
  public static final IntegerProperty LEVEL_HONEY_CAULDRON =
      IntegerProperty.create("level", BlockStateProperties.MIN_LEVEL_CAULDRON, MAX_LEVEL_4);
  public static final IntegerProperty LEVEL_SLIME_CAULDRON =
      IntegerProperty.create("level", BlockStateProperties.MIN_LEVEL_CAULDRON, MAX_LEVEL_4);
  public static final IntegerProperty LEVEL_MAGMA_CAULDRON =
      IntegerProperty.create("level", BlockStateProperties.MIN_LEVEL_CAULDRON, MAX_LEVEL_4);
}
