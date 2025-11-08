package com.github.unreference.untapped.mixin.client.renderer.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class UntappedMixinLivingEntityRenderer {
  @ModifyReturnValue(method = "isUpsideDownName", at = @At("RETURN"))
  private static boolean isUpsideDownName(boolean original, String name) {
    if (original) {
      return true;
    }

    return "Unreference".equals(name);
  }
}
