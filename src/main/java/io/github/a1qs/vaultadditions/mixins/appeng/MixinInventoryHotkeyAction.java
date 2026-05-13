package io.github.a1qs.vaultadditions.mixins.appeng;

import appeng.hotkeys.InventoryHotkeyAction;
import io.github.a1qs.vaultadditions.compat.ae2.Ae2CuriosCompat;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Restriction(require = {
        @Condition(type = Condition.Type.MOD, value = "ae2"),
        @Condition(type = Condition.Type.MOD, value = "curios")
})
@Mixin(value = InventoryHotkeyAction.class, remap = false)
public class MixinInventoryHotkeyAction {

    @Shadow @Final private Predicate<ItemStack> locatable;

    @Inject(method = "run", at = @At("RETURN"), cancellable = true)
    private void vaultadditions$searchCurios(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            return;
        }

        cir.setReturnValue(Ae2CuriosCompat.openTerminalFromCurio(player, this.locatable));
    }
}
