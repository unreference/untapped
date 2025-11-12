package com.github.unreference.untapped.world.level.block.entity;

import com.github.unreference.untapped.world.level.block.UntappedBlocks;
import com.github.unreference.untapped.world.level.block.UntappedHoneyCauldronBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class UntappedHoneyCauldronBlockEntity extends BlockEntity {
  private static final int TICKS_PER_CHECK = 40;
  private static final int MAX_LENGTH_TO_CAULDRON = 11;

  private int tickDelay = 0;

  public UntappedHoneyCauldronBlockEntity(BlockPos blockPos, BlockState blockState) {
    super(UntappedBlockEntityType.HONEY_CAULDRON, blockPos, blockState);
  }

  public static void tick(
      Level level,
      BlockPos blockPos,
      BlockState blockState,
      UntappedHoneyCauldronBlockEntity honeyCauldronBlockEntity) {
    if (level.isClientSide()) {
      return;
    }

    honeyCauldronBlockEntity.tickDelay++;
    if (honeyCauldronBlockEntity.tickDelay < TICKS_PER_CHECK) {
      return;
    }

    honeyCauldronBlockEntity.tickDelay = 0;

    final int currentFillLevel = blockState.getValue(UntappedHoneyCauldronBlock.LEVEL);
    if (currentFillLevel == UntappedHoneyCauldronBlock.MAX_FILL_LEVEL) {
      return;
    }

    final BlockPos beehiveBlockPos = searchForHive(level, blockPos);
    if (beehiveBlockPos != null) {
      transferHoney(level, blockPos, blockState, beehiveBlockPos);
    }
  }

  public static void transferHoney(
      Level level,
      BlockPos cauldronBlockPos,
      BlockState cauldronBlockState,
      BlockPos beehiveBlockPos) {
    if (cauldronBlockState.is(Blocks.CAULDRON)) {
      level.setBlock(
          cauldronBlockPos,
          UntappedBlocks.HONEY_CAULDRON
              .defaultBlockState()
              .setValue(UntappedHoneyCauldronBlock.LEVEL, 1),
          Block.UPDATE_ALL);
    } else if (cauldronBlockState.is(UntappedBlocks.HONEY_CAULDRON)) {
      final int currentHoneyLevel = cauldronBlockState.getValue(UntappedHoneyCauldronBlock.LEVEL);
      if (currentHoneyLevel != UntappedHoneyCauldronBlock.MAX_FILL_LEVEL) {
        level.setBlock(
            cauldronBlockPos,
            cauldronBlockState.setValue(UntappedHoneyCauldronBlock.LEVEL, currentHoneyLevel + 1),
            Block.UPDATE_ALL);
      }
    } else {
      return;
    }

    final BlockState beehiveBlockState = level.getBlockState(beehiveBlockPos);
    if (beehiveBlockState.hasProperty(BlockStateProperties.LEVEL_HONEY)) {
      level.setBlock(
          beehiveBlockPos,
          beehiveBlockState.setValue(BlockStateProperties.LEVEL_HONEY, 0),
          Block.UPDATE_ALL);
    }
  }

  public static BlockPos searchForHive(Level level, BlockPos blockPos) {
    for (int blocksChecked = 1; blocksChecked <= MAX_LENGTH_TO_CAULDRON; blocksChecked++) {
      final BlockPos checkedBlockPos = blockPos.above(blocksChecked);
      final BlockState checkedBlockState = level.getBlockState(checkedBlockPos);

      if (checkedBlockState.is(BlockTags.BEEHIVES)
          && checkedBlockState.hasProperty(BlockStateProperties.LEVEL_HONEY)
          && checkedBlockState.getValue(BlockStateProperties.LEVEL_HONEY) == 5) {
        return checkedBlockPos;
      }

      if (checkedBlockState.isRedstoneConductor(level, checkedBlockPos)) {
        return null;
      }
    }

    return null;
  }
}
