package io.github.a1qs.vaultadditions.client.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.a1qs.vaultadditions.container.AltarDoubleChestContainer;
import io.github.a1qs.vaultadditions.mixins.accessors.AccessorSlotPosition;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class AltarDoubleChestScreen extends AbstractContainerScreen<AltarDoubleChestContainer> {
    private static final ResourceLocation CONTAINER_TEXTURE =
            new ResourceLocation("textures/gui/container/generic_54.png");
    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_TOP = 18;
    private static final int SCROLLBAR_HEIGHT = 90;
    private static final int SCROLLBAR_THUMB_HEIGHT = 15;
    private static final int MAX_SCROLL_ROW =
            AltarDoubleChestContainer.CHEST_ROWS - AltarDoubleChestContainer.VISIBLE_ROWS;

    private int scrollRow;
    private boolean draggingScrollbar;

    public AltarDoubleChestScreen(
            AltarDoubleChestContainer menu,
            Inventory playerInventory,
            Component title
    ) {
        super(menu, playerInventory, title);
        this.imageHeight = 114 + AltarDoubleChestContainer.VISIBLE_ROWS * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.updateChestSlotPositions();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, CONTAINER_TEXTURE);

        int chestHeight = AltarDoubleChestContainer.VISIBLE_ROWS * 18 + 17;
        this.blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, chestHeight);
        this.blit(
                poseStack,
                this.leftPos,
                this.topPos + chestHeight,
                0,
                126,
                this.imageWidth,
                96
        );

        int trackX = this.leftPos + this.imageWidth + 4;
        int trackY = this.topPos + SCROLLBAR_TOP;
        fill(poseStack, trackX, trackY, trackX + SCROLLBAR_WIDTH, trackY + SCROLLBAR_HEIGHT, 0xFF1B1B1B);
        fill(poseStack, trackX + 1, trackY + 1, trackX + SCROLLBAR_WIDTH - 1, trackY + SCROLLBAR_HEIGHT - 1, 0xFF4A4A4A);

        int thumbY = trackY + Math.round(
                (SCROLLBAR_HEIGHT - SCROLLBAR_THUMB_HEIGHT) * (this.scrollRow / (float) MAX_SCROLL_ROW)
        );
        fill(poseStack, trackX + 1, thumbY, trackX + SCROLLBAR_WIDTH - 1, thumbY + SCROLLBAR_THUMB_HEIGHT, 0xFF8B8B8B);
        fill(poseStack, trackX + 2, thumbY + 1, trackX + SCROLLBAR_WIDTH - 2, thumbY + 3, 0xFFC6C6C6);
        fill(
                poseStack,
                trackX + 2,
                thumbY + SCROLLBAR_THUMB_HEIGHT - 2,
                trackX + SCROLLBAR_WIDTH - 2,
                thumbY + SCROLLBAR_THUMB_HEIGHT - 1,
                0xFF303030
        );
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta != 0.0D) {
            this.setScrollRow(this.scrollRow - (int) Math.signum(delta));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.isOverScrollbar(mouseX, mouseY)) {
            this.draggingScrollbar = true;
            this.scrollTo(mouseY);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.draggingScrollbar && button == 0) {
            this.scrollTo(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && this.draggingScrollbar) {
            this.draggingScrollbar = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean isOverScrollbar(double mouseX, double mouseY) {
        int x = this.leftPos + this.imageWidth + 4;
        int y = this.topPos + SCROLLBAR_TOP;
        return mouseX >= x
                && mouseX < x + SCROLLBAR_WIDTH
                && mouseY >= y
                && mouseY < y + SCROLLBAR_HEIGHT;
    }

    private void scrollTo(double mouseY) {
        int trackY = this.topPos + SCROLLBAR_TOP;
        float scroll = (float) (mouseY - trackY - SCROLLBAR_THUMB_HEIGHT / 2.0D)
                / (SCROLLBAR_HEIGHT - SCROLLBAR_THUMB_HEIGHT);
        this.setScrollRow(Math.round(scroll * MAX_SCROLL_ROW));
    }

    private void setScrollRow(int row) {
        int clamped = Math.max(0, Math.min(MAX_SCROLL_ROW, row));
        if (clamped != this.scrollRow) {
            this.scrollRow = clamped;
            this.updateChestSlotPositions();
        }
    }

    private void updateChestSlotPositions() {
        for (int index = 0; index < AltarDoubleChestContainer.CHEST_SLOT_COUNT; index++) {
            Slot slot = this.menu.slots.get(index);
            AccessorSlotPosition position = (AccessorSlotPosition) slot;
            int row = index / AltarDoubleChestContainer.CHEST_COLUMNS;
            if (row >= this.scrollRow
                    && row < this.scrollRow + AltarDoubleChestContainer.VISIBLE_ROWS) {
                position.vaultadditions$setX(8 + index % AltarDoubleChestContainer.CHEST_COLUMNS * 18);
                position.vaultadditions$setY(18 + (row - this.scrollRow) * 18);
            } else {
                position.vaultadditions$setX(-10_000);
                position.vaultadditions$setY(-10_000);
            }
        }
    }
}
