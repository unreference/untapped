package com.github.unreference.untapped.world.level.block.entity;

import com.github.unreference.untapped.resources.UntappedResourceLocation;
import com.github.unreference.untapped.world.level.block.UntappedBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class UntappedBlockEntityType {
  private static <T extends BlockEntity> BlockEntityType<T> register(
      String name, FabricBlockEntityTypeBuilder.Factory<? extends T> factory, Block... blocks) {
    return Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        UntappedResourceLocation.withDefaultNamespace(name),
        FabricBlockEntityTypeBuilder.<T>create(factory, blocks).build());
  }

  public static void initialize() {}

  public static final BlockEntityType<UntappedHoneyCauldronBlockEntity> HONEY_CAULDRON =
      register(
          "honey_cauldron", UntappedHoneyCauldronBlockEntity::new, UntappedBlocks.HONEY_CAULDRON);

  public static final BlockEntityType<UntappedFrozenCauldronBlockEntity> FROZEN_CAULDRON =
      register(
          "frozen_cauldron",
          UntappedFrozenCauldronBlockEntity::new,
          UntappedBlocks.FROZEN_CAULDRON);

  public static final BlockEntityType<UntappedPotionCauldronBlockEntity> POTION_CAULDRON =
      register(
          "potion_cauldron",
          UntappedPotionCauldronBlockEntity::new,
          UntappedBlocks.POTION_CAULDRON);

  public static final BlockEntityType<UntappedDyedWaterCauldronEntity> DYED_WATER_CAULDRON =
      register(
          "dyed_water_cauldron",
          UntappedDyedWaterCauldronEntity::new,
          UntappedBlocks.DYED_WATER_CAULDRON);
}
