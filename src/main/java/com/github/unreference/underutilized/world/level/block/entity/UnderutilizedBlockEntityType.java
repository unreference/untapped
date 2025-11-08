package com.github.unreference.underutilized.world.level.block.entity;

import com.github.unreference.underutilized.resources.UnderutilizedResourceLocation;
import com.github.unreference.underutilized.world.level.block.UnderutilizedBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class UnderutilizedBlockEntityType {
  private static <T extends BlockEntity> BlockEntityType<T> register(
      String name, FabricBlockEntityTypeBuilder.Factory<? extends T> factory, Block... blocks) {
    return Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        UnderutilizedResourceLocation.withDefaultNamespace(name),
        FabricBlockEntityTypeBuilder.<T>create(factory, blocks).build());
  }

  public static void initialize() {}

  public static final BlockEntityType<UnderutilizedHoneyCauldronBlockEntity> HONEY_CAULDRON =
      register(
          "honey_cauldron",
          UnderutilizedHoneyCauldronBlockEntity::new,
          UnderutilizedBlocks.HONEY_CAULDRON);
}
