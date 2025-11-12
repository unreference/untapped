package com.github.unreference.untapped.client.data.models.model;

import com.github.unreference.untapped.resources.UntappedResourceLocation;
import java.util.Optional;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;

public final class UntappedModelTemplates {
  public static final ModelTemplate FOUR_LAYERED_CAULDRON_LEVEL1 =
      create(
          "template_four_layered_cauldron_level1",
          UntappedTextureSlot.TRANSLUCENT_TOP,
          UntappedTextureSlot.TRANSLUCENT_BOTTOM);

  public static final ModelTemplate FOUR_LAYERED_CAULDRON_LEVEL2 =
      create(
          "template_four_layered_cauldron_level2",
          UntappedTextureSlot.TRANSLUCENT_TOP,
          UntappedTextureSlot.TRANSLUCENT_BOTTOM);

  public static final ModelTemplate FOUR_LAYERED_CAULDRON_LEVEL3 =
      create(
          "template_four_layered_cauldron_level3",
          UntappedTextureSlot.TRANSLUCENT_TOP,
          UntappedTextureSlot.TRANSLUCENT_BOTTOM);

  public static final ModelTemplate FOUR_LAYERED_CAULDRON_FULL =
      create(
          "template_four_layered_cauldron_full",
          UntappedTextureSlot.TRANSLUCENT_TOP,
          UntappedTextureSlot.TRANSLUCENT_BOTTOM);

  private static ModelTemplate create(String name, TextureSlot... textureSlots) {
    return new ModelTemplate(
        Optional.of(UntappedResourceLocation.withDefaultNamespace("block/" + name)),
        Optional.empty(),
        textureSlots);
  }
}
