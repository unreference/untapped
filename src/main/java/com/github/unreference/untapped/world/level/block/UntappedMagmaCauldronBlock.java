package com.github.unreference.untapped.world.level.block;

import com.github.unreference.untapped.core.cauldron.UntappedCauldronInteraction;
import com.github.unreference.untapped.world.level.block.state.properties.UntappedBlockStateProperties;
import com.mojang.serialization.MapCodec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public final class UntappedMagmaCauldronBlock extends UntappedAbstractFourLayeredCauldronBlock {
  public static final IntegerProperty LEVEL = UntappedBlockStateProperties.LEVEL_MAGMA_CAULDRON;
  public static final MapCodec<UntappedMagmaCauldronBlock> CODEC =
      simpleCodec(UntappedMagmaCauldronBlock::new);
  private static final double HEIGHT_PER_LEVEL = 3.0;

  private static final VoxelShape[] MAGMA_FILLED_SHAPES =
      Util.make(
          new VoxelShape[MAX_FILL_LEVEL],
          (shapes) -> {
            for (int i = 0; i < shapes.length; i++) {
              shapes[i] =
                  Shapes.or(Block.column(12.0, HEIGHT_PER_LEVEL, getPixelContentHeight(i + 1)));
            }
          });

  private static final VoxelShape[] COLLISION_SHAPES =
      Util.make(
          new VoxelShape[MAX_FILL_LEVEL],
          (shapes) -> {
            for (int i = 0; i < shapes.length; i++) {
              shapes[i] = Shapes.or(AbstractCauldronBlock.SHAPE, MAGMA_FILLED_SHAPES[i]);
            }
          });

  public UntappedMagmaCauldronBlock(Properties properties) {
    super(properties, UntappedCauldronInteraction.MAGMA);
    this.registerDefaultState(this.getStateDefinition().any().setValue(LEVEL, MIN_FILL_LEVEL));
  }

  public static void lowerFillLevel(BlockState blockState, Level level, BlockPos blockPos) {
    final int nextFillLevel = blockState.getValue(LEVEL) - 1;
    final BlockState newBlockState =
        nextFillLevel == 0
            ? Blocks.CAULDRON.defaultBlockState()
            : blockState.setValue(LEVEL, nextFillLevel);
    level.setBlockAndUpdate(blockPos, newBlockState);
    level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(newBlockState));
  }

  @Override
  protected IntegerProperty getLevelProperty() {
    return LEVEL;
  }

  @Override
  protected VoxelShape[] getLiquidShapes() {
    return MAGMA_FILLED_SHAPES;
  }

  @Override
  protected VoxelShape[] getCollisionShapes() {
    return COLLISION_SHAPES;
  }

  @Override
  protected @NotNull MapCodec<? extends UntappedAbstractFourLayeredCauldronBlock> codec() {
    return CODEC;
  }
}
