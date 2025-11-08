package com.github.unreference.underutilized.client.data.models;

import com.github.unreference.underutilized.client.data.models.model.UnderutilizedModelTemplates;
import com.github.unreference.underutilized.client.data.models.model.UnderutilizedTextureSlot;
import com.github.unreference.underutilized.resources.UnderutilizedResourceLocation;
import com.github.unreference.underutilized.world.level.block.UnderutilizedBlocks;
import com.github.unreference.underutilized.world.level.block.UnderutilizedHoneyCauldronBlock;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class UnderutilizedModelProvider extends FabricModelProvider {
  public UnderutilizedModelProvider(FabricDataOutput output) {
    super(output);
  }

  private static void createHoneyCauldron(BlockModelGenerators blockStateModelGenerator) {
    final TextureMapping textures = createHoneyCauldronTextures();

    final ResourceLocation level1Model =
        UnderutilizedResourceLocation.withDefaultNamespace("block/honey_cauldron_level1");
    final ResourceLocation level2Model =
        UnderutilizedResourceLocation.withDefaultNamespace("block/honey_cauldron_level2");
    final ResourceLocation fullModel =
        UnderutilizedResourceLocation.withDefaultNamespace("block/honey_cauldron_full");

    UnderutilizedModelTemplates.TRANSLUCENT_CAULDRON_LEVEL1.create(
        level1Model, textures, blockStateModelGenerator.modelOutput);
    UnderutilizedModelTemplates.TRANSLUCENT_CAULDRON_LEVEL_2.create(
        level2Model, textures, blockStateModelGenerator.modelOutput);
    UnderutilizedModelTemplates.TRANSLUCENT_CAULDRON_FULL.create(
        fullModel, textures, blockStateModelGenerator.modelOutput);

    blockStateModelGenerator.blockStateOutput.accept(
        MultiVariantGenerator.dispatch(UnderutilizedBlocks.HONEY_CAULDRON)
            .with(
                PropertyDispatch.initial(UnderutilizedHoneyCauldronBlock.HONEY_LEVEL)
                    .select(1, BlockModelGenerators.plainVariant(level1Model))
                    .select(2, BlockModelGenerators.plainVariant(level2Model))
                    .select(3, BlockModelGenerators.plainVariant(fullModel))));
  }

  private static TextureMapping createHoneyCauldronTextures() {
    final Block cauldron = Blocks.CAULDRON;
    final Block honeyBlock = Blocks.HONEY_BLOCK;

    final ResourceLocation cauldronSide = TextureMapping.getBlockTexture(cauldron, "_side");
    final ResourceLocation cauldronTop = TextureMapping.getBlockTexture(cauldron, "_top");
    final ResourceLocation cauldronBottom = TextureMapping.getBlockTexture(cauldron, "_bottom");
    final ResourceLocation cauldronInner = TextureMapping.getBlockTexture(cauldron, "_inner");

    final ResourceLocation honeyTop = TextureMapping.getBlockTexture(honeyBlock, "_top");
    final ResourceLocation honeyBottom = TextureMapping.getBlockTexture(honeyBlock, "_bottom");
    final ResourceLocation honeySide = TextureMapping.getBlockTexture(honeyBlock, "_side");

    return new TextureMapping()
        .put(TextureSlot.PARTICLE, cauldronSide)
        .put(TextureSlot.TOP, cauldronTop)
        .put(TextureSlot.BOTTOM, cauldronBottom)
        .put(TextureSlot.SIDE, cauldronSide)
        .put(TextureSlot.INSIDE, cauldronInner)
        .put(UnderutilizedTextureSlot.TRANSLUCENT_TOP, honeyTop)
        .put(UnderutilizedTextureSlot.TRANSLUCENT_BOTTOM, honeyBottom)
        .put(UnderutilizedTextureSlot.TRANSLUCENT_SIDE, honeySide);
  }

  @Override
  public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
    createHoneyCauldron(blockStateModelGenerator);
  }

  @Override
  public void generateItemModels(ItemModelGenerators itemModelGenerator) {}
}
