package com.github.unreference.untapped.world.item;

import com.github.unreference.untapped.core.component.UntappedDataComponents;
import com.github.unreference.untapped.core.component.UntappedQuiverContents;
import com.github.unreference.untapped.resources.UntappedResourceLocation;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public final class UntappedItems {
  public static final Item QUIVER =
      register(
          "quiver",
          UntappedQuiverItem::new,
          new Item.Properties()
              .stacksTo(1)
              .component(UntappedDataComponents.QUIVER_CONTENTS, UntappedQuiverContents.EMPTY));

  private static Item register(
      String name, Function<Item.Properties, Item> itemFunction, Item.Properties properties) {
    final ResourceKey<Item> itemResourceKey =
        ResourceKey.create(Registries.ITEM, UntappedResourceLocation.withDefaultNamespace(name));
    final Item item = itemFunction.apply(properties.setId(itemResourceKey));

    Registry.register(BuiltInRegistries.ITEM, itemResourceKey, item);
    return item;
  }

  public static void initialize() {}
}
