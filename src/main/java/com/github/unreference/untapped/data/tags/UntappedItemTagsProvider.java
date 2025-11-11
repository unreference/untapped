package com.github.unreference.untapped.data.tags;

import com.github.unreference.untapped.tags.UntappedBlockTags;
import com.github.unreference.untapped.tags.UntappedItemTags;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.Nullable;

public final class UntappedItemTagsProvider extends FabricTagProvider.ItemTagProvider {
  public UntappedItemTagsProvider(
      FabricDataOutput output,
      CompletableFuture<HolderLookup.Provider> registriesFuture,
      @Nullable BlockTagProvider blockTagProvider) {
    super(output, registriesFuture, blockTagProvider);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    this.copy(UntappedBlockTags.STAINED_GLASS, UntappedItemTags.STAINED_GLASS);
    this.copy(UntappedBlockTags.STAINED_GLASS_PANES, UntappedItemTags.STAINED_GLASS_PANES);
  }
}
