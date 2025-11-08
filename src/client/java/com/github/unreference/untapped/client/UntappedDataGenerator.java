package com.github.unreference.untapped.client;

import com.github.unreference.untapped.client.data.models.UntappedModelProvider;
import com.github.unreference.untapped.data.loot.UntappedBlockLootTableProvider;
import com.github.unreference.untapped.data.tags.UntappedBlockTagsProvider;
import com.github.unreference.untapped.data.tags.UntappedItemTagsProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class UntappedDataGenerator implements DataGeneratorEntrypoint {
  @Override
  public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
    final FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

    final UntappedBlockTagsProvider blockTags = pack.addProvider(UntappedBlockTagsProvider::new);
    pack.addProvider((output, lookup) -> new UntappedItemTagsProvider(output, lookup, blockTags));

    pack.addProvider(UntappedModelProvider::new);
    pack.addProvider(UntappedBlockLootTableProvider::new);
  }
}
