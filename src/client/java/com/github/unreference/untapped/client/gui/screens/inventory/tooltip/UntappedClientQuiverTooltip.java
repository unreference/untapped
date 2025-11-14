package com.github.unreference.untapped.client.gui.screens.inventory.tooltip;

import com.github.unreference.untapped.core.component.UntappedQuiverContents;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public record UntappedClientQuiverTooltip(UntappedQuiverContents contents)
    implements ClientTooltipComponent {
  private static final ResourceLocation PROGRESS_BAR_BORDER_SPRITE =
      ResourceLocation.withDefaultNamespace("container/bundle/bundle_progressbar_border");
  private static final ResourceLocation PROGRESS_BAR_FILL_SPRITE =
      ResourceLocation.withDefaultNamespace("container/bundle/bundle_progressbar_full");
  private static final ResourceLocation PROGRESS_BAR_FULL_SPRITE =
      ResourceLocation.withDefaultNamespace("container/bundle/bundle_progressbar_fill");

  private static final ResourceLocation SLOT_HIGHLIGHT_BACK_SPRITE =
      ResourceLocation.withDefaultNamespace("container/bundle/slot_highlight_back");
  private static final ResourceLocation SLOT_HIGHLIGHT_FRONT_SPRITE =
      ResourceLocation.withDefaultNamespace("container/bundle/slot_highlight_front");
  private static final ResourceLocation SLOT_BACKGROUND_SPRITE =
      ResourceLocation.withDefaultNamespace("container/bundle/slot_background");

  private static final int SLOT_MARGIN = 4;
  private static final int SLOT_SIZE = 24;
  private static final int GRID_WIDTH = 96;
  private static final int PROGRESS_BAR_HEIGHT = 13;
  private static final int PROGRESSBAR_BORDER = 1;
  private static final int PROGRESS_BAR_FILL_MAX = 94;
  private static final int PROGRESS_BAR_MARGIN_Y = 4;
  private static final int GRID_COLS = UntappedQuiverContents.MAX_STACKS;
  private static final int MAX_TOOLTIP_SLOTS = GRID_COLS;

  private static final Component QUIVER_FULL_TEXT =
      Component.translatable("item.minecraft.bundle.full");
  private static final Component QUIVER_EMPTY_TEXT =
      Component.translatable("item.minecraft.bundle.empty");
  private static final Component QUIVER_EMPTY_DESCRIPTION =
      Component.translatable("item.untapped.quiver.empty.description", GRID_COLS);

  private static int getEmptyQuiverBackgroundHeight(Font font) {
    return getEmptyQuiverDescriptionTextHeight(font)
        + PROGRESS_BAR_HEIGHT
        + PROGRESS_BAR_MARGIN_Y * 2;
  }

  private static int getEmptyQuiverDescriptionTextHeight(Font font) {
    return font.split(QUIVER_EMPTY_DESCRIPTION, GRID_WIDTH).size() * 9;
  }

  private static void getRenderCount(int x, int y, int count, Font font, GuiGraphics guiGraphics) {
    guiGraphics.drawCenteredString(font, "+" + count, x + 12, y + 10, -1);
  }

  private static void drawEmptyQuiverDescriptionText(
      int x, int y, Font font, GuiGraphics guiGraphics) {
    guiGraphics.drawWordWrap(font, QUIVER_EMPTY_DESCRIPTION, x, y, GRID_WIDTH, -5592406);
  }

  @Override
  public int getHeight(Font font) {
    return this.contents.empty()
        ? getEmptyQuiverBackgroundHeight(font)
        : this.getBackgroundHeight();
  }

  private boolean isFull() {
    return this.contents.occupancy() >= UntappedQuiverContents.MAX_CAPACITY;
  }

  private int getBackgroundHeight() {
    return this.getItemGridHeight() + PROGRESS_BAR_HEIGHT + PROGRESS_BAR_MARGIN_Y * 2;
  }

  private int getItemGridHeight() {
    return this.getGridSizeY() * SLOT_SIZE;
  }

  private int getGridSizeY() {
    return Mth.positiveCeilDiv(this.getSlotCount(), MAX_TOOLTIP_SLOTS);
  }

  private int getSlotCount() {
    return Math.min(MAX_TOOLTIP_SLOTS, this.contents.size());
  }

  @Override
  public int getWidth(Font font) {
    return GRID_WIDTH;
  }

  @Override
  public boolean showTooltipWithItemInHand() {
    return true;
  }

  @Override
  public void renderImage(Font font, int i, int j, int k, int l, GuiGraphics guiGraphics) {
    if (this.contents.empty()) {
      this.renderEmptyQuiverTooltip(font, i, j, k, guiGraphics);
    } else {
      this.renderQuiverWithItemsTooltip(font, i, j, k, guiGraphics);
    }
  }

  private void renderQuiverWithItemsTooltip(
      Font font, int x, int y, int width, GuiGraphics guiGraphics) {
    final boolean hasMoreItems = this.contents.size() > MAX_TOOLTIP_SLOTS;
    final int slotCount = this.getSlotCount();
    final List<ItemStack> shownItems = this.contents.itemsCopyStream().limit(slotCount).toList();

    final int gridLeftX = x + this.getContentXOffset(width);

    int itemIndex = 0;
    for (int row = 0; row < this.getGridSizeY(); row++) {
      for (int col = 0; col < GRID_COLS; col++) {
        final int slotX = gridLeftX + col * SLOT_SIZE;
        final int slotY = y + row * SLOT_SIZE;

        if (hasMoreItems && itemIndex == MAX_TOOLTIP_SLOTS - 1) {
          getRenderCount(slotX, slotY, this.getHiddenItems(slotCount), font, guiGraphics);
        } else if (itemIndex < slotCount) {
          this.renderSlot(itemIndex, slotX, slotY, shownItems, itemIndex, font, guiGraphics);
        }

        itemIndex++;
      }
    }

    this.drawProgressBar(
        x + this.getContentXOffset(width),
        y + this.getItemGridHeight() + PROGRESS_BAR_MARGIN_Y,
        font,
        guiGraphics);
  }

  private void drawProgressBar(int x, int y, Font font, GuiGraphics guiGraphics) {
    guiGraphics.blitSprite(
        RenderPipelines.GUI_TEXTURED,
        this.getProgressBarTexture(),
        x + PROGRESSBAR_BORDER,
        y,
        this.getProgressBarFill(),
        PROGRESS_BAR_HEIGHT);

    guiGraphics.blitSprite(
        RenderPipelines.GUI_TEXTURED,
        PROGRESS_BAR_BORDER_SPRITE,
        x,
        y,
        GRID_WIDTH,
        PROGRESS_BAR_HEIGHT);

    final Component component = this.getProgressBarFillText();
    if (component != null) {
      guiGraphics.drawCenteredString(font, component, x + (GRID_WIDTH / 2), y + 3, -1);
    }
  }

  private ResourceLocation getProgressBarTexture() {
    return this.isFull() ? PROGRESS_BAR_FULL_SPRITE : PROGRESS_BAR_FILL_SPRITE;
  }

  private Component getProgressBarFillText() {
    if (this.contents.empty()) {
      return QUIVER_EMPTY_TEXT;
    }

    return this.isFull() ? QUIVER_FULL_TEXT : null;
  }

  private int getProgressBarFill() {
    final float occupancy =
        (float) this.contents.occupancy() / (float) UntappedQuiverContents.MAX_CAPACITY;

    if (occupancy == 0.0f) {
      return 0;
    }

    return Math.min(1 + (int) (occupancy * (PROGRESS_BAR_FILL_MAX - 1)), PROGRESS_BAR_FILL_MAX);
  }

  private void renderSlot(
      int itemIndex,
      int x,
      int y,
      List<ItemStack> list,
      int height,
      Font font,
      GuiGraphics guiGraphics) {
    final boolean isChosen = itemIndex == 0;

    if (isChosen) {
      guiGraphics.blitSprite(
          RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_SPRITE, x, y, SLOT_SIZE, SLOT_SIZE);
    } else {
      guiGraphics.blitSprite(
          RenderPipelines.GUI_TEXTURED, SLOT_BACKGROUND_SPRITE, x, y, SLOT_SIZE, SLOT_SIZE);
    }

    final ItemStack itemStack = list.get(itemIndex);

    guiGraphics.renderItem(itemStack, x + SLOT_MARGIN, y + SLOT_MARGIN, height);
    guiGraphics.renderItemDecorations(font, itemStack, x + SLOT_MARGIN, y + SLOT_MARGIN);

    if (isChosen) {
      guiGraphics.blitSprite(
          RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_SPRITE, x, y, SLOT_SIZE, SLOT_SIZE);
    }
  }

  private int getHiddenItems(int shown) {
    return this.contents.itemsCopyStream().skip(shown).mapToInt(ItemStack::getCount).sum();
  }

  private int getContentXOffset(int width) {
    return (width - GRID_WIDTH) / 2;
  }

  private void renderEmptyQuiverTooltip(
      Font font, int x, int y, int width, GuiGraphics guiGraphics) {
    drawEmptyQuiverDescriptionText(x + this.getContentXOffset(width), y, font, guiGraphics);
    this.drawProgressBar(
        x + this.getContentXOffset(width),
        y + getEmptyQuiverDescriptionTextHeight(font) + PROGRESS_BAR_MARGIN_Y,
        font,
        guiGraphics);
  }
}
