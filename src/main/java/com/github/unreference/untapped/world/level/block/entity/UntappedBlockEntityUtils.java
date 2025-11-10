package com.github.unreference.untapped.world.level.block.entity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class UntappedBlockEntityUtils {
  public static <E extends BlockEntity, A extends BlockEntity>
      BlockEntityTicker<A> createTickerHelper(
          BlockEntityType<A> blockEntityType,
          BlockEntityType<E> blockEntityType2,
          BlockEntityTicker<? super E> blockEntityTicker) {
    return blockEntityType2 == blockEntityType ? (BlockEntityTicker<A>) blockEntityTicker : null;
  }
}
