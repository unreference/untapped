package com.github.unreference.underutilized.mixin.world.level.block;

import com.github.unreference.underutilized.world.level.block.UnderutilizedHoneyCauldronBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.LavaCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LavaCauldronBlock.class)
public abstract class UnderutilizedMixinLavaCauldronBlock extends AbstractCauldronBlock {
  public UnderutilizedMixinLavaCauldronBlock(
      Properties properties, CauldronInteraction.InteractionMap interactionMap) {
    super(properties, interactionMap);
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
      return UnderutilizedHoneyCauldronBlock.fillWithHoney(
          blockState, level, blockPos, player, interactionHand, itemStack);
    }

    return super.useItemOn(
        itemStack, blockState, level, blockPos, player, interactionHand, blockHitResult);
  }
}
