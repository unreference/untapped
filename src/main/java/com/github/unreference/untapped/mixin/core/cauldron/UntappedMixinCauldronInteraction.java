package com.github.unreference.untapped.mixin.core.cauldron;

import com.github.unreference.untapped.core.cauldron.UntappedCauldronWashableHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CauldronInteraction.class)
public interface UntappedMixinCauldronInteraction {
  @Inject(method = "bootStrap", at = @At("TAIL"))
  private static void bootStrap(CallbackInfo callbackInfo) {
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
                    ItemUtils.createFilledResult(itemStack, player, washed, false));
                LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);
              }

              return InteractionResult.SUCCESS;
            }

            return InteractionResult.TRY_WITH_EMPTY_HAND;
          };

      map.defaultReturnValue(washing);
    }
  }
}
