package com.github.unreference.untapped.world.level.block;

import com.github.unreference.untapped.world.level.block.entity.UntappedFrozenCauldronBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public final class UntappedFrozenCauldronBlock extends BaseEntityBlock {
  private static final VoxelShape CAULDRON_INNER_SHAPE = Block.column(12.0, 4.0, 16.0);
  private static final VoxelShape CAULDRON_WALLS_SHAPE =
      Util.make(
          () ->
              Shapes.join(
                  Shapes.block(),
                  Shapes.or(
                      CAULDRON_INNER_SHAPE,
                      Block.column(16.0, 8.0, 0.0, 3.0),
                      Block.column(8.0, 16.0, 0.0, 3.0),
                      Block.column(12.0, 0.0, 3.0)),
                  BooleanOp.ONLY_FIRST));

  public UntappedFrozenCauldronBlock(Properties properties) {
    super(properties);
  }

  private static void showParticles(Entity entity, int amount) {
    if (entity.level().isClientSide()) {
      final BlockState blockState = Blocks.ICE.defaultBlockState();

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

  private static void melt(ServerLevel serverLevel, BlockPos blockPos) {
    serverLevel.setBlockAndUpdate(blockPos, meltsInto());
  }

  private static BlockState meltsInto() {
    return Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3);
  }

  @Override
  protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
    return simpleCodec(UntappedFrozenCauldronBlock::new);
  }

  @Override
  public @NotNull BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new UntappedFrozenCauldronBlockEntity(blockPos, blockState);
  }

  @Override
  protected @NotNull RenderShape getRenderShape(BlockState blockState) {
    return RenderShape.MODEL;
  }

  @Override
  protected @NotNull VoxelShape getCollisionShape(
      BlockState blockState,
      BlockGetter blockGetter,
      BlockPos blockPos,
      CollisionContext collisionContext) {
    return CAULDRON_INNER_SHAPE;
  }

  @Override
  protected @NotNull VoxelShape getInteractionShape(
      BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
    return CAULDRON_INNER_SHAPE;
  }

  @Override
  protected @NotNull VoxelShape getEntityInsideCollisionShape(
      BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Entity entity) {
    return CAULDRON_WALLS_SHAPE;
  }

  @Override
  protected @NotNull ItemStack getCloneItemStack(
      LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean bl) {
    return new ItemStack(Blocks.CAULDRON);
  }

  @Override
  protected @NotNull VoxelShape getShape(
      BlockState blockState,
      BlockGetter blockGetter,
      BlockPos blockPos,
      CollisionContext collisionContext) {
    return CAULDRON_WALLS_SHAPE;
  }

  @Override
  public void fallOn(
      Level level, BlockState blockState, BlockPos blockPos, Entity entity, double d) {
    if (level.isClientSide()) {
      showParticles(entity, 5);
    }

    if (entity.causeFallDamage(d, 1.0f, level.damageSources().fall())) {
      entity.playSound(
          SoundType.GLASS.getFallSound(),
          SoundType.GLASS.getVolume() * 0.5f,
          SoundType.GLASS.getPitch() * 0.75f);
    }
  }

  @Override
  protected void randomTick(
      BlockState blockState,
      ServerLevel serverLevel,
      BlockPos blockPos,
      RandomSource randomSource) {
    if (serverLevel.getBrightness(LightLayer.BLOCK, blockPos) > 11 - blockState.getLightBlock()) {
      melt(serverLevel, blockPos);
    }
  }
}
