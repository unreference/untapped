package com.github.unreference.untapped.world.level.block;

import com.github.unreference.untapped.core.cauldron.UntappedCauldronInteraction;
import com.github.unreference.untapped.world.level.block.entity.UntappedDyedWaterCauldronEntity;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public final class UntappedDyedWaterCauldronBlock extends LayeredCauldronBlock
    implements EntityBlock {
  private static final int BASE_CONTENT_HEIGHT = 6;
  private static final double HEIGHT_PER_LEVEL = 3.0;
  private static final VoxelShape[] WATER_FILLED_SHAPES =
      Util.make(
          () ->
              Block.boxes(
                  2,
                  i ->
                      Shapes.or(
                          AbstractCauldronBlock.SHAPE,
                          Block.column(12.0, 4.0, getPixelContentHeight(i + 1)))));

  public UntappedDyedWaterCauldronBlock(Properties properties) {
    super(Biome.Precipitation.RAIN, UntappedCauldronInteraction.WATER_DYED, properties);
  }

  private static double getPixelContentHeight(int content) {
    return BASE_CONTENT_HEIGHT + content * HEIGHT_PER_LEVEL;
  }

  @Override
  protected double getContentHeight(BlockState blockState) {
    return getPixelContentHeight(blockState.getValue(LEVEL)) / 16.0;
  }

  @Override
  protected @NotNull VoxelShape getEntityInsideCollisionShape(
      BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Entity entity) {
    return WATER_FILLED_SHAPES[blockState.getValue(LEVEL) - 1];
  }

  @Override
  protected @NotNull ItemStack getCloneItemStack(
      LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean bl) {
    return new ItemStack(Items.CAULDRON);
  }

  @Override
  protected boolean canReceiveStalactiteDrip(Fluid fluid) {
    return true;
  }

  @Override
  public @NotNull BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new UntappedDyedWaterCauldronEntity(blockPos, blockState);
  }
}
