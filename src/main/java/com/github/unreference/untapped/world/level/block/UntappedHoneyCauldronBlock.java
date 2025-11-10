package com.github.unreference.untapped.world.level.block;

import com.github.unreference.untapped.core.cauldron.UntappedCauldronInteraction;
import com.github.unreference.untapped.world.level.block.entity.UntappedBlockEntityType;
import com.github.unreference.untapped.world.level.block.entity.UntappedBlockEntityUtils;
import com.github.unreference.untapped.world.level.block.entity.UntappedHoneyCauldronBlockEntity;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UntappedHoneyCauldronBlock extends LayeredCauldronBlock implements EntityBlock {
  private static final int BASE_CONTENT_HEIGHT = 6;
  private static final double HEIGHT_PER_LEVEL = 3.0;

  private static final VoxelShape[] HONEY_FILLED_SHAPES =
      Util.make(
          () ->
              Block.boxes(
                  2,
                  i ->
                      Shapes.or(
                          AbstractCauldronBlock.SHAPE,
                          Block.column(12.0, 4.0, getPixelContentHeight(i + 1)))));

  public UntappedHoneyCauldronBlock(Properties properties) {
    super(Biome.Precipitation.NONE, UntappedCauldronInteraction.HONEY, properties);
    this.registerDefaultState(this.getStateDefinition().any().setValue(LEVEL, 1));
  }

  private static double getPixelContentHeight(int content) {
    return BASE_CONTENT_HEIGHT + content * HEIGHT_PER_LEVEL;
  }

  private static void showParticles(Entity entity, int amount) {
    if (entity.level().isClientSide()) {
      final BlockState blockState = Blocks.HONEY_BLOCK.defaultBlockState();

      for (int particles = 0; particles < amount; particles++) {
        entity
            .level()
            .addParticle(
                new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                0.0,
                0.0,
                0.0);
      }
    }
  }

  @Override
  protected boolean canReceiveStalactiteDrip(Fluid fluid) {
    return false;
  }

  @Override
  protected double getContentHeight(BlockState blockState) {
    return getPixelContentHeight(blockState.getValue(LEVEL)) / 16.0;
  }

  @Override
  public @NotNull BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new UntappedHoneyCauldronBlockEntity(blockPos, blockState);
  }

  @Override
  protected @NotNull VoxelShape getCollisionShape(
      BlockState blockState,
      BlockGetter blockGetter,
      BlockPos blockPos,
      CollisionContext collisionContext) {
    return Shapes.or(
        AbstractCauldronBlock.SHAPE, HONEY_FILLED_SHAPES[blockState.getValue(LEVEL) - 1]);
  }

  @Override
  protected @NotNull VoxelShape getShape(
      BlockState blockState,
      BlockGetter blockGetter,
      BlockPos blockPos,
      CollisionContext collisionContext) {
    return Shapes.or(
        AbstractCauldronBlock.SHAPE, HONEY_FILLED_SHAPES[blockState.getValue(LEVEL) - 1]);
  }

  @Override
  protected @NotNull VoxelShape getEntityInsideCollisionShape(
      BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Entity entity) {
    return HONEY_FILLED_SHAPES[blockState.getValue(LEVEL) - 1];
  }

  @Override
  protected void entityInside(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Entity entity,
      InsideBlockEffectApplier insideBlockEffectApplier,
      boolean bl) {
    // TODO: Maybe add some honey block properties here
  }

  @Override
  public void fallOn(
      Level level, BlockState blockState, BlockPos blockPos, Entity entity, double d) {
    entity.playSound(SoundEvents.HONEY_BLOCK_FALL);

    if (level.isClientSide()) {
      showParticles(entity, 5);
    }

    if (entity.causeFallDamage(d, 0.2f, level.damageSources().fall())) {
      entity.playSound(
          SoundType.HONEY_BLOCK.getFallSound(),
          SoundType.HONEY_BLOCK.getVolume() * 0.5f,
          SoundType.HONEY_BLOCK.getPitch() * 0.75f);
    }
  }

  @Override
  public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
    return UntappedBlockEntityUtils.createTickerHelper(
        blockEntityType,
        UntappedBlockEntityType.HONEY_CAULDRON,
        UntappedHoneyCauldronBlockEntity::tick);
  }

  @Override
  protected @NotNull ItemStack getCloneItemStack(
      LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean bl) {
    return new ItemStack(Items.CAULDRON);
  }
}
