package com.github.unreference.underutilized.client;

import com.github.unreference.underutilized.data.tags.UnderutilizedBlockTagsProvider;
import com.github.unreference.underutilized.data.tags.UnderutilizedItemTagsProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class UnderutilizedDataGenerator implements DataGeneratorEntrypoint {
  @Override
  public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
    final FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

    final UnderutilizedBlockTagsProvider blockTags =
        pack.addProvider(UnderutilizedBlockTagsProvider::new);
    pack.addProvider(
        (output, lookup) -> new UnderutilizedItemTagsProvider(output, lookup, blockTags));
  }
}
