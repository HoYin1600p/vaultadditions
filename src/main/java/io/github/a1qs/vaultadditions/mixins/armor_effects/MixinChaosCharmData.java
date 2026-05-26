package io.github.a1qs.vaultadditions.mixins.armor_effects;

import io.github.a1qs.vaultadditions.util.ZarithReputationBonusHelper;
import iskallia.vault.item.gear.VaultCharmItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.dog.vaultchaosgod.item.ChaosCharmData", remap = false)
public class MixinChaosCharmData {
    @Shadow @Final private ItemStack stack;

    @Inject(method = "getGodReputation", at = @At("RETURN"), cancellable = true)
    private void vaultadditions$returnUncappedZarithChaosReputation(CallbackInfoReturnable<Integer> cir) {
        if (!ZarithReputationBonusHelper.isZarith(VaultCharmItem.getGod(this.stack).orElse(null))) {
            return;
        }

        int rawReputation = this.vaultadditions$getRootTag().getInt("godReputation");
        if (rawReputation > cir.getReturnValueI()) {
            cir.setReturnValue(rawReputation);
        }
    }

    @Inject(method = "setGodReputation", at = @At("HEAD"), cancellable = true)
    private void vaultadditions$setUncappedZarithChaosReputation(int reputation, CallbackInfo ci) {
        if (reputation <= 50 || !ZarithReputationBonusHelper.isZarith(VaultCharmItem.getGod(this.stack).orElse(null))) {
            return;
        }

        this.vaultadditions$getRootTag().putInt("godReputation", reputation);
        ci.cancel();
    }

    private CompoundTag vaultadditions$getRootTag() {
        return this.stack.getOrCreateTagElement("vaultchaosgod:chaos_charm_data");
    }
}
