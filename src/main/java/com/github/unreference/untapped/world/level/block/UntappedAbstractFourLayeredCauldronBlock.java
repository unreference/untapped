package com.github.unreference.untapped.world.level.block;

import com.github.unreference.untapped.world.level.block.state.properties.UntappedBlockStateProperties;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public abstract class UntappedAbstractFourLayeredCauldronBlock extends AbstractCauldronBlock {
  public static final int MIN_FILL_LEVEL = BlockStateProperties.MIN_LEVEL_CAULDRON;
  public static final int MAX_FILL_LEVEL = UntappedBlockStateProperties.MAX_LEVEL_4;
  private static final int BASE_CONTENT_HEIGHT = 3;
  private static final double HEIGHT_PER_LEVEL = 3.0;

  public UntappedAbstractFourLayeredCauldronBlock(
      Properties properties, CauldronInteraction.InteractionMap interactionMap) {
    super(properties, interactionMap);
  }

  protected static double getPixelContentHeight(int content) {
    return BASE_CONTENT_HEIGHT + content * HEIGHT_PER_LEVEL;
  }

  protected abstract IntegerProperty getLevelProperty();

  protected abstract VoxelShape[] getLiquidShapes();

  protected abstract VoxelShape[] getCollisionShapes();

  @Override
  protected abstract @NotNull MapCodec<? extends UntappedAbstractFourLayeredCauldronBlock> codec();

  @Override
  protected double getContentHeight(BlockState blockState) {
    return getPixelContentHeight(blockState.getValue(this.getLevelProperty())) / 16.0;
  }

  @Override
  protected @NotNull VoxelShape getCollisionShape(
      BlockState blockState,
      BlockGetter blockGetter,
      BlockPos blockPos,
      CollisionContext collisionContext) {
    return this.getCollisionShapes()[blockState.getValue(this.getLevelProperty()) - 1];
  }

  @Override
  protected @NotNull VoxelShape getShape(
      BlockState blockState,
      BlockGetter blockGetter,
      BlockPos blockPos,
      CollisionContext collisionContext) {
    return this.getCollisionShapes()[blockState.getValue(this.getLevelProperty()) - 1];
  }

  @Override
  protected @NotNull VoxelShape getEntityInsideCollisionShape(
      BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Entity entity) {
    return this.getLiquidShapes()[blockState.getValue(this.getLevelProperty()) - 1];
  }

  @Override
  protected @NotNull ItemStack getCloneItemStack(
      LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean bl) {
    return new ItemStack(Items.CAULDRON);
  }

  @Override
  public boolean isFull(BlockState blockState) {
    return blockState.getValue(this.getLevelProperty()) == MAX_FILL_LEVEL;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(this.getLevelProperty());
  }

  @Override
  protected @NotNull VoxelShape getInteractionShape(
      BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
    return this.getCollisionShapes()[blockState.getValue(this.getLevelProperty()) - 1];
  }
}
