package com.github.unreference.untapped.world.level.block.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public final class UntappedPotionCauldronBlockEntity extends BlockEntity
    implements RenderDataBlockEntity {
  public static final int ARROW_POTENCY_PER_LEVEL = 8;

  private PotionContents potionContents = PotionContents.EMPTY;
  private Item potionType = Items.POTION;
  private int arrowPotency = 0;

  public UntappedPotionCauldronBlockEntity(BlockPos blockPos, BlockState blockState) {
    super(UntappedBlockEntityType.POTION_CAULDRON, blockPos, blockState);
  }

  public void setContents(PotionContents potionContents, Item potionType, int fillLevel) {
    this.potionContents = potionContents;
    this.potionType = potionType;
    this.arrowPotency = fillLevel * ARROW_POTENCY_PER_LEVEL;
    this.setChanged();

    if (this.level instanceof ServerLevel serverLevel) {
      final BlockState blockState = this.getBlockState();
      serverLevel.sendBlockUpdated(
          this.worldPosition, blockState, blockState, Block.UPDATE_CLIENTS);
    }
  }

  public PotionContents getPotionContents() {
    return this.potionContents;
  }

  public Item getPotionType() {
    return this.potionType;
  }

  public int getArrowPotency() {
    return this.arrowPotency;
  }

  public void setArrowPotency(int potency) {
    this.arrowPotency = potency;
  }

  public List<MobEffectInstance> getAllEffects() {
    final List<MobEffectInstance> effects = new ArrayList<>(this.potionContents.customEffects());
    this.potionContents.potion().ifPresent(p -> effects.addAll(p.value().getEffects()));
    return effects;
  }

  public Optional<Holder<Potion>> getPotion() {
    return this.potionContents.potion();
  }

  public void savePotionContentsToItemStack(ItemStack itemStack) {
    itemStack.set(DataComponents.POTION_CONTENTS, this.potionContents);
  }

  public boolean isWater() {
    return this.potionContents.is(Potions.WATER)
        || this.potionContents.is(Potions.MUNDANE)
        || this.potionContents.is(Potions.AWKWARD)
        || this.potionContents.is(Potions.THICK);
  }

  @Override
  protected void saveAdditional(ValueOutput valueOutput) {
    super.saveAdditional(valueOutput);
    if (this.potionContents != PotionContents.EMPTY) {
      valueOutput.store("potion_contents", PotionContents.CODEC, this.potionContents);
    } else {
      valueOutput.discard("potion_contents");
    }

    valueOutput.store("potion_type", BuiltInRegistries.ITEM.byNameCodec(), this.potionType);
    valueOutput.putInt("arrow_potency", this.arrowPotency);
  }

  @Override
  protected void loadAdditional(ValueInput valueInput) {
    super.loadAdditional(valueInput);

    this.potionContents =
        valueInput.read("potion_contents", PotionContents.CODEC).orElse(PotionContents.EMPTY);
    this.potionType =
        valueInput.read("potion_type", BuiltInRegistries.ITEM.byNameCodec()).orElse(Items.POTION);
    this.arrowPotency = valueInput.getIntOr("arrow_potency", 0);

    if (this.level != null) {
      final BlockState blockState = this.getBlockState();
      this.level.sendBlockUpdated(this.worldPosition, blockState, blockState, 0);
    }
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

    tag.putString("potion_type", BuiltInRegistries.ITEM.getKey(this.potionType).toString());
    tag.putInt("arrow_potency", this.arrowPotency);
    return tag;
  }

  @Override
  public @NotNull Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public @NotNull Object getRenderData() {
    return this.potionContents.getColor();
  }
}
