package io.github.a1qs.vaultadditions.mixins.ae2wtlib_compat;

import appeng.client.gui.me.items.CraftingTermScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.TabButton;
import com.mojang.blaze3d.vertex.PoseStack;
import de.mari_023.ae2wtlib.wct.WCTMenu;
import de.mari_023.ae2wtlib.wct.WCTScreen;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = {
        @Condition(type = Condition.Type.MOD, value = "ae2wtlib"),
        @Condition(type = Condition.Type.MOD, value = "ae2")
})
@Mixin(value = WCTScreen.class, remap = false)
public abstract class MixinWCTScreen extends CraftingTermScreen<WCTMenu> {
    private MixinWCTScreen(WCTMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    public void init() {
        super.init();
        vaultadditions$moveExternalRightTabsAwayFromViewCells();
    }

    @Inject(method = "updateBeforeRender", at = @At("TAIL"), remap = false)
    private void vaultadditions$moveTabsAfterUpdateBeforeRender(CallbackInfo ci) {
        vaultadditions$moveExternalRightTabsAwayFromViewCells();
    }

    @Inject(method = "m_6305_", at = @At("HEAD"), remap = false)
    private void vaultadditions$moveTabsBeforeRender(PoseStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        vaultadditions$moveExternalRightTabsAwayFromViewCells();
    }

    private void vaultadditions$moveExternalRightTabsAwayFromViewCells() {
        int guiLeft = getGuiLeft();
        int targetX = Math.min(guiLeft + imageWidth + 34, width - 22);
        for (var child : children()) {
            if (child instanceof TabButton tab
                    && tab.getStyle() == TabButton.Style.HORIZONTAL
                    && tab.x > guiLeft + imageWidth / 2) {
                tab.x = targetX;
            }
        }
    }
}
