package com.github.unreference.untapped.core.cauldron;

import com.github.unreference.untapped.world.item.UntappedItemUtils;
import com.github.unreference.untapped.world.level.block.*;
import com.github.unreference.untapped.world.level.block.entity.UntappedDyedWaterCauldronEntity;
import com.github.unreference.untapped.world.level.block.entity.UntappedPotionCauldronBlockEntity;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ARGB;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.DyedItemColor;
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
  public static final CauldronInteraction.InteractionMap WATER_DYED =
      CauldronInteraction.newInteractionMap("water_dyed");
  public static final CauldronInteraction.InteractionMap SLIME =
      CauldronInteraction.newInteractionMap("slime");
  public static final CauldronInteraction.InteractionMap MAGMA =
      CauldronInteraction.newInteractionMap("magma");

  // -------------------------------------------------------------------------------------------------------------------
  // Honey Cauldron
  // -------------------------------------------------------------------------------------------------------------------

  private static void addHoneyCauldronInteractions() {
    final Map<Item, CauldronInteraction> map = HONEY.map();
    CauldronInteraction.addDefaultInteractions(map);
    CauldronInteraction.EMPTY
        .map()
        .put(Items.HONEY_BOTTLE, UntappedCauldronInteraction::fillHoneyInteraction);
    map.put(Items.GLASS_BOTTLE, UntappedCauldronInteraction::takeHoneyInteraction);
    map.put(Items.HONEY_BOTTLE, UntappedCauldronInteraction::fillHoneyInteraction);
    map.put(Items.AIR, UntappedCauldronInteraction::takeHoneyBlockInteraction);
  }

  private static InteractionResult takeHoneyBlockInteraction(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      ItemStack itemStack) {
    if (blockState.is(UntappedBlocks.HONEY_CAULDRON)
        && blockState.getValue(UntappedHoneyCauldronBlock.LEVEL)
            != UntappedHoneyCauldronBlock.MAX_FILL_LEVEL) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    if (level.isClientSide()) {
      return InteractionResult.SUCCESS;
    }

    player.setItemInHand(
        interactionHand,
        ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.HONEY_BLOCK)));
    player.awardStat(Stats.USE_CAULDRON);

    level.setBlock(blockPos, Blocks.CAULDRON.defaultBlockState(), Block.UPDATE_ALL);
    level.playSound(null, blockPos, SoundEvents.HONEY_BLOCK_BREAK, SoundSource.BLOCKS);
    level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);

    return InteractionResult.SUCCESS;
  }

  private static InteractionResult takeHoneyInteraction(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      ItemStack itemStack) {
    if (!blockState.is(UntappedBlocks.HONEY_CAULDRON)) {
      return InteractionResult.PASS;
    }
    if (blockState.getValue(UntappedHoneyCauldronBlock.LEVEL) <= 0) {
      return InteractionResult.PASS;
    }

    if (level.isClientSide()) {
      return InteractionResult.SUCCESS;
    }

    player.setItemInHand(
        interactionHand,
        ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.HONEY_BOTTLE)));
    player.awardStat(Stats.USE_CAULDRON);
    player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));

    UntappedHoneyCauldronBlock.lowerFillLevel(blockState, level, blockPos);
    level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS);
    level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);

    return InteractionResult.SUCCESS;
  }

  public static InteractionResult fillHoneyInteraction(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      ItemStack itemStack) {
    if (!blockState.is(UntappedBlocks.HONEY_CAULDRON) && !blockState.is(Blocks.CAULDRON)) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    if (blockState.is(UntappedBlocks.HONEY_CAULDRON)
        && blockState.getValue(UntappedHoneyCauldronBlock.LEVEL)
            == UntappedHoneyCauldronBlock.MAX_FILL_LEVEL) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    if (level.isClientSide()) {
      return InteractionResult.SUCCESS;
    }

    player.setItemInHand(
        interactionHand,
        ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.GLASS_BOTTLE)));
    player.awardStat(Stats.FILL_CAULDRON);
    player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));

    BlockState newBlockState;
    if (blockState.is(UntappedBlocks.HONEY_CAULDRON)) {
      final IntegerProperty fillLevel = UntappedHoneyCauldronBlock.LEVEL;
      final Integer currentFillLevel = blockState.getValue(fillLevel);
      newBlockState = blockState.setValue(fillLevel, currentFillLevel + 1);
    } else {
      newBlockState =
          UntappedBlocks.HONEY_CAULDRON
              .defaultBlockState()
              .setValue(
                  UntappedHoneyCauldronBlock.LEVEL, UntappedHoneyCauldronBlock.MIN_FILL_LEVEL);
    }

    level.setBlock(blockPos, newBlockState, Block.UPDATE_ALL);
    level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS);
    level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);

    return InteractionResult.SUCCESS;
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Potion Cauldron
  // -------------------------------------------------------------------------------------------------------------------

  private static void addPotionCauldronInteractions() {
    final Map<Item, CauldronInteraction> map = POTION.map();
    CauldronInteraction.addDefaultInteractions(map);
    CauldronInteraction.EMPTY.map().remove(Items.POTION);
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
    if (level.isClientSide()) {
      return InteractionResult.SUCCESS;
    }

    if (!(level.getBlockEntity(blockPos)
        instanceof UntappedPotionCauldronBlockEntity potionCauldronBlockEntity)) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    if (potionCauldronBlockEntity.getAllEffects().isEmpty()) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    final int currentPotency = potionCauldronBlockEntity.getArrowPotency();
    if (currentPotency <= 0) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    final int arrowsToConvert = Math.min(itemStack.getCount(), currentPotency);
    if (arrowsToConvert <= 0) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    final ItemStack tippedArrow = new ItemStack(Items.TIPPED_ARROW, arrowsToConvert);
    final ItemStack tippedArrowActual = tippedArrow.copy();

    potionCauldronBlockEntity.savePotionContentsToItemStack(tippedArrowActual);

    player.setItemInHand(
        interactionHand,
        UntappedItemUtils.createFilledResults(
            itemStack, player, tippedArrowActual, false, arrowsToConvert));
    player.awardStat(Stats.USE_CAULDRON);
    player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));

    final int newPotency = currentPotency - arrowsToConvert;
    potionCauldronBlockEntity.setArrowPotency(newPotency);

    final int newLevel =
        (newPotency + UntappedPotionCauldronBlockEntity.ARROW_POTENCY_PER_LEVEL - 1)
            / UntappedPotionCauldronBlockEntity.ARROW_POTENCY_PER_LEVEL;

    if (newLevel > 0) {
      level.setBlock(
          blockPos, blockState.setValue(LayeredCauldronBlock.LEVEL, newLevel), Block.UPDATE_ALL);
    } else {
      level.setBlock(blockPos, Blocks.CAULDRON.defaultBlockState(), Block.UPDATE_ALL);
    }

    level.playSound(null, blockPos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS);

    return InteractionResult.SUCCESS;
  }

  private static InteractionResult takePotionInteraction(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      ItemStack itemStack) {
    if (level.isClientSide()) {
      return InteractionResult.SUCCESS;
    }

    if (!(level.getBlockEntity(blockPos)
        instanceof UntappedPotionCauldronBlockEntity potionCauldronBlockEntity)) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    if (!potionCauldronBlockEntity.isWater()
        && potionCauldronBlockEntity.getAllEffects().isEmpty()) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    final ItemStack newPotionStack = new ItemStack(potionCauldronBlockEntity.getPotionType());
    final ItemStack actualPotionStack = newPotionStack.copy();
    potionCauldronBlockEntity.savePotionContentsToItemStack(actualPotionStack);

    player.setItemInHand(
        interactionHand, ItemUtils.createFilledResult(itemStack, player, actualPotionStack));
    player.awardStat(Stats.USE_CAULDRON);
    player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));

    LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);
    level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS);
    level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);

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
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    final boolean isWater =
        potionContents.is(Potions.WATER)
            || potionContents.is(Potions.AWKWARD)
            || potionContents.is(Potions.MUNDANE)
            || potionContents.is(Potions.THICK);

    final Optional<Holder<Potion>> incomingPotionOptional = potionContents.potion();
    if (incomingPotionOptional.isEmpty()) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    if (!isWater && incomingPotionOptional.get().value().getEffects().isEmpty()) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    if (blockState.is(UntappedBlocks.POTION_CAULDRON)) {
      if (level.getBlockEntity(blockPos)
          instanceof UntappedPotionCauldronBlockEntity potionCauldronBlockEntity) {
        final Optional<Holder<Potion>> existingPotion = potionCauldronBlockEntity.getPotion();

        if (existingPotion.isPresent()
            && !existingPotion.get().equals(incomingPotionOptional.get())) {
          return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (potionCauldronBlockEntity.getPotionType() != itemStack.getItem()) {
          return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (blockState.getValue(LayeredCauldronBlock.LEVEL)
            == LayeredCauldronBlock.MAX_FILL_LEVEL) {
          return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
      }
    }

    if (level.isClientSide()) {
      return InteractionResult.SUCCESS;
    }

    final int newLevel =
        blockState.is(UntappedBlocks.POTION_CAULDRON)
            ? blockState.getValue(LayeredCauldronBlock.LEVEL) + 1
            : LayeredCauldronBlock.MIN_FILL_LEVEL;

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
      potionCauldronBlockEntity.setContents(potionContents, itemStack.getItem(), newLevel);

      player.setItemInHand(
          interactionHand,
          ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.GLASS_BOTTLE)));
      player.awardStat(Stats.FILL_CAULDRON);
      player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));

      level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS);
      level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
    }

    return InteractionResult.SUCCESS;
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Dyed Water Cauldron
  // -------------------------------------------------------------------------------------------------------------------

  private static void addDyedWaterCauldronInteractions() {
    final Map<Item, CauldronInteraction> water = CauldronInteraction.WATER.map();
    for (DyeColor dyeColor : DyeColor.values()) {
      final Item dyeItem = DyeItem.byColor(dyeColor);
      water.put(dyeItem, UntappedCauldronInteraction::dyeWaterInteraction);
    }

    final Map<Item, CauldronInteraction> dyed = WATER_DYED.map();
    CauldronInteraction.addDefaultInteractions(dyed);

    for (DyeColor dyeColor : DyeColor.values()) {
      final Item dyeItem = DyeItem.byColor(dyeColor);
      dyed.put(dyeItem, UntappedCauldronInteraction::mixMoreDyeInteraction);
    }

    if (dyed instanceof Object2ObjectOpenHashMap<Item, CauldronInteraction> map) {
      map.defaultReturnValue(UntappedCauldronInteraction::dipDyeableInteraction);
    }
  }

  private static InteractionResult dipDyeableInteraction(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      ItemStack itemStack) {
    if (!itemStack.is(ItemTags.DYEABLE)) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    if (level.isClientSide()) {
      return InteractionResult.SUCCESS;
    }

    if (!(level.getBlockEntity(blockPos)
        instanceof UntappedDyedWaterCauldronEntity dyedWaterCauldronEntity)) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    final int waterColor = dyedWaterCauldronEntity.getColor();
    final DyedItemColor currentDyeItemColorComponent = itemStack.get(DataComponents.DYED_COLOR);
    final int dyedItemColor =
        (currentDyeItemColorComponent != null)
            ? currentDyeItemColorComponent.rgb()
            : DyedItemColor.LEATHER_COLOR;

    final int blendedColor = getBlendedColor(dyedItemColor, waterColor);
    if (blendedColor == dyedItemColor) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    final ItemStack dyeItemStack = itemStack.copy();
    dyeItemStack.set(DataComponents.DYED_COLOR, new DyedItemColor(blendedColor));

    player.setItemInHand(
        interactionHand, ItemUtils.createFilledResult(itemStack, player, dyeItemStack));
    player.awardStat(Stats.USE_CAULDRON);
    player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));

    LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);
    level.playSound(null, blockPos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS);

    return InteractionResult.SUCCESS;
  }

  private static int getBlendedColor(int base, int blend) {
    int baseRed = ARGB.red(base);
    int baseGreen = ARGB.green(base);
    int baseBlue = ARGB.blue(base);

    int sum = Math.max(baseRed, Math.max(baseGreen, baseBlue));
    int colors = 1;

    final int blendRed = ARGB.red(blend);
    final int blendGreen = ARGB.green(blend);
    final int blendBlue = ARGB.blue(blend);

    sum += Math.max(blendRed, Math.max(blendGreen, blendBlue));
    baseRed += blendRed;
    baseGreen += blendGreen;
    baseBlue += blendBlue;
    colors++;

    final int averageRed = baseRed / colors;
    final int averageGreen = baseGreen / colors;
    final int averageBlue = baseBlue / colors;

    final float average = (float) Math.max(averageRed, Math.max(averageGreen, averageBlue));
    final float brightness = (float) sum / (float) colors;

    final int red = (int) ((float) averageRed * brightness / average);
    final int green = (int) ((float) averageGreen * brightness / average);
    final int blue = (int) ((float) averageBlue * brightness / average);

    return ARGB.color(red, green, blue);
  }

  private static InteractionResult mixMoreDyeInteraction(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      ItemStack itemStack) {
    if (level.isClientSide()) {
      return InteractionResult.SUCCESS;
    }

    if (!(level.getBlockEntity(blockPos)
        instanceof UntappedDyedWaterCauldronEntity dyedWaterCauldronEntity)) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    final boolean isDyeable =
        dyedWaterCauldronEntity.isDyeable(List.of((DyeItem) itemStack.getItem()));
    if (!isDyeable) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    player.setItemInHand(
        interactionHand, ItemUtils.createFilledResult(itemStack, player, ItemStack.EMPTY));
    player.awardStat(Stats.USE_CAULDRON);
    player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));

    return InteractionResult.SUCCESS;
  }

  private static InteractionResult dyeWaterInteraction(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      ItemStack itemStack) {
    if (level.isClientSide()) {
      return InteractionResult.SUCCESS;
    }

    final int currentLevel = blockState.getValue(LayeredCauldronBlock.LEVEL);
    final BlockState newState =
        UntappedBlocks.DYED_WATER_CAULDRON
            .defaultBlockState()
            .setValue(LayeredCauldronBlock.LEVEL, currentLevel);

    level.setBlock(blockPos, newState, Block.UPDATE_ALL);

    if (level.getBlockEntity(blockPos) instanceof UntappedDyedWaterCauldronEntity dyed) {
      if (!dyed.isDyeable(List.of((DyeItem) itemStack.getItem()))) {
        level.setBlock(blockPos, blockState, Block.UPDATE_ALL);
        return InteractionResult.TRY_WITH_EMPTY_HAND;
      }
    }

    player.setItemInHand(
        interactionHand, ItemUtils.createFilledResult(itemStack, player, ItemStack.EMPTY));
    player.awardStat(Stats.USE_CAULDRON);
    player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));

    level.playSound(null, blockPos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS);

    return InteractionResult.SUCCESS;
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Water Cauldron
  // -------------------------------------------------------------------------------------------------------------------

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
                    interactionHand, ItemUtils.createFilledResult(itemStack, player, washed));
                LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);
              }

              return InteractionResult.SUCCESS;
            }

            return InteractionResult.TRY_WITH_EMPTY_HAND;
          };

      map.defaultReturnValue(washing);
    }
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Slime Cauldron
  // -------------------------------------------------------------------------------------------------------------------

  private static void addSlimeCauldronInteractions() {
    final Map<Item, CauldronInteraction> map = SLIME.map();
    CauldronInteraction.addDefaultInteractions(map);
    CauldronInteraction.EMPTY
        .map()
        .put(Items.SLIME_BALL, UntappedCauldronInteraction::fillSlimeInteraction);
    map.put(Items.SLIME_BALL, UntappedCauldronInteraction::fillSlimeInteraction);
    map.put(Items.AIR, UntappedCauldronInteraction::takeSlimeInteraction);
  }

  private static InteractionResult takeSlimeInteraction(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      ItemStack itemStack) {
    if (interactionHand.equals(InteractionHand.OFF_HAND)) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    if (level.isClientSide()) {
      return InteractionResult.SUCCESS;
    }

    if (blockState.is(UntappedBlocks.SLIME_CAULDRON)
        && blockState.getValue(UntappedSlimeCauldronBlock.LEVEL)
            == UntappedSlimeCauldronBlock.MAX_FILL_LEVEL) {
      player.setItemInHand(
          interactionHand,
          ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.SLIME_BLOCK)));
      player.awardStat(Stats.USE_CAULDRON);
      level.setBlock(blockPos, Blocks.CAULDRON.defaultBlockState(), Block.UPDATE_ALL);
    } else {
      player.setItemInHand(
          interactionHand,
          ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.SLIME_BALL)));
      UntappedSlimeCauldronBlock.lowerFillLevel(blockState, level, blockPos);
    }

    level.playSound(null, blockPos, SoundEvents.SLIME_BLOCK_BREAK, SoundSource.BLOCKS);
    level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);

    return InteractionResult.SUCCESS;
  }

  private static InteractionResult fillSlimeInteraction(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      ItemStack itemStack) {
    if (!blockState.is(UntappedBlocks.SLIME_CAULDRON) && !blockState.is(Blocks.CAULDRON)) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    if (blockState.is(UntappedBlocks.SLIME_CAULDRON)
        && blockState.getValue(UntappedSlimeCauldronBlock.LEVEL)
            == UntappedSlimeCauldronBlock.MAX_FILL_LEVEL) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    if (level.isClientSide()) {
      return InteractionResult.SUCCESS;
    }

    player.setItemInHand(
        interactionHand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.AIR)));
    player.awardStat(Stats.FILL_CAULDRON);
    player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));

    BlockState newBlockState;
    if (blockState.is(UntappedBlocks.SLIME_CAULDRON)) {
      final IntegerProperty fillLevel = UntappedSlimeCauldronBlock.LEVEL;
      final Integer currentFillLevel = blockState.getValue(fillLevel);
      newBlockState = blockState.setValue(fillLevel, currentFillLevel + 1);
    } else {
      newBlockState =
          UntappedBlocks.SLIME_CAULDRON
              .defaultBlockState()
              .setValue(
                  UntappedSlimeCauldronBlock.LEVEL, UntappedSlimeCauldronBlock.MIN_FILL_LEVEL);
    }

    level.setBlock(blockPos, newBlockState, Block.UPDATE_ALL);
    level.playSound(null, blockPos, SoundEvents.SLIME_SQUISH_SMALL, SoundSource.BLOCKS);
    level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);

    return InteractionResult.SUCCESS;
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Magma Cauldron
  // -------------------------------------------------------------------------------------------------------------------

  private static void addMagmaCauldronInteractions() {
    final Map<Item, CauldronInteraction> map = MAGMA.map();
    CauldronInteraction.addDefaultInteractions(map);
    CauldronInteraction.EMPTY
        .map()
        .put(Items.MAGMA_CREAM, UntappedCauldronInteraction::fillMagmaInteraction);
    map.put(Items.MAGMA_CREAM, UntappedCauldronInteraction::fillMagmaInteraction);
    map.put(Items.AIR, UntappedCauldronInteraction::takeMagmaInteraction);
  }

  private static InteractionResult takeMagmaInteraction(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      ItemStack itemStack) {
    if (interactionHand.equals(InteractionHand.OFF_HAND)) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    if (level.isClientSide()) {
      return InteractionResult.SUCCESS;
    }

    if (blockState.getValue(UntappedMagmaCauldronBlock.LEVEL)
        == UntappedMagmaCauldronBlock.MAX_FILL_LEVEL) {
      player.setItemInHand(
          interactionHand,
          ItemUtils.createFilledResult(itemStack, player, new ItemStack(Blocks.MAGMA_BLOCK)));
      player.awardStat(Stats.USE_CAULDRON);
      level.setBlock(blockPos, Blocks.CAULDRON.defaultBlockState(), Block.UPDATE_ALL);
    } else {
      player.setItemInHand(
          interactionHand,
          ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.MAGMA_CREAM)));
      UntappedMagmaCauldronBlock.lowerFillLevel(blockState, level, blockPos);
    }

    level.playSound(null, blockPos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS);
    level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);

    return InteractionResult.SUCCESS;
  }

  private static InteractionResult fillMagmaInteraction(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      ItemStack itemStack) {
    if (!blockState.is(UntappedBlocks.MAGMA_CAULDRON) && !blockState.is(Blocks.CAULDRON)) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    if (blockState.is(UntappedBlocks.MAGMA_CAULDRON)
        && blockState.getValue(UntappedMagmaCauldronBlock.LEVEL)
            == UntappedMagmaCauldronBlock.MAX_FILL_LEVEL) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    if (level.isClientSide()) {
      return InteractionResult.SUCCESS;
    }

    player.setItemInHand(
        interactionHand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.AIR)));
    player.awardStat(Stats.FILL_CAULDRON);
    player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));

    BlockState newBlockState;
    if (blockState.is(UntappedBlocks.MAGMA_CAULDRON)) {
      final IntegerProperty fillLevel = UntappedMagmaCauldronBlock.LEVEL;
      final Integer currentFillLevel = blockState.getValue(fillLevel);
      newBlockState = blockState.setValue(fillLevel, currentFillLevel + 1);
    } else {
      newBlockState =
          UntappedBlocks.MAGMA_CAULDRON
              .defaultBlockState()
              .setValue(
                  UntappedMagmaCauldronBlock.LEVEL, UntappedMagmaCauldronBlock.MIN_FILL_LEVEL);
    }

    level.setBlock(blockPos, newBlockState, Block.UPDATE_ALL);
    level.playSound(null, blockPos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS);
    level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);

    return InteractionResult.SUCCESS;
  }

  public static void initialize() {
    addDyeableInteractions();
    addHoneyCauldronInteractions();
    addPotionCauldronInteractions();
    addDyedWaterCauldronInteractions();
    addSlimeCauldronInteractions();
    addMagmaCauldronInteractions();
  }
}
