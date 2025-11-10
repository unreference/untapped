package com.github.unreference.untapped.core.cauldron;

import com.github.unreference.untapped.world.item.UntappedItemUtils;
import com.github.unreference.untapped.world.level.block.UntappedBlocks;
import com.github.unreference.untapped.world.level.block.UntappedPotionCauldronBlock;
import com.github.unreference.untapped.world.level.block.entity.UntappedPotionCauldronBlockEntity;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;

public final class UntappedCauldronInteraction {
  public static final CauldronInteraction.InteractionMap HONEY =
      CauldronInteraction.newInteractionMap("honey");
  public static final CauldronInteraction.InteractionMap POTION =
      CauldronInteraction.newInteractionMap("potion");

  private static void addHoneyCauldronInteractions() {
    final Map<Item, CauldronInteraction> map = HONEY.map();
    CauldronInteraction.addDefaultInteractions(map);
    CauldronInteraction.EMPTY
        .map()
        .put(Items.HONEY_BOTTLE, UntappedCauldronInteraction::fillHoneyInteraction);
    map.put(Items.GLASS_BOTTLE, UntappedCauldronInteraction::takeHoneyInteraction);
    map.put(Items.HONEY_BOTTLE, UntappedCauldronInteraction::fillHoneyInteraction);
  }

  private static InteractionResult takeHoneyInteraction(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      ItemStack itemStack) {
    if (!level.isClientSide()) {
      final ItemStack honeyBottle = new ItemStack(Items.HONEY_BOTTLE);

      player.setItemInHand(
          interactionHand, UntappedItemUtils.convertItemInHand(player, itemStack, honeyBottle));
      player.awardStat(Stats.USE_CAULDRON);
      player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));

      LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);
      level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS);
      level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
    }

    return InteractionResult.SUCCESS;
  }

  public static InteractionResult fillHoneyInteraction(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      ItemStack itemStack) {
    if (blockState.is(UntappedBlocks.HONEY_CAULDRON)
        && blockState.getValue(LayeredCauldronBlock.LEVEL) == 3) {
      return InteractionResult.PASS;
    }

    if (!level.isClientSide()) {
      final ItemStack glassBottle = new ItemStack(Items.GLASS_BOTTLE);

      player.setItemInHand(
          interactionHand, UntappedItemUtils.convertItemInHand(player, itemStack, glassBottle));
      player.awardStat(Stats.USE_CAULDRON);
      player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
      BlockState newBlockState;

      if (blockState.is(UntappedBlocks.HONEY_CAULDRON)) {
        final IntegerProperty honeyLevel = LayeredCauldronBlock.LEVEL;
        final Integer currentHoneyLevel = blockState.getValue(honeyLevel);
        newBlockState = blockState.setValue(honeyLevel, currentHoneyLevel + 1);
      } else {
        newBlockState =
            UntappedBlocks.HONEY_CAULDRON
                .defaultBlockState()
                .setValue(LayeredCauldronBlock.LEVEL, 1);
      }

      level.setBlock(blockPos, newBlockState, Block.UPDATE_ALL);
      level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS);
    }

    return InteractionResult.SUCCESS;
  }

  private static void addPotionCauldronInteractions() {
    final Map<Item, CauldronInteraction> map = POTION.map();
    CauldronInteraction.addDefaultInteractions(map);
    CauldronInteraction.EMPTY
        .map()
        .put(Items.POTION, UntappedCauldronInteraction::fillPotionInteraction);
    CauldronInteraction.EMPTY
        .map()
        .put(Items.LINGERING_POTION, UntappedCauldronInteraction::fillPotionInteraction);
    CauldronInteraction.EMPTY
        .map()
        .put(Items.SPLASH_POTION, UntappedCauldronInteraction::fillPotionInteraction);
    map.put(Items.POTION, UntappedCauldronInteraction::fillPotionInteraction);
    map.put(Items.LINGERING_POTION, UntappedCauldronInteraction::fillPotionInteraction);
    map.put(Items.SPLASH_POTION, UntappedCauldronInteraction::fillPotionInteraction);
    map.put(Items.GLASS_BOTTLE, UntappedCauldronInteraction::takePotionInteraction);
    map.put(Items.ARROW, UntappedCauldronInteraction::dipArrowInteraction);
  }

  private static InteractionResult dipArrowInteraction(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      ItemStack itemStack) {
    if (!level.isClientSide()) {
      if (level.getBlockEntity(blockPos)
          instanceof UntappedPotionCauldronBlockEntity potionCauldronBlockEntity) {
        if (potionCauldronBlockEntity.getEffect().isEmpty()) {
          return InteractionResult.PASS;
        }

        final int cauldronLevel = blockState.getValue(LayeredCauldronBlock.LEVEL);
        final int maxArrowsPerLevel = (cauldronLevel == 3) ? 64 : (cauldronLevel == 2) ? 32 : 16;
        final int arrowsToConvert = Math.min(itemStack.getCount(), maxArrowsPerLevel);

        if (arrowsToConvert <= 0) {
          return InteractionResult.PASS;
        }

        final ItemStack tippedArrow = new ItemStack(Items.TIPPED_ARROW, arrowsToConvert);
        potionCauldronBlockEntity.saveEffectToItem(tippedArrow);

        player.setItemInHand(
            interactionHand,
            UntappedItemUtils.convertItemInHand(player, itemStack, tippedArrow, arrowsToConvert));
        player.awardStat(Stats.USE_CAULDRON);
        player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));

        final int consumedLevels = Math.min(cauldronLevel, (arrowsToConvert + 15) / 16);
        final int newLevel = cauldronLevel - consumedLevels;

        if (newLevel > 0) {
          level.setBlock(
              blockPos,
              blockState.setValue(LayeredCauldronBlock.LEVEL, newLevel),
              Block.UPDATE_ALL);
        } else {
          level.setBlock(blockPos, Blocks.CAULDRON.defaultBlockState(), Block.UPDATE_ALL);
        }
      }
    }

    return InteractionResult.SUCCESS;
  }

  private static InteractionResult takePotionInteraction(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      ItemStack itemStack) {
    if (!level.isClientSide()) {
      if (level.getBlockEntity(blockPos)
          instanceof UntappedPotionCauldronBlockEntity potionCauldronBlockEntity) {
        if (potionCauldronBlockEntity.getEffect().isEmpty()) {
          return InteractionResult.PASS;
        }

        final ItemStack newPotionStack = new ItemStack(Items.POTION);
        potionCauldronBlockEntity.saveEffectToItem(newPotionStack);

        player.setItemInHand(
            interactionHand,
            UntappedItemUtils.convertItemInHand(player, itemStack, newPotionStack));
        player.awardStat(Stats.USE_CAULDRON);
        player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));

        UntappedPotionCauldronBlock.lowerFillLevel(blockState, level, blockPos);
        level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS);
        level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
      }
    }

    return InteractionResult.SUCCESS;
  }

  private static InteractionResult fillPotionInteraction(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      ItemStack itemStack) {
    final PotionContents potionContents = itemStack.get(DataComponents.POTION_CONTENTS);
    if (potionContents == null || potionContents == PotionContents.EMPTY) {
      return InteractionResult.PASS;
    }

    final Optional<Holder<Potion>> incomingPotionOptional = potionContents.potion();
    if (incomingPotionOptional.isEmpty()) {
      return InteractionResult.PASS;
    }

    if (incomingPotionOptional.get().value().getEffects().isEmpty()) {
      return InteractionResult.PASS;
    }

    if (blockState.is(UntappedBlocks.POTION_CAULDRON)) {
      if (level.getBlockEntity(blockPos)
          instanceof UntappedPotionCauldronBlockEntity potionCauldronBlockEntity) {
        final Optional<Holder<Potion>> existingPotion = potionCauldronBlockEntity.getPotion();

        if (existingPotion.isPresent()
            && !existingPotion.get().equals(incomingPotionOptional.get())) {
          return InteractionResult.PASS;
        }

        if (blockState.getValue(LayeredCauldronBlock.LEVEL) == 3) {
          return InteractionResult.PASS;
        }
      }
    }

    if (!level.isClientSide()) {
      player.setItemInHand(
          interactionHand,
          UntappedItemUtils.convertItemInHand(
              player, itemStack, new ItemStack(Items.GLASS_BOTTLE)));
      player.awardStat(Stats.USE_CAULDRON);
      player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));

      final int newLevel =
          blockState.is(UntappedBlocks.POTION_CAULDRON)
              ? blockState.getValue(LayeredCauldronBlock.LEVEL) + 1
              : 1;

      if (!blockState.is(UntappedBlocks.POTION_CAULDRON)) {
        level.setBlock(
            blockPos,
            UntappedBlocks.POTION_CAULDRON
                .defaultBlockState()
                .setValue(LayeredCauldronBlock.LEVEL, newLevel),
            Block.UPDATE_ALL);
      } else {
        level.setBlock(
            blockPos, blockState.setValue(LayeredCauldronBlock.LEVEL, newLevel), Block.UPDATE_ALL);
      }

      if (level.getBlockEntity(blockPos)
          instanceof UntappedPotionCauldronBlockEntity potionCauldronBlockEntity) {
        potionCauldronBlockEntity.setPotionContents(potionContents);
      }

      level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS);
    }

    return InteractionResult.SUCCESS;
  }

  private static void addDyeableInteractions() {
    final Map<Item, CauldronInteraction> water = CauldronInteraction.WATER.map();

    if (water instanceof Object2ObjectOpenHashMap<Item, CauldronInteraction> map) {
      final CauldronInteraction washing =
          (blockState, level, blockPos, player, interactionHand, itemStack) -> {
            final Item itemToWash = itemStack.getItem();
            Item uncoloredResultItem = null;

            if (UntappedCauldronWashableHolder.WASHABLE_GLAZED_TERRACOTTA.containsKey(itemToWash)) {
              uncoloredResultItem =
                  UntappedCauldronWashableHolder.WASHABLE_GLAZED_TERRACOTTA.get(itemToWash);
            }

            if (uncoloredResultItem == null) {
              for (Map.Entry<TagKey<Item>, Item> entry :
                  UntappedCauldronWashableHolder.WASHABLE.entrySet()) {
                final TagKey<Item> washable = entry.getKey();
                final Item uncoloredItem = entry.getValue();

                if (itemStack.is(washable) && itemStack.getItem() != uncoloredItem) {
                  uncoloredResultItem = uncoloredItem;
                  break;
                }
              }
            }

            if (uncoloredResultItem != null) {
              if (!level.isClientSide()) {
                final ItemStack washed = itemStack.transmuteCopy(uncoloredResultItem, 1);
                player.setItemInHand(
                    interactionHand,
                    UntappedItemUtils.convertItemInHand(player, itemStack, washed));
                LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);
              }

              return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
          };

      map.defaultReturnValue(washing);
    }
  }

  public static void initialize() {
    addDyeableInteractions();
    addHoneyCauldronInteractions();
    addPotionCauldronInteractions();
  }
}
