package com.github.unreference.untapped.client;

import com.github.unreference.untapped.world.level.block.UntappedBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.Block;

public class UntappedClient implements ClientModInitializer {
  private static void registerBlockRenderLayerMaps() {
    // TODO: Custom honey cauldron renderer to prevent the inner cauldron from being translucent
    BlockRenderLayerMap.putBlock(UntappedBlocks.HONEY_CAULDRON, ChunkSectionLayer.TRANSLUCENT);
    BlockRenderLayerMap.putBlock(UntappedBlocks.FROZEN_CAULDRON, ChunkSectionLayer.TRANSLUCENT);
    BlockRenderLayerMap.putBlock(UntappedBlocks.POTION_CAULDRON, ChunkSectionLayer.SOLID);
    BlockRenderLayerMap.putBlock(UntappedBlocks.DYED_WATER_CAULDRON, ChunkSectionLayer.TRANSLUCENT);
    BlockRenderLayerMap.putBlock(UntappedBlocks.SLIME_CAULDRON, ChunkSectionLayer.TRANSLUCENT);
    BlockRenderLayerMap.putBlock(UntappedBlocks.MAGMA_CAULDRON, ChunkSectionLayer.SOLID);
  }

  private static void registerBlockColorHandlers() {
    registerBlockColorHandler(UntappedBlocks.POTION_CAULDRON, PotionContents.BASE_POTION_COLOR);
    registerBlockColorHandler(UntappedBlocks.DYED_WATER_CAULDRON, DyedItemColor.LEATHER_COLOR);
  }

  private static void registerBlockColorHandler(Block block, int fallbackColor) {
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

  @Override
  public void onInitializeClient() {
    registerBlockRenderLayerMaps();
    registerBlockColorHandlers();
  }
}
