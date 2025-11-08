package com.github.unreference.underutilized.world.level.block.entity;

import com.github.unreference.underutilized.world.level.block.UnderutilizedBlocks;
import com.github.unreference.underutilized.world.level.block.UnderutilizedHoneyCauldronBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class UnderutilizedHoneyCauldronBlockEntity extends BlockEntity {
  private static final int TICKS_PER_CHECK = 40;
  private static final int MAX_LENGTH_TO_CAULDRON = 11;

  private int tickDelay = 0;

  public UnderutilizedHoneyCauldronBlockEntity(BlockPos blockPos, BlockState blockState) {
    super(UnderutilizedBlockEntityType.HONEY_CAULDRON, blockPos, blockState);
  }

  public static void tick(
      Level level,
      BlockPos blockPos,
      BlockState blockState,
      UnderutilizedHoneyCauldronBlockEntity honeyCauldronBlockEntity) {
    if (level.isClientSide()) {
      return;
    }

    honeyCauldronBlockEntity.tickDelay++;
    if (honeyCauldronBlockEntity.tickDelay < TICKS_PER_CHECK) {
      return;
    }

    honeyCauldronBlockEntity.tickDelay = 0;

    final int currentHoneyLevel = blockState.getValue(UnderutilizedHoneyCauldronBlock.HONEY_LEVEL);
    if (currentHoneyLevel == 3) {
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
          UnderutilizedBlocks.HONEY_CAULDRON
              .defaultBlockState()
              .setValue(UnderutilizedHoneyCauldronBlock.HONEY_LEVEL, 1),
          Block.UPDATE_ALL);
    } else if (cauldronBlockState.is(UnderutilizedBlocks.HONEY_CAULDRON)) {
      final int currentHoneyLevel =
          cauldronBlockState.getValue(UnderutilizedHoneyCauldronBlock.HONEY_LEVEL);
      if (currentHoneyLevel != 3) {
        level.setBlock(
            cauldronBlockPos,
            cauldronBlockState.setValue(
                UnderutilizedHoneyCauldronBlock.HONEY_LEVEL, currentHoneyLevel + 1),
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
