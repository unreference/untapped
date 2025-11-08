package com.github.unreference.untapped.client.data.models.model;

import com.github.unreference.untapped.resources.UntappedResourceLocation;
import java.util.Optional;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;

public final class UntappedModelTemplates {
  public static final ModelTemplate TRANSLUCENT_CAULDRON_LEVEL1 =
      create(
          "template_translucent_cauldron_level1",
          TextureSlot.PARTICLE,
          TextureSlot.TOP,
          TextureSlot.BOTTOM,
          TextureSlot.SIDE,
          TextureSlot.INSIDE,
          UntappedTextureSlot.TRANSLUCENT_TOP,
          UntappedTextureSlot.TRANSLUCENT_BOTTOM,
          UntappedTextureSlot.TRANSLUCENT_SIDE);

  public static final ModelTemplate TRANSLUCENT_CAULDRON_LEVEL_2 =
      create(
          "template_translucent_cauldron_level2",
          TextureSlot.PARTICLE,
          TextureSlot.TOP,
          TextureSlot.BOTTOM,
          TextureSlot.SIDE,
          TextureSlot.INSIDE,
          UntappedTextureSlot.TRANSLUCENT_TOP,
          UntappedTextureSlot.TRANSLUCENT_BOTTOM,
          UntappedTextureSlot.TRANSLUCENT_SIDE);

  public static final ModelTemplate TRANSLUCENT_CAULDRON_FULL =
      create(
          "template_translucent_cauldron_full",
          TextureSlot.PARTICLE,
          TextureSlot.TOP,
          TextureSlot.BOTTOM,
          TextureSlot.SIDE,
          TextureSlot.INSIDE,
          UntappedTextureSlot.TRANSLUCENT_TOP,
          UntappedTextureSlot.TRANSLUCENT_BOTTOM,
          UntappedTextureSlot.TRANSLUCENT_SIDE);

  private static ModelTemplate create(String name, TextureSlot... textureSlots) {
    return new ModelTemplate(
        Optional.of(UntappedResourceLocation.withDefaultNamespace("block/" + name)),
        Optional.empty(),
        textureSlots);
  }
}
