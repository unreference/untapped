package com.github.unreference.underutilized.client;

import com.github.unreference.underutilized.world.level.block.UnderutilizedBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public class UnderutilizedClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    BlockRenderLayerMap.putBlock(UnderutilizedBlocks.HONEY_CAULDRON, ChunkSectionLayer.TRANSLUCENT);
  }
}
