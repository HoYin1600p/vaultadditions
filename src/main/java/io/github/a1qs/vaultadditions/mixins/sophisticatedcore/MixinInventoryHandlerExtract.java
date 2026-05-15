package io.github.a1qs.vaultadditions.mixins.sophisticatedcore;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.logging.LogUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Fix: SophisticatedCore's InventoryHandler.extractItemInternal() caps extraction at
 * existing.getMaxStackSize() (vanilla 64), but stack-upgraded slots can hold much more.
 *
 * Building Gadgets calls handler.extractItem(slot, needed, simulate) exactly once per slot.
 * If the slot has 256 stone (4× stack upgrade) and BG needs 200, SophisticatedCore returns
 * only 64 (vanilla cap). BG marks the remaining 136 as "missing" in the paste UI.
 *
 * Fix: replace the vanilla-max cap with max(vanillaMax, slotLimit), so stack-upgraded
 * slots yield their full contents in a single extractItem call.
 *
 * Targets: sophisticatedcore-1.18.2-0.6.x
 */
@Restriction(require = @Condition(type = Condition.Type.MOD, value = "sophisticatedcore"))
@Pseudo
@Mixin(targets = "net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler", remap = false)
public abstract class MixinInventoryHandlerExtract {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Shadow
    public abstract int getSlotLimit(int slot);

    /**
     * Intercepts the local variable {@code toExtract} immediately after it is computed as
     * {@code Math.min(amount, existing.getMaxStackSize())} and raises the cap to
     * {@code Math.min(amount, max(vanillaMax, slotLimit))}.
     *
     * <p>ordinal = 0 at STORE targets the first int stored in extractItemInternal,
     * which is always {@code toExtract} given the method's structure:
     * <pre>
     *   validateSlotIndex(slot);          // no ISTORE
     *   ItemStack existing = …;           // ASTORE, not ISTORE
     *   int toExtract = Math.min(…);      // ISTORE ordinal 0  ← we intercept here
     * </pre>
     *
     * require = 0: silently skips if SophisticatedCore internals changed across versions.
     */
    @ModifyVariable(
            method = "extractItemInternal",
            at = @At(value = "STORE", ordinal = 0),
            require = 0,
            remap = false
    )
    private int vaultadditions$capExtractionAtSlotLimit(
            int toExtract,
            @Local(argsOnly = true, ordinal = 0) int slot,
            @Local(argsOnly = true, ordinal = 1) int amount) {

        int slotLimit = this.getSlotLimit(slot);
        int corrected = Math.min(amount, Math.max(toExtract, slotLimit));

        if (corrected != toExtract) {
            LOGGER.debug("[VaultAdditions/SC] Slot {} stack-upgrade cap: was {} → {} (slotLimit={})",
                    slot, toExtract, corrected, slotLimit);
        }

        return corrected;
    }
}
