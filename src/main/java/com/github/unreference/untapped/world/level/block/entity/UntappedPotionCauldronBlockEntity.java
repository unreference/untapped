package com.github.unreference.untapped.world.level.block.entity;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public final class UntappedPotionCauldronBlockEntity extends BlockEntity {
  private PotionContents potionContents = PotionContents.EMPTY;

  public UntappedPotionCauldronBlockEntity(BlockPos blockPos, BlockState blockState) {
    super(UntappedBlockEntityType.POTION_CAULDRON, blockPos, blockState);
  }

  public void setPotionContents(PotionContents potionContents) {
    this.potionContents = potionContents;
    this.setChanged();

    if (level instanceof ServerLevel serverLevel) {
      serverLevel.getChunkSource().blockChanged(worldPosition);
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }
  }

  public Optional<MobEffectInstance> getEffect() {
    return this.potionContents.potion().flatMap(p -> p.value().getEffects().stream().findFirst());
  }

  public Optional<Holder<Potion>> getPotion() {
    return this.potionContents.potion();
  }

  public int getColor() {
    return this.potionContents.getColor();
  }

  public void saveEffectToItem(ItemStack itemStack) {
    itemStack.set(DataComponents.POTION_CONTENTS, this.potionContents);
  }

  @Override
  protected void saveAdditional(ValueOutput valueOutput) {
    super.saveAdditional(valueOutput);
    if (this.potionContents != PotionContents.EMPTY) {
      valueOutput.store("potion_contents", PotionContents.CODEC, this.potionContents);
    } else {
      valueOutput.discard("potion_contents");
    }
  }

  @Override
  protected void loadAdditional(ValueInput valueInput) {
    super.loadAdditional(valueInput);
    valueInput
        .read("potion_contents", PotionContents.CODEC)
        .ifPresentOrElse(
            c -> this.potionContents = c, () -> this.potionContents = PotionContents.EMPTY);
  }

  @Override
  public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
    final CompoundTag tag = super.getUpdateTag(provider);
    if (this.potionContents != PotionContents.EMPTY) {
      PotionContents.CODEC
          .encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this.potionContents)
          .resultOrPartial()
          .ifPresent(t -> tag.put("potion_contents", t));
    }

    return tag;
  }

  @Override
  public @NotNull Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public @NotNull Object getRenderData() {
    return this.getColor();
  }

  @Override
  public void setRemoved() {
    super.setRemoved();
    this.potionContents = PotionContents.EMPTY;
  }
}
