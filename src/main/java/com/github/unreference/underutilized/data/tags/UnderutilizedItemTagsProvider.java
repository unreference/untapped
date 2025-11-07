package com.github.unreference.underutilized.data.tags;

import com.github.unreference.underutilized.tags.UnderutilizedBlockTags;
import com.github.unreference.underutilized.tags.UnderutilizedItemTags;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.Nullable;

public final class UnderutilizedItemTagsProvider extends FabricTagProvider.ItemTagProvider {
  public UnderutilizedItemTagsProvider(
      FabricDataOutput output,
      CompletableFuture<HolderLookup.Provider> registriesFuture,
      @Nullable BlockTagProvider blockTagProvider) {
    super(output, registriesFuture, blockTagProvider);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    this.copy(UnderutilizedBlockTags.STAINED_GLASS, UnderutilizedItemTags.STAINED_GLASS);
    this.copy(
        UnderutilizedBlockTags.STAINED_GLASS_PANES, UnderutilizedItemTags.STAINED_GLASS_PANES);
  }
}
