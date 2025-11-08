package com.github.unreference.untapped.data.loot;

import com.github.unreference.untapped.world.level.block.UntappedBlocks;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Blocks;

public final class UntappedBlockLootTableProvider extends FabricBlockLootTableProvider {
  public UntappedBlockLootTableProvider(
      FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
    super(dataOutput, registryLookup);
  }

  @Override
  public void generate() {
    this.dropOther(UntappedBlocks.HONEY_CAULDRON, Blocks.CAULDRON);
  }
}
