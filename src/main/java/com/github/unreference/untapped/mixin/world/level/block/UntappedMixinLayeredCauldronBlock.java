package com.github.unreference.untapped.mixin.world.level.block;

import com.github.unreference.untapped.world.level.block.UntappedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LayeredCauldronBlock.class)
public abstract class UntappedMixinLayeredCauldronBlock extends Block {
  public UntappedMixinLayeredCauldronBlock(Properties properties) {
    super(properties);
  }

  @ModifyArg(
      method = "<init>",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/level/block/AbstractCauldronBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;Lnet/minecraft/core/cauldron/CauldronInteraction$InteractionMap;)V"),
      index = 0)
  private static BlockBehaviour.Properties init(Properties properties) {
    return properties.randomTicks();
  }

  @Override
  protected void randomTick(
      BlockState blockState,
      ServerLevel serverLevel,
      BlockPos blockPos,
      RandomSource randomSource) {
    if (blockState.is(Blocks.WATER_CAULDRON)
        && blockState.getValue(LayeredCauldronBlock.LEVEL) == LayeredCauldronBlock.MAX_FILL_LEVEL) {
      if (serverLevel.getBiome(blockPos).value().coldEnoughToSnow(blockPos, 0)) {
        serverLevel.setBlockAndUpdate(blockPos, UntappedBlocks.FROZEN_CAULDRON.defaultBlockState());
        serverLevel.playSound(null, blockPos, SoundEvents.GLASS_PLACE, SoundSource.BLOCKS);
      }
    }
  }
}
