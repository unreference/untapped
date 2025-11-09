package com.github.unreference.untapped.core.cauldron;

import com.github.unreference.untapped.util.UntappedItemUtils;
import com.github.unreference.untapped.world.level.block.UntappedBlocks;
import com.github.unreference.untapped.world.level.block.UntappedHoneyCauldronBlock;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;

public final class UntappedCauldronInteraction {
  public static final CauldronInteraction.InteractionMap HONEY =
      CauldronInteraction.newInteractionMap("honey");

  private static void addHoneyCauldronInteractions() {
    CauldronInteraction.EMPTY
        .map()
        .put(Items.HONEY_BOTTLE, UntappedCauldronInteraction::fillHoneyInteraction);

    final Map<Item, CauldronInteraction> map = HONEY.map();
    CauldronInteraction.addDefaultInteractions(map);

    map.put(
        Items.GLASS_BOTTLE,
        ((blockState, level, blockPos, player, interactionHand, itemStack) -> {
          if (!level.isClientSide()) {
            final ItemStack honeyBottle = new ItemStack(Items.HONEY_BOTTLE);

            player.setItemInHand(
                interactionHand,
                UntappedItemUtils.convertItemInHand(player, itemStack, honeyBottle));
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));

            LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);
            level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS);
            level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
          }

          return InteractionResult.SUCCESS;
        }));

    map.put(Items.HONEY_BOTTLE, UntappedCauldronInteraction::fillHoneyInteraction);
  }

  public static InteractionResult fillHoneyInteraction(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      ItemStack itemStack) {
    if (blockState.is(UntappedBlocks.HONEY_CAULDRON)
        && blockState.getValue(UntappedHoneyCauldronBlock.LEVEL) == 3) {
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
        final IntegerProperty honeyLevel = UntappedHoneyCauldronBlock.LEVEL;
        final Integer currentHoneyLevel = blockState.getValue(honeyLevel);
        newBlockState = blockState.setValue(honeyLevel, currentHoneyLevel + 1);
      } else {
        newBlockState =
            UntappedBlocks.HONEY_CAULDRON
                .defaultBlockState()
                .setValue(UntappedHoneyCauldronBlock.LEVEL, 1);
      }

      level.setBlock(blockPos, newBlockState, Block.UPDATE_ALL);
      level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS);
    }

    return InteractionResult.SUCCESS;
  }

  private static void addCauldronInteractions() {
    addDyeableInteractions();
    addHoneyCauldronInteractions();
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
    addCauldronInteractions();
  }
}
