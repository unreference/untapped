package com.github.unreference.untapped.world.level.block;

import com.github.unreference.untapped.world.level.block.entity.UntappedBlockEntityType;
import com.github.unreference.untapped.world.level.block.entity.UntappedHoneyCauldronBlockEntity;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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

public final class UntappedHoneyCauldronBlock extends BaseEntityBlock {
  public static final IntegerProperty HONEY_LEVEL = BlockStateProperties.LEVEL_CAULDRON;
  public static final Map<Item, CauldronInteraction> HONEY_CAULDRON_INTERACTIONS =
      createInteractionMap();
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

  public UntappedHoneyCauldronBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.getStateDefinition().any().setValue(HONEY_LEVEL, 1));
  }

  private static Map<Item, CauldronInteraction> createInteractionMap() {
    final Map<Item, CauldronInteraction> map = new Object2ObjectOpenHashMap<>();

    map.put(
        Items.WATER_BUCKET,
        bucketHandler(
            () -> Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3),
            SoundEvents.BUCKET_EMPTY));

    map.put(
        Items.LAVA_BUCKET,
        bucketHandler(Blocks.LAVA_CAULDRON::defaultBlockState, SoundEvents.BUCKET_EMPTY_LAVA));

    map.put(
        Items.POWDER_SNOW_BUCKET,
        bucketHandler(
            () ->
                Blocks.POWDER_SNOW_CAULDRON
                    .defaultBlockState()
                    .setValue(LayeredCauldronBlock.LEVEL, 3),
            SoundEvents.BUCKET_EMPTY_POWDER_SNOW));

    map.put(
        Items.GLASS_BOTTLE,
        bottleHandler(
            (oldBlockState) -> {
              final int currentHoneyLevel = oldBlockState.getValue(HONEY_LEVEL);
              return (currentHoneyLevel > 1)
                  ? oldBlockState.setValue(HONEY_LEVEL, currentHoneyLevel - 1)
                  : Blocks.CAULDRON.defaultBlockState();
            },
            Items.HONEY_BOTTLE,
            Stats.USE_CAULDRON,
            SoundEvents.BOTTLE_FILL));

    map.put(
        Items.POTION,
        bottleHandler(
            (oldBlockState) ->
                Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 1),
            Items.GLASS_BOTTLE,
            Stats.USE_CAULDRON,
            SoundEvents.BOTTLE_EMPTY));

    map.put(Items.HONEY_BOTTLE, UntappedHoneyCauldronBlock::fillWithHoney);
    return map;
  }

  static CauldronInteraction bucketHandler(
      Supplier<BlockState> resultBlockStateSupplier, SoundEvent sound) {
    return ((blockState, level, blockPos, player, interactionHand, itemStack) -> {
      if (!level.isClientSide()) {
        final ItemStack bucket = new ItemStack(Items.BUCKET);
        if (!player.hasInfiniteMaterials()) {
          player.setItemInHand(interactionHand, bucket.copy());
        } else {
          if (!player.getInventory().contains(bucket.copy())) {
            if (!player.getInventory().add(bucket.copy())) {
              player.drop(bucket.copy(), false);
            }
          }
        }

        player.awardStat(Stats.FILL_CAULDRON);
        level.setBlock(blockPos, resultBlockStateSupplier.get(), Block.UPDATE_ALL);
        level.playSound(null, blockPos, sound, SoundSource.BLOCKS);
      }

      return InteractionResult.SUCCESS;
    });
  }

  static CauldronInteraction bottleHandler(
      Function<BlockState, BlockState> blockStateAfterUse,
      Item resultItem,
      ResourceLocation stat,
      SoundEvent sound) {
    return ((blockState, level, blockPos, player, interactionHand, itemStack) -> {
      final int currentHoneyLevel = blockState.getValue(HONEY_LEVEL);
      if (currentHoneyLevel == 0) {
        return InteractionResult.PASS;
      }

      if (!level.isClientSide()) {
        final ItemStack resultStack = new ItemStack(resultItem);

        if (!player.hasInfiniteMaterials()) {
          itemStack.shrink(1);
          if (itemStack.isEmpty()) {
            player.setItemInHand(interactionHand, resultStack);
          } else if (!player.getInventory().add(resultStack)) {
            player.drop(resultStack, false);
          }
        } else {
          if (!player.getInventory().contains(resultStack)) {
            if (!player.getInventory().add(resultStack)) {
              player.drop(resultStack, false);
            }
          }
        }

        player.awardStat(stat);

        final BlockState newBlockState = blockStateAfterUse.apply(blockState);
        level.setBlock(blockPos, newBlockState, Block.UPDATE_ALL);
        level.playSound(null, blockPos, sound, SoundSource.BLOCKS);
      }

      return InteractionResult.SUCCESS;
    });
  }

  public static InteractionResult fillWithHoney(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      ItemStack itemStack) {
    if (!level.isClientSide()) {
      final ItemStack glassBottle = new ItemStack(Items.GLASS_BOTTLE);

      if (!player.hasInfiniteMaterials()) {
        player.setItemInHand(interactionHand, glassBottle);
      } else {
        if (!player.getInventory().contains(glassBottle)) {
          if (!player.getInventory().add(glassBottle)) {
            player.drop(glassBottle, false);
          }
        }
      }

      player.awardStat(Stats.USE_CAULDRON);
      BlockState newBlockState;

      if (blockState.is(UntappedBlocks.HONEY_CAULDRON)) {
        final int currentLevel = blockState.getValue(HONEY_LEVEL);
        if (currentLevel == 3) {
          return InteractionResult.PASS;
        }

        newBlockState = blockState.setValue(HONEY_LEVEL, currentLevel + 1);
      } else {
        newBlockState = UntappedBlocks.HONEY_CAULDRON.defaultBlockState().setValue(HONEY_LEVEL, 1);
      }

      level.setBlock(blockPos, newBlockState, Block.UPDATE_ALL);
      level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS);
    }

    return InteractionResult.SUCCESS;
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
  protected @NotNull MapCodec<? extends UntappedHoneyCauldronBlock> codec() {
    return simpleCodec(UntappedHoneyCauldronBlock::new);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new UntappedHoneyCauldronBlockEntity(blockPos, blockState);
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
        UntappedBlockEntityType.HONEY_CAULDRON,
        UntappedHoneyCauldronBlockEntity::tick);
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
    final CauldronInteraction cauldronInteraction =
        HONEY_CAULDRON_INTERACTIONS.get(itemStack.getItem());
    if (cauldronInteraction != null) {
      return cauldronInteraction.interact(
          blockState, level, blockPos, player, interactionHand, itemStack);
    }

    return super.useItemOn(
        itemStack, blockState, level, blockPos, player, interactionHand, blockHitResult);
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
