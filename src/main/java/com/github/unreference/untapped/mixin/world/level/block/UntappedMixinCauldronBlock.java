package com.github.unreference.untapped.mixin.world.level.block;

import com.github.unreference.untapped.world.level.block.entity.UntappedHoneyCauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CauldronBlock.class)
public abstract class UntappedMixinCauldronBlock extends AbstractCauldronBlock {
  public UntappedMixinCauldronBlock(
      Properties properties, CauldronInteraction.InteractionMap interactionMap) {
    super(properties, interactionMap);
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
    if (blockState.is(Blocks.CAULDRON)) {
      final BlockPos beehiveBlockPos =
          UntappedHoneyCauldronBlockEntity.searchForHive(serverLevel, blockPos);
      if (beehiveBlockPos != null) {
        UntappedHoneyCauldronBlockEntity.transferHoney(
            serverLevel, blockPos, blockState, beehiveBlockPos);
      }
    }
  }
}
