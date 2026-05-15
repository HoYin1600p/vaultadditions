package io.github.a1qs.vaultadditions.mixins.buildinggadgets;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.logging.LogUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Fix: StackHandlerItemHandle.match() calls handler.extractItem(slot, count, simulate).
 * SophisticatedCore (and some other mods) cap extractItem at existing.getMaxStackSize()
 * (vanilla 64) even when the slot holds more due to stack upgrades.
 *
 * When simulate=true (the material-availability check), this causes Building Gadgets to
 * see only 64 items available in a slot that actually contains 200+, so all quantities
 * above 64 show red in the material list and are not placed.
 *
 * Fix: after match() returns, if simulate=true and the result looks capped, replace it
 * with Math.min(stack.getCount(), count) — reading the real slot count directly.
 * This is safe because:
 *  - returned > 0 means the item DID match (the original's matches() check already passed)
 *  - We only touch simulate=true; actual single-block extractions (simulate=false) are << 64
 *  - We never return more than 'count' (what was requested)
 *
 * Targets: buildinggadgets-3.13.2-build.21+mc1.18.2
 */
@Restriction(require = @Condition(type = Condition.Type.MOD, value = "buildinggadgets"))
@Pseudo
@Mixin(targets = "com.direwolf20.buildinggadgets.common.tainted.inventory.handle.StackHandlerItemHandle", remap = false)
public abstract class MixinStackHandlerItemHandle {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Shadow
    private IItemHandler handler;

    @Shadow
    private int slot;

    @ModifyReturnValue(method = "match", at = @At("RETURN"), remap = false)
    private int vaultadditions$fixSimulatedExtractionCap(
            int returned,
            @Local(argsOnly = true, ordinal = 0) int count,
            @Local(argsOnly = true, ordinal = 0) boolean simulate) {

        // returned == 0 → item didn't match; leave it
        if (returned == 0) return 0;
        // Only fix the simulate=true (counting) path
        if (!simulate) return returned;
        // Already satisfied — no cap issue
        if (returned >= count) return returned;

        // extractItem(slot, count, true) was capped (e.g. SophisticatedCore caps at maxStackSize=64).
        // Read the real stack count directly instead.
        ItemStack stack = this.handler.getStackInSlot(this.slot);
        if (stack.isEmpty()) return 0;

        int corrected = Math.min(stack.getCount(), count);
        if (corrected > returned) {
            LOGGER.debug("[VaultAdditions/BG] slot {} simulate cap corrected: extractItem gave {} but slot has {} (need {})",
                    this.slot, returned, stack.getCount(), count);
        }
        return corrected;
    }
}
