package io.github.a1qs.vaultadditions.mixins.power_crystals;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.a1qs.vaultadditions.util.MiscUtil;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.render.hud.module.VaultPointsModule;
import iskallia.vault.client.render.hud.module.context.ModuleRenderContext;
import iskallia.vault.init.ModOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VaultPointsModule.class, remap = false)
public abstract class MixinVaultBarOverlay {

    @Shadow
    protected abstract int baseWidth(ModuleRenderContext context);

    @Inject(method = "renderModule(Liskallia/vault/client/render/hud/module/context/ModuleRenderContext;)V", at = @At(value = "INVOKE", target = "Liskallia/vault/client/render/hud/module/context/ModuleRenderContext;isEditing()Z", ordinal = 1))
    private void renderPowerPoints(ModuleRenderContext context, CallbackInfo ci, @Local(name = "poseStack") PoseStack poseStack, @Local(name = "rendered") boolean rendered, @Local(name = "y") int y) {
        Minecraft.getInstance().getProfiler().popPush("batchPowerPointText");
        if (!ModOptions.SHOW_POINT_MESSAGES.getValue() || MiscUtil.unspentPowerPoints == 0 || VaultBarOverlay.vaultLevel < 100) {
            return;
        }

        int width = this.baseWidth(context) + 5;

        MiscUtil.POWER_POINT_SUPPLIER.ifChanged(MixinVaultBarOverlay::vaultadditions$onUnspentPowerPointsChanged);
        Minecraft.getInstance().font.drawShadow(poseStack, MiscUtil.unspentPowerPointComponent, (float)(width - VaultBarOverlay.unspentArchetypePointComponentWidth), (float)y, 16777215);
        y += 12;
        rendered = true;
    }

    @Unique
    private static void vaultadditions$onUnspentPowerPointsChanged(int unspentPowerPoints) {
        MutableComponent cmp = new TextComponent(String.valueOf(unspentPowerPoints)).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(16724414)));
        int absUnspentPowerPoint = Math.abs(unspentPowerPoints);
        MiscUtil.unspentPowerPointComponent = cmp.append((new TextComponent(" unspent power point" + (absUnspentPowerPoint == 1 ? "" : "s"))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(16777215))));
        MiscUtil.unspentPowerPointComponentWidth = Minecraft.getInstance().font.width(MiscUtil.unspentPowerPointComponent);
    }
}
