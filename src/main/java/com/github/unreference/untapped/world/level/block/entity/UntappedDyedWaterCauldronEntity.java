package com.github.unreference.untapped.world.level.block.entity;

import com.mojang.serialization.Codec;
import java.util.List;
import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public final class UntappedDyedWaterCauldronEntity extends BlockEntity
    implements RenderDataBlockEntity {
  private int color = DyedItemColor.LEATHER_COLOR;

  public UntappedDyedWaterCauldronEntity(BlockPos blockPos, BlockState blockState) {
    super(UntappedBlockEntityType.DYED_WATER_CAULDRON, blockPos, blockState);
  }

  public int getColor() {
    return this.color;
  }

  public void setColor(int color) {
    if (color == this.color) {
      return;
    }

    this.color = color;
    this.setChanged();

    if (this.level instanceof ServerLevel serverLevel) {
      final BlockState blockState = this.getBlockState();
      serverLevel.sendBlockUpdated(
          this.worldPosition, blockState, blockState, Block.UPDATE_CLIENTS);
    }
  }

  public boolean isDyeable(List<DyeItem> dyes) {
    ItemStack dummy = new ItemStack(Items.LEATHER_CHESTPLATE);

    if (this.color != DyedItemColor.LEATHER_COLOR) {
      dummy.set(DataComponents.DYED_COLOR, new DyedItemColor(this.color));
    }

    dummy = DyedItemColor.applyDyes(dummy, dyes);

    final DyedItemColor dyed = dummy.get(DataComponents.DYED_COLOR);
    final int newColor = dyed != null ? (dyed.rgb() & 0xFFFFFF) : this.color;

    if (newColor != this.color) {
      this.setColor(newColor);
      return true;
    }

    return false;
  }

  @Override
  protected void saveAdditional(ValueOutput valueOutput) {
    super.saveAdditional(valueOutput);
    valueOutput.store("dyed_color", Codec.INT, this.color);
  }

  @Override
  protected void loadAdditional(ValueInput valueInput) {
    super.loadAdditional(valueInput);
    this.color = valueInput.read("dyed_color", Codec.INT).orElse(DyedItemColor.LEATHER_COLOR);

    if (this.level != null) {
      final BlockState blockState = this.getBlockState();
      this.level.sendBlockUpdated(this.worldPosition, blockState, blockState, 0);
    }
  }

  @Override
  public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
    final CompoundTag tag = super.getUpdateTag(provider);
    tag.putInt("dyed_color", this.color);
    return tag;
  }

  @Override
  public @NotNull Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public @NotNull Object getRenderData() {
    return this.color;
  }
}
