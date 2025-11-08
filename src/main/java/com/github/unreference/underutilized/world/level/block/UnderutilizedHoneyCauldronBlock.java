package com.github.unreference.underutilized.world.level.block;

import com.github.unreference.underutilized.world.level.block.entity.UnderutilizedBlockEntityType;
import com.github.unreference.underutilized.world.level.block.entity.UnderutilizedHoneyCauldronBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UnderutilizedHoneyCauldronBlock extends BaseEntityBlock {
  public static final IntegerProperty HONEY_LEVEL = BlockStateProperties.LEVEL_CAULDRON;

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

  private static final VoxelShape[] HONEY_LEVEL_SHAPES =
      Util.make(
          new VoxelShape[4],
          shapes -> {
            shapes[0] = Shapes.empty();
            shapes[1] = Block.column(12.0, 4.0, 9.0);
            shapes[2] = Block.column(12.0, 4.0, 12.0);
            shapes[3] = Block.column(12.0, 4.0, 15.0);
          });

  public UnderutilizedHoneyCauldronBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.getStateDefinition().any().setValue(HONEY_LEVEL, 1));
  }

  private static void showParticles(Entity entity, int amount) {
    if (entity.level().isClientSide()) {
      final BlockState blockState = UnderutilizedBlocks.HONEY_CAULDRON.defaultBlockState();

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

  public static InteractionResult fillWithHoney(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand) {
    if (!level.isClientSide()) {
      if (!player.hasInfiniteMaterials()) {
        player.setItemInHand(interactionHand, new ItemStack(Items.GLASS_BOTTLE));
      }

      player.awardStat(Stats.USE_CAULDRON);
      BlockState newBlockState;

      if (blockState.is(UnderutilizedBlocks.HONEY_CAULDRON)) {
        final int currentLevel = blockState.getValue(HONEY_LEVEL);
        if (currentLevel == 3) {
          return InteractionResult.PASS;
        }

        newBlockState = blockState.setValue(HONEY_LEVEL, currentLevel + 1);
      } else {
        newBlockState =
            UnderutilizedBlocks.HONEY_CAULDRON.defaultBlockState().setValue(HONEY_LEVEL, 1);
      }

      level.setBlock(blockPos, newBlockState, Block.UPDATE_ALL);
      level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS);
    }

    return InteractionResult.SUCCESS;
  }

  @Override
  protected @NotNull MapCodec<? extends UnderutilizedHoneyCauldronBlock> codec() {
    return simpleCodec(UnderutilizedHoneyCauldronBlock::new);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new UnderutilizedHoneyCauldronBlockEntity(blockPos, blockState);
  }

  @Override
  protected @NotNull RenderShape getRenderShape(BlockState blockState) {
    return RenderShape.MODEL;
  }

  @Override
  public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
    if (level.isClientSide()) {
      return null;
    }

    return createTickerHelper(
        blockEntityType,
        UnderutilizedBlockEntityType.HONEY_CAULDRON,
        UnderutilizedHoneyCauldronBlockEntity::tick);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(HONEY_LEVEL);
  }

  @Override
  protected @NotNull InteractionResult useItemOn(
      ItemStack itemStack,
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      BlockHitResult blockHitResult) {
    if (itemStack.isEmpty()) {
      return InteractionResult.PASS;
    }

    final int currentHoneyLevel = blockState.getValue(HONEY_LEVEL);

    if (itemStack.is(Items.WATER_BUCKET)) {
      if (!level.isClientSide()) {
        if (!player.hasInfiniteMaterials()) {
          player.setItemInHand(interactionHand, new ItemStack(Items.BUCKET));
        }

        player.awardStat(Stats.FILL_CAULDRON);
        level.setBlock(
            blockPos,
            Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3),
            Block.UPDATE_ALL);
        level.playSound(null, blockPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS);
      }

      return InteractionResult.SUCCESS;
    }

    if (itemStack.is(Items.LAVA_BUCKET)) {
      if (!level.isClientSide()) {
        if (!player.hasInfiniteMaterials()) {
          player.setItemInHand(interactionHand, new ItemStack(Items.BUCKET));
        }

        player.awardStat(Stats.FILL_CAULDRON);
        level.setBlock(blockPos, Blocks.LAVA_CAULDRON.defaultBlockState(), Block.UPDATE_ALL);
        level.playSound(null, blockPos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS);
      }

      return InteractionResult.SUCCESS;
    }

    if (itemStack.is(Items.POWDER_SNOW_BUCKET)) {
      if (!level.isClientSide()) {
        if (!player.hasInfiniteMaterials()) {
          player.setItemInHand(interactionHand, new ItemStack(Items.BUCKET));
        }

        player.awardStat(Stats.FILL_CAULDRON);
        level.setBlock(
            blockPos,
            Blocks.POWDER_SNOW_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3),
            Block.UPDATE_ALL);
        level.playSound(null, blockPos, SoundEvents.BUCKET_EMPTY_POWDER_SNOW, SoundSource.BLOCKS);
      }

      return InteractionResult.SUCCESS;
    }

    if (itemStack.is(Items.GLASS_BOTTLE)) {
      if (currentHoneyLevel > 0) {
        if (!level.isClientSide()) {
          if (currentHoneyLevel > 1) {
            level.setBlock(
                blockPos,
                blockState.setValue(HONEY_LEVEL, currentHoneyLevel - 1),
                Block.UPDATE_ALL);
          } else {
            level.setBlock(blockPos, Blocks.CAULDRON.defaultBlockState(), Block.UPDATE_ALL);
          }

          player.awardStat(Stats.USE_CAULDRON);

          if (!player.hasInfiniteMaterials()) {
            itemStack.shrink(1);
          }

          if (itemStack.isEmpty()) {
            player.setItemInHand(interactionHand, new ItemStack(Items.HONEY_BOTTLE));
          } else if (!player.getInventory().add(new ItemStack(Items.HONEY_BOTTLE))) {
            player.drop(new ItemStack(Items.HONEY_BOTTLE), false);
          }

          level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS);
        }

        return InteractionResult.SUCCESS;
      }

      return InteractionResult.PASS;
    }

    if (itemStack.is(Items.HONEY_BOTTLE)) {
      return fillWithHoney(blockState, level, blockPos, player, interactionHand);
    }

    return super.useItemOn(
        itemStack, blockState, level, blockPos, player, interactionHand, blockHitResult);
  }

  @Override
  public void fallOn(
      Level level, BlockState blockState, BlockPos blockPos, Entity entity, double d) {
    entity.playSound(SoundEvents.HONEY_BLOCK_SLIDE);

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
  protected @NotNull VoxelShape getCollisionShape(
      BlockState blockState,
      BlockGetter blockGetter,
      BlockPos blockPos,
      CollisionContext collisionContext) {
    return Shapes.or(CAULDRON_WALLS_SHAPE, HONEY_LEVEL_SHAPES[blockState.getValue(HONEY_LEVEL)]);
  }

  @Override
  protected @NotNull VoxelShape getShape(
      BlockState blockState,
      BlockGetter blockGetter,
      BlockPos blockPos,
      CollisionContext collisionContext) {
    return Shapes.or(CAULDRON_WALLS_SHAPE, HONEY_LEVEL_SHAPES[blockState.getValue(HONEY_LEVEL)]);
  }

  @Override
  protected @NotNull VoxelShape getInteractionShape(
      BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
    return CAULDRON_WALLS_SHAPE;
  }

  @Override
  protected @NotNull ItemStack getCloneItemStack(
      LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean bl) {
    return new ItemStack(Blocks.CAULDRON);
  }
}
