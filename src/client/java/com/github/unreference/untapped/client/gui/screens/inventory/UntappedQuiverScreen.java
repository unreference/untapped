package com.github.unreference.untapped.client.gui.screens.inventory;

import com.github.unreference.untapped.resources.UntappedResourceLocation;
import com.github.unreference.untapped.world.inventory.UntappedQuiverMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class UntappedQuiverScreen extends AbstractContainerScreen<UntappedQuiverMenu> {
  private static final ResourceLocation CONTAINER_BACKGROUND =
      UntappedResourceLocation.withDefaultNamespace("textures/gui/container/quiver.png");

  private static final int PLAYER_INVENTORY_LABEL_Y_OFFSET = 94;
  private static final int BASE_CONTAINER_HEIGHT = 133;

  public UntappedQuiverScreen(
      UntappedQuiverMenu abstractContainerMenu, Inventory inventory, Component component) {
    super(abstractContainerMenu, inventory, component);

    this.imageHeight = BASE_CONTAINER_HEIGHT;
    this.inventoryLabelY = this.imageHeight - PLAYER_INVENTORY_LABEL_Y_OFFSET;
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float f, int i, int j) {
    final int x = (this.width - this.imageWidth) / 2;
    final int y = (this.height - this.imageHeight) / 2;

    guiGraphics.blit(
        RenderPipelines.GUI_TEXTURED,
        CONTAINER_BACKGROUND,
        x,
        y,
        0.0f,
        0.0f,
        this.imageWidth,
        this.imageHeight,
        BACKGROUND_TEXTURE_WIDTH,
        BACKGROUND_TEXTURE_HEIGHT);
  }

  @Override
  public void render(GuiGraphics guiGraphics, int i, int j, float f) {
    super.render(guiGraphics, i, j, f);
    this.renderTooltip(guiGraphics, i, j);
  }
}
