package io.github.a1qs.vaultadditions.mixins.ae2wtlib_compat;

import com.mojang.blaze3d.vertex.PoseStack;
import de.mari_023.ae2wtlib.wct.PlayerEntityWidget;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = @Condition(type = Condition.Type.MOD, value = "ae2wtlib"))
@Mixin(value = PlayerEntityWidget.class, remap = false)
public abstract class MixinPlayerEntityWidget extends AbstractWidget {
    @Shadow
    @Final
    private LivingEntity entity;

    private MixinPlayerEntityWidget() {
        super(0, 0, 0, 0, TextComponent.EMPTY);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void vaultadditions$setNonZeroSize(LivingEntity entity, CallbackInfo ci) {
        this.width = 32;
        this.height = 54;
    }

    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        InventoryScreen.renderEntityInInventory(this.x, this.y, 30, this.x - mouseX, this.y - 44 - mouseY, this.entity);
    }
}
