package com.github.unreference.underutilized.data.loot;

import com.github.unreference.underutilized.world.level.block.UnderutilizedBlocks;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Blocks;

public final class UnderutilizedBlockLootTableProvider extends FabricBlockLootTableProvider {
  public UnderutilizedBlockLootTableProvider(
      FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
    super(dataOutput, registryLookup);
  }

  @Override
  public void generate() {
    this.dropOther(UnderutilizedBlocks.HONEY_CAULDRON, Blocks.CAULDRON);
  }
}
