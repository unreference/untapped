package com.github.unreference.untapped.data.recipes.packs;

import com.github.unreference.untapped.world.item.UntappedItems;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class UntappedRecipeProvider extends FabricRecipeProvider {
  public UntappedRecipeProvider(
      FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
    super(output, registriesFuture);
  }

  @Override
  protected @NotNull RecipeProvider createRecipeProvider(
      HolderLookup.Provider provider, RecipeOutput recipeOutput) {
    return new RecipeProvider(provider, recipeOutput) {
      @Override
      public void buildRecipes() {
        final HolderLookup.RegistryLookup<Item> itemProvider =
            registries.lookupOrThrow(Registries.ITEM);

        final Item leather = Items.LEATHER;
        this.shaped(RecipeCategory.COMBAT, UntappedItems.QUIVER)
            .define('S', Items.STRING)
            .define('L', leather)
            .pattern("SS ")
            .pattern("SL ")
            .pattern("L  ")
            .unlockedBy(getHasName(leather), this.has(leather))
            .save(recipeOutput);
      }
    };
  }

  @Override
  public String getName() {
    return "";
  }
}
