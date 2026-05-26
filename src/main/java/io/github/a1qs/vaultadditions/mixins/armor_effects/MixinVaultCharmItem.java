package io.github.a1qs.vaultadditions.mixins.armor_effects;

import io.github.a1qs.vaultadditions.util.ZarithReputationBonusHelper;
import iskallia.vault.item.gear.VaultCharmItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = VaultCharmItem.class, remap = false)
public class MixinVaultCharmItem {
    @Inject(method = "getGodReputation", at = @At("RETURN"), cancellable = true)
    private static void vaultadditions$returnUncappedZarithCharmReputation(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof VaultCharmItem)) {
            return;
        }

        if (!ZarithReputationBonusHelper.isZarith(VaultCharmItem.getGod(stack).orElse(null))) {
            return;
        }

        int rawReputation = stack.getOrCreateTag().getInt("godReputation");
        if (rawReputation > cir.getReturnValueI()) {
            cir.setReturnValue(rawReputation);
        }
    }
}
