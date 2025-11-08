package com.github.unreference.untapped.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class UntappedFrozenCauldronBlockEntity extends BlockEntity {
  public UntappedFrozenCauldronBlockEntity(BlockPos blockPos, BlockState blockState) {
    super(UntappedBlockEntityType.FROZEN_CAULDRON, blockPos, blockState);
  }
}
