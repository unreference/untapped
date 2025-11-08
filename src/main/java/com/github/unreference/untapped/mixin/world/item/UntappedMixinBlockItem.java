package com.github.unreference.untapped.mixin.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class UntappedMixinBlockItem {
  @Shadow @Final private Block block;

  @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
  public void useOn(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir) {
    final Level level = useOnContext.getLevel();
    final BlockPos blockPos = useOnContext.getClickedPos();
    final BlockState blockState = level.getBlockState(blockPos);

    final Player player = useOnContext.getPlayer();
    if (player == null
        || !(blockState.getBlock() instanceof LayeredCauldronBlock)
        || blockState.getValue(LayeredCauldronBlock.LEVEL) == 0) {
      return;
    }

    final ItemStack itemStack = useOnContext.getItemInHand();
    if (this.block instanceof StainedGlassPaneBlock && itemStack.getItem() != Items.GLASS_PANE) {

      if (!level.isClientSide()) {
        final InteractionHand hand = useOnContext.getHand();
        final ItemStack washed = itemStack.transmuteCopy(Items.GLASS_PANE, 1);
        player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, washed, false));
        LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);
      }

      cir.setReturnValue(InteractionResult.SUCCESS);
    }
  }
}
