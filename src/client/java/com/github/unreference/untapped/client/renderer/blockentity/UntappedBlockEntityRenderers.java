package com.github.unreference.untapped.client.renderer.blockentity;

import com.github.unreference.untapped.world.level.block.UntappedBlocks;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.Block;

public class UntappedBlockEntityRenderers {
  public static void initialize() {
    register(UntappedBlocks.POTION_CAULDRON, PotionContents.BASE_POTION_COLOR);
    register(UntappedBlocks.DYED_WATER_CAULDRON, DyedItemColor.LEATHER_COLOR);
  }

  private static void register(Block block, int fallbackColor) {
    ColorProviderRegistry.BLOCK.register(
        (blockState, blockAndTintGetter, blockPos, i) -> {
          if (blockAndTintGetter != null) {
            final Object data = blockAndTintGetter.getBlockEntityRenderData(blockPos);
            if (data instanceof Integer color) {
              return color;
            }
          }

          return fallbackColor;
        },
        block);
  }
}
