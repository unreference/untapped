package com.github.unreference.untapped.client;

import com.github.unreference.untapped.world.level.block.UntappedBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public class UntappedClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    BlockRenderLayerMap.putBlock(UntappedBlocks.HONEY_CAULDRON, ChunkSectionLayer.TRANSLUCENT);
    BlockRenderLayerMap.putBlock(UntappedBlocks.FROZEN_CAULDRON, ChunkSectionLayer.TRANSLUCENT);
  }
}
