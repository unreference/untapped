package com.github.unreference.untapped.client.data.models;

import com.github.unreference.untapped.client.data.models.model.UntappedModelTemplates;
import com.github.unreference.untapped.client.data.models.model.UntappedTextureSlot;
import com.github.unreference.untapped.resources.UntappedResourceLocation;
import com.github.unreference.untapped.world.item.UntappedItems;
import com.github.unreference.untapped.world.level.block.*;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public final class UntappedModelProvider extends FabricModelProvider {
  public UntappedModelProvider(FabricDataOutput output) {
    super(output);
  }

  private static void createHoneyCauldron(BlockModelGenerators blockStateModelGenerator) {
    final Block block = UntappedBlocks.HONEY_CAULDRON;
    final TextureMapping textures = createFourLayeredCauldronTextures(Blocks.HONEY_BLOCK, true);
    final String location = ModelLocationUtils.getModelLocation(block).getPath();

    final ResourceLocation level1Model =
        UntappedResourceLocation.withDefaultNamespace(location + "_level1");
    final ResourceLocation level2Model =
        UntappedResourceLocation.withDefaultNamespace(location + "_level2");
    final ResourceLocation level3Model =
        UntappedResourceLocation.withDefaultNamespace(location + "_level3");
    final ResourceLocation fullModel =
        UntappedResourceLocation.withDefaultNamespace(location + "_full");

    UntappedModelTemplates.FOUR_LAYERED_CAULDRON_LEVEL1.create(
        level1Model, textures, blockStateModelGenerator.modelOutput);
    UntappedModelTemplates.FOUR_LAYERED_CAULDRON_LEVEL2.create(
        level2Model, textures, blockStateModelGenerator.modelOutput);
    UntappedModelTemplates.FOUR_LAYERED_CAULDRON_LEVEL3.create(
        level3Model, textures, blockStateModelGenerator.modelOutput);
    UntappedModelTemplates.FOUR_LAYERED_CAULDRON_FULL.create(
        fullModel, textures, blockStateModelGenerator.modelOutput);

    blockStateModelGenerator.blockStateOutput.accept(
        MultiVariantGenerator.dispatch(block)
            .with(
                PropertyDispatch.initial(UntappedHoneyCauldronBlock.LEVEL)
                    .select(1, BlockModelGenerators.plainVariant(level1Model))
                    .select(2, BlockModelGenerators.plainVariant(level2Model))
                    .select(3, BlockModelGenerators.plainVariant(level3Model))
                    .select(4, BlockModelGenerators.plainVariant(fullModel))));
  }

  private static void createFrozenCauldron(BlockModelGenerators blockStateModelGenerator) {
    final Block frozenCauldron = UntappedBlocks.FROZEN_CAULDRON;

    blockStateModelGenerator.blockStateOutput.accept(
        BlockModelGenerators.createSimpleBlock(
            frozenCauldron,
            BlockModelGenerators.plainVariant(
                ModelTemplates.CAULDRON_FULL.create(
                    frozenCauldron,
                    TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.ICE)),
                    blockStateModelGenerator.modelOutput))));
  }

  private static void createPotionCauldron(BlockModelGenerators blockStateModelGenerator) {
    final Block block = UntappedBlocks.POTION_CAULDRON;
    final TextureMapping texture =
        TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.WATER, "_still"));
    final String location = ModelLocationUtils.getModelLocation(block).getPath();

    final ResourceLocation level1Model =
        UntappedResourceLocation.withDefaultNamespace(location + "_level1");
    final ResourceLocation level2Model =
        UntappedResourceLocation.withDefaultNamespace(location + "_level2");
    final ResourceLocation fullModel =
        UntappedResourceLocation.withDefaultNamespace(location + "_full");

    ModelTemplates.CAULDRON_LEVEL1.create(
        level1Model, texture, blockStateModelGenerator.modelOutput);
    ModelTemplates.CAULDRON_LEVEL2.create(
        level2Model, texture, blockStateModelGenerator.modelOutput);
    ModelTemplates.CAULDRON_FULL.create(fullModel, texture, blockStateModelGenerator.modelOutput);

    blockStateModelGenerator.blockStateOutput.accept(
        MultiVariantGenerator.dispatch(UntappedBlocks.POTION_CAULDRON)
            .with(
                PropertyDispatch.initial(UntappedPotionCauldronBlock.LEVEL)
                    .select(1, BlockModelGenerators.plainVariant(level1Model))
                    .select(2, BlockModelGenerators.plainVariant(level2Model))
                    .select(3, BlockModelGenerators.plainVariant(fullModel))));
  }

  private static void createSlimeCauldron(BlockModelGenerators blockStateModelGenerator) {
    final Block block = UntappedBlocks.SLIME_CAULDRON;
    final TextureMapping textures = createFourLayeredCauldronTextures(Blocks.SLIME_BLOCK, false);
    final String location = ModelLocationUtils.getModelLocation(block).getPath();

    final ResourceLocation level1Model =
        UntappedResourceLocation.withDefaultNamespace(location + "_level1");
    final ResourceLocation level2Model =
        UntappedResourceLocation.withDefaultNamespace(location + "_level2");
    final ResourceLocation level3Model =
        UntappedResourceLocation.withDefaultNamespace(location + "_level3");
    final ResourceLocation fullModel =
        UntappedResourceLocation.withDefaultNamespace(location + "_full");

    UntappedModelTemplates.FOUR_LAYERED_CAULDRON_LEVEL1.create(
        level1Model, textures, blockStateModelGenerator.modelOutput);
    UntappedModelTemplates.FOUR_LAYERED_CAULDRON_LEVEL2.create(
        level2Model, textures, blockStateModelGenerator.modelOutput);
    UntappedModelTemplates.FOUR_LAYERED_CAULDRON_LEVEL3.create(
        level3Model, textures, blockStateModelGenerator.modelOutput);
    UntappedModelTemplates.FOUR_LAYERED_CAULDRON_FULL.create(
        fullModel, textures, blockStateModelGenerator.modelOutput);

    blockStateModelGenerator.blockStateOutput.accept(
        MultiVariantGenerator.dispatch(block)
            .with(
                PropertyDispatch.initial(UntappedSlimeCauldronBlock.LEVEL)
                    .select(1, BlockModelGenerators.plainVariant(level1Model))
                    .select(2, BlockModelGenerators.plainVariant(level2Model))
                    .select(3, BlockModelGenerators.plainVariant(level3Model))
                    .select(4, BlockModelGenerators.plainVariant(fullModel))));
  }

  private static TextureMapping createFourLayeredCauldronTextures(
      Block content, boolean isMultisided) {

    final ResourceLocation contentTop =
        isMultisided
            ? TextureMapping.getBlockTexture(content, "_top")
            : TextureMapping.getBlockTexture(content);
    final ResourceLocation contentBottom =
        isMultisided
            ? TextureMapping.getBlockTexture(content, "_bottom")
            : TextureMapping.getBlockTexture(content);

    return new TextureMapping()
        .put(UntappedTextureSlot.TRANSLUCENT_TOP, contentTop)
        .put(UntappedTextureSlot.TRANSLUCENT_BOTTOM, contentBottom);
  }

  private static TextureMapping createMagmaCauldronTextures() {
    final ResourceLocation content = ResourceLocation.withDefaultNamespace("block/magma");

    return new TextureMapping()
        .put(UntappedTextureSlot.TRANSLUCENT_TOP, content)
        .put(UntappedTextureSlot.TRANSLUCENT_BOTTOM, content);
  }

  private static void createDyedWaterCauldron(BlockModelGenerators blockStateModelGenerator) {
    final Block block = UntappedBlocks.DYED_WATER_CAULDRON;
    final TextureMapping texture =
        TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.WATER, "_still"));
    final String location = ModelLocationUtils.getModelLocation(block).getPath();

    final ResourceLocation level1Model =
        UntappedResourceLocation.withDefaultNamespace(location + "_level1");
    final ResourceLocation level2Model =
        UntappedResourceLocation.withDefaultNamespace(location + "_level2");
    final ResourceLocation fullModel =
        UntappedResourceLocation.withDefaultNamespace(location + "_full");

    ModelTemplates.CAULDRON_LEVEL1.create(
        level1Model, texture, blockStateModelGenerator.modelOutput);
    ModelTemplates.CAULDRON_LEVEL2.create(
        level2Model, texture, blockStateModelGenerator.modelOutput);
    ModelTemplates.CAULDRON_FULL.create(fullModel, texture, blockStateModelGenerator.modelOutput);

    blockStateModelGenerator.blockStateOutput.accept(
        MultiVariantGenerator.dispatch(block)
            .with(
                PropertyDispatch.initial(UntappedDyedWaterCauldronBlock.LEVEL)
                    .select(1, BlockModelGenerators.plainVariant(level1Model))
                    .select(2, BlockModelGenerators.plainVariant(level2Model))
                    .select(3, BlockModelGenerators.plainVariant(fullModel))));
  }

  private static void createMagmaCauldron(BlockModelGenerators blockStateModelGenerator) {
    final Block block = UntappedBlocks.MAGMA_CAULDRON;
    final TextureMapping textures = createMagmaCauldronTextures();
    final String location = ModelLocationUtils.getModelLocation(block).getPath();

    final ResourceLocation level1Model =
        UntappedResourceLocation.withDefaultNamespace(location + "_level1");
    final ResourceLocation level2Model =
        UntappedResourceLocation.withDefaultNamespace(location + "_level2");
    final ResourceLocation level3Model =
        UntappedResourceLocation.withDefaultNamespace(location + "_level3");
    final ResourceLocation fullModel =
        UntappedResourceLocation.withDefaultNamespace(location + "_full");

    UntappedModelTemplates.FOUR_LAYERED_CAULDRON_LEVEL1.create(
        level1Model, textures, blockStateModelGenerator.modelOutput);
    UntappedModelTemplates.FOUR_LAYERED_CAULDRON_LEVEL2.create(
        level2Model, textures, blockStateModelGenerator.modelOutput);
    UntappedModelTemplates.FOUR_LAYERED_CAULDRON_LEVEL3.create(
        level3Model, textures, blockStateModelGenerator.modelOutput);
    UntappedModelTemplates.FOUR_LAYERED_CAULDRON_FULL.create(
        fullModel, textures, blockStateModelGenerator.modelOutput);

    blockStateModelGenerator.blockStateOutput.accept(
        MultiVariantGenerator.dispatch(block)
            .with(
                PropertyDispatch.initial(UntappedMagmaCauldronBlock.LEVEL)
                    .select(1, BlockModelGenerators.plainVariant(level1Model))
                    .select(2, BlockModelGenerators.plainVariant(level2Model))
                    .select(3, BlockModelGenerators.plainVariant(level3Model))
                    .select(4, BlockModelGenerators.plainVariant(fullModel))));
  }

  @Override
  public void generateBlockStateModels(@NotNull BlockModelGenerators blockStateModelGenerator) {
    createHoneyCauldron(blockStateModelGenerator);
    createFrozenCauldron(blockStateModelGenerator);
    createPotionCauldron(blockStateModelGenerator);
    createDyedWaterCauldron(blockStateModelGenerator);
    createSlimeCauldron(blockStateModelGenerator);
    createMagmaCauldron(blockStateModelGenerator);
  }

  @Override
  public void generateItemModels(@NotNull ItemModelGenerators itemModelGenerator) {
    itemModelGenerator.generateFlatItem(UntappedItems.QUIVER, ModelTemplates.FLAT_ITEM);
  }
}
