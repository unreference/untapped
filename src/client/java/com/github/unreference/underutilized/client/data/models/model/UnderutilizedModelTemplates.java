package com.github.unreference.underutilized.client.data.models.model;

import com.github.unreference.underutilized.resources.UnderutilizedResourceLocation;
import java.util.Optional;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;

public final class UnderutilizedModelTemplates {
  public static final ModelTemplate TRANSLUCENT_CAULDRON_LEVEL1 =
      create(
          "template_translucent_cauldron_level1",
          TextureSlot.PARTICLE,
          TextureSlot.TOP,
          TextureSlot.BOTTOM,
          TextureSlot.SIDE,
          TextureSlot.INSIDE,
          UnderutilizedTextureSlot.TRANSLUCENT_TOP,
          UnderutilizedTextureSlot.TRANSLUCENT_BOTTOM,
          UnderutilizedTextureSlot.TRANSLUCENT_SIDE);

  public static final ModelTemplate TRANSLUCENT_CAULDRON_LEVEL_2 =
      create(
          "template_translucent_cauldron_level2",
          TextureSlot.PARTICLE,
          TextureSlot.TOP,
          TextureSlot.BOTTOM,
          TextureSlot.SIDE,
          TextureSlot.INSIDE,
          UnderutilizedTextureSlot.TRANSLUCENT_TOP,
          UnderutilizedTextureSlot.TRANSLUCENT_BOTTOM,
          UnderutilizedTextureSlot.TRANSLUCENT_SIDE);

  public static final ModelTemplate TRANSLUCENT_CAULDRON_FULL =
      create(
          "template_translucent_cauldron_full",
          TextureSlot.PARTICLE,
          TextureSlot.TOP,
          TextureSlot.BOTTOM,
          TextureSlot.SIDE,
          TextureSlot.INSIDE,
          UnderutilizedTextureSlot.TRANSLUCENT_TOP,
          UnderutilizedTextureSlot.TRANSLUCENT_BOTTOM,
          UnderutilizedTextureSlot.TRANSLUCENT_SIDE);

  private static ModelTemplate create(String name, TextureSlot... textureSlots) {
    return new ModelTemplate(
        Optional.of(UnderutilizedResourceLocation.withDefaultNamespace("block/" + name)),
        Optional.empty(),
        textureSlots);
  }
}
