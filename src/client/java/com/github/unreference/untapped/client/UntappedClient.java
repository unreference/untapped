package com.github.unreference.untapped.client;

import com.github.unreference.untapped.world.level.block.UntappedBlocks;
import com.github.unreference.untapped.world.level.block.entity.UntappedPotionCauldronBlockEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.item.alchemy.PotionContents;

public class UntappedClient implements ClientModInitializer {
  private static void registerBlockRenderLayerMaps() {
    // TODO: Possibly custom renderers to prevent inner cauldron being translucent
    BlockRenderLayerMap.putBlock(UntappedBlocks.HONEY_CAULDRON, ChunkSectionLayer.TRANSLUCENT);
    BlockRenderLayerMap.putBlock(UntappedBlocks.FROZEN_CAULDRON, ChunkSectionLayer.TRANSLUCENT);
    BlockRenderLayerMap.putBlock(UntappedBlocks.POTION_CAULDRON, ChunkSectionLayer.TRANSLUCENT);
  }

  private static void registerBlockColorHandlers() {
    ColorProviderRegistry.BLOCK.register(
        (blockState, blockAndTintGetter, blockPos, i) -> {
          if (blockAndTintGetter != null && blockPos != null) {
            if (blockAndTintGetter.getBlockEntity(blockPos)
                instanceof UntappedPotionCauldronBlockEntity potionCauldronBlockEntity) {
              return potionCauldronBlockEntity.getColor();
            }
          }

          return PotionContents.BASE_POTION_COLOR;
        },
        UntappedBlocks.POTION_CAULDRON);
  }

  @Override
  public void onInitializeClient() {
    registerBlockRenderLayerMaps();
    registerBlockColorHandlers();
  }
}
