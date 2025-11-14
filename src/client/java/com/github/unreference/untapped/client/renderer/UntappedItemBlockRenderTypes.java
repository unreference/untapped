package com.github.unreference.untapped.client.renderer;

import com.github.unreference.untapped.world.level.block.UntappedBlocks;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public final class UntappedItemBlockRenderTypes {
  public static void initialize() {
    BlockRenderLayerMap.putBlock(UntappedBlocks.HONEY_CAULDRON, ChunkSectionLayer.TRANSLUCENT);
    BlockRenderLayerMap.putBlock(UntappedBlocks.FROZEN_CAULDRON, ChunkSectionLayer.TRANSLUCENT);
    BlockRenderLayerMap.putBlock(UntappedBlocks.POTION_CAULDRON, ChunkSectionLayer.SOLID);
    BlockRenderLayerMap.putBlock(UntappedBlocks.DYED_WATER_CAULDRON, ChunkSectionLayer.TRANSLUCENT);
    BlockRenderLayerMap.putBlock(UntappedBlocks.SLIME_CAULDRON, ChunkSectionLayer.TRANSLUCENT);
    BlockRenderLayerMap.putBlock(UntappedBlocks.MAGMA_CAULDRON, ChunkSectionLayer.SOLID);
  }
}
