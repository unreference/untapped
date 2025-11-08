package com.github.unreference.underutilized.mixin.core.cauldron;

import com.github.unreference.underutilized.core.cauldron.UnderutilizedCauldronWashableHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CauldronInteraction.class)
public interface UnderutilizedMixinCauldronInteraction {
  @Inject(method = "bootStrap", at = @At("TAIL"))
  private static void bootStrap(CallbackInfo callbackInfo) {
    final Map<Item, CauldronInteraction> water = CauldronInteraction.WATER.map();

    if (water instanceof Object2ObjectOpenHashMap<Item, CauldronInteraction> map) {
      final CauldronInteraction washing =
          (blockState, level, blockPos, player, interactionHand, itemStack) -> {
            for (Map.Entry<TagKey<Item>, Item> entry :
                UnderutilizedCauldronWashableHolder.WASHABLE.entrySet()) {
              final TagKey<Item> washable = entry.getKey();
              final ItemLike uncolored = entry.getValue();

              if (itemStack.is(washable) && itemStack.getItem() != uncolored.asItem()) {
                if (!level.isClientSide()) {
                  final ItemStack washed = itemStack.transmuteCopy(uncolored, 1);
                  player.setItemInHand(
                      interactionHand,
                      ItemUtils.createFilledResult(itemStack, player, washed, false));
                  LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);
                }

                return InteractionResult.SUCCESS;
              }
            }

            return InteractionResult.TRY_WITH_EMPTY_HAND;
          };

      map.defaultReturnValue(washing);
    }
  }
}
