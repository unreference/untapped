package com.github.unreference.untapped.mixin.world.level.block;

import com.github.unreference.untapped.world.level.block.UntappedHoneyCauldronBlock;
import com.github.unreference.untapped.world.level.block.entity.UntappedHoneyCauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
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

  @Override
  protected @NotNull InteractionResult useItemOn(
      ItemStack itemStack,
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      BlockHitResult blockHitResult) {
    if (itemStack.is(Items.HONEY_BOTTLE)) {
      return UntappedHoneyCauldronBlock.fillWithHoney(
          blockState, level, blockPos, player, interactionHand, itemStack);
    }

    return super.useItemOn(
        itemStack, blockState, level, blockPos, player, interactionHand, blockHitResult);
  }
}
