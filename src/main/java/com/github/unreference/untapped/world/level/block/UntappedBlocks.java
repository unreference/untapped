package com.github.unreference.untapped.world.level.block;

import com.github.unreference.untapped.resources.UntappedResourceLocation;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class UntappedBlocks {
  public static final Block HONEY_CAULDRON =
      register(
          "honey_cauldron",
          UntappedHoneyCauldronBlock::new,
          BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON),
          false);

  public static final Block FROZEN_CAULDRON =
      register(
          "frozen_cauldron",
          UntappedFrozenCauldronBlock::new,
          BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON),
          false);

  public static final Block POTION_CAULDRON =
      register(
          "potion_cauldron",
          UntappedPotionCauldronBlock::new,
          BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON),
          false);

  private static Block register(
      String name,
      Function<BlockBehaviour.Properties, Block> blockFunction,
      BlockBehaviour.Properties properties,
      boolean shouldRegisterItem) {
    final ResourceKey<Block> blockResourceKey = createBlockResourceKey(name);
    final Block block = blockFunction.apply(properties.setId(blockResourceKey));

    if (shouldRegisterItem) {
      final ResourceKey<Item> itemResourceKey = createItemResourceKey(name);
      final BlockItem blockItem =
          new BlockItem(
              block, new Item.Properties().setId(itemResourceKey).useBlockDescriptionPrefix());
      Registry.register(BuiltInRegistries.ITEM, itemResourceKey, blockItem);
    }

    return Registry.register(BuiltInRegistries.BLOCK, blockResourceKey, block);
  }

  private static ResourceKey<Block> createBlockResourceKey(String name) {
    return ResourceKey.create(
        Registries.BLOCK, UntappedResourceLocation.withDefaultNamespace(name));
  }

  private static ResourceKey<Item> createItemResourceKey(String name) {
    return ResourceKey.create(Registries.ITEM, UntappedResourceLocation.withDefaultNamespace(name));
  }

  public static void initialize() {}
}
