package com.github.unreference.untapped.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public final class UntappedFrozenCauldronBlock extends AbstractCauldronBlock {
  private static final MapCodec<UntappedFrozenCauldronBlock> CODEC =
      simpleCodec(UntappedFrozenCauldronBlock::new);

  private static final VoxelShape SHAPE_INSIDE = Block.column(12.0, 4.0, 16.0);

  public UntappedFrozenCauldronBlock(Properties properties) {
    super(properties, CauldronInteraction.EMPTY);
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
  protected @NotNull RenderShape getRenderShape(BlockState blockState) {
    return RenderShape.MODEL;
  }

  @Override
  protected @NotNull VoxelShape getCollisionShape(
      BlockState blockState,
      BlockGetter blockGetter,
      BlockPos blockPos,
      CollisionContext collisionContext) {
    return SHAPE_INSIDE;
  }

  @Override
  protected @NotNull VoxelShape getInteractionShape(
      BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
    return SHAPE_INSIDE;
  }

  @Override
  public boolean isFull(BlockState blockState) {
    return true;
  }

  @Override
  protected @NotNull VoxelShape getEntityInsideCollisionShape(
      BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Entity entity) {
    return SHAPE;
  }

  @Override
  protected @NotNull ItemStack getCloneItemStack(
      LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean bl) {
    return new ItemStack(Blocks.CAULDRON);
  }

  @Override
  protected @NotNull MapCodec<? extends AbstractCauldronBlock> codec() {
    return CODEC;
  }

  @Override
  protected @NotNull VoxelShape getShape(
      BlockState blockState,
      BlockGetter blockGetter,
      BlockPos blockPos,
      CollisionContext collisionContext) {
    return SHAPE;
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
