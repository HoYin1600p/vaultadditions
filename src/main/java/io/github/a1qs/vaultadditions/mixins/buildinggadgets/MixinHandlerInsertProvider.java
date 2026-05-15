package io.github.a1qs.vaultadditions.mixins.buildinggadgets;

import com.mojang.logging.LogUtils;
import io.github.a1qs.vaultadditions.config.ServerConfigs;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Fixes two separate bugs in HandlerInsertProvider (Building Gadgets 3.13.2).
 *
 * ── BUG A: Wrong parameter order (root cause of the Undo crash) ──────────────
 * Original code:
 *   stack = remoteInventory.insertItem(count, stack, simulate);
 *                                       ^^^^^
 *                                       passing 'count' (e.g. 200) as the SLOT index
 *
 * Forge IItemHandler signature: insertItem(int slot, ItemStack stack, boolean simulate)
 * When count > number of slots (common during Undo of large builds), every inventory
 * that validates slot bounds (AE2's InternalInventory uses Preconditions.checkArgument)
 * throws IllegalArgumentException: slot out of range, crashing the server tick thread.
 *
 * Fix: pass the loop variable 'i' (the actual slot index) instead of 'count'.
 *
 * ── BUG B: No exception safety ──────────────────────────────────────────────
 * Any RuntimeException from a provider propagates up through UndoScheduler.advance()
 * into ServerTickingScheduler.tick() and kills the server.
 * Fix: catch, log, and skip the provider; remaining items fall back to
 * PlayerInventoryInsertProvider (player inventory / on-ground drop). No items voided.
 *
 * ── AE2 blacklist (config-gated) ────────────────────────────────────────────
 * When enableAe2BuildingGadgetsProvider = false (default), AE2-backed handlers are
 * skipped entirely, preventing the crash even if the parameter-order fix somehow does
 * not apply. Default is OFF because AE2's InternalInventory has strict slot validation
 * that can still throw for other edge cases (network topology changes mid-undo, etc.).
 *
 * Targets: buildinggadgets-3.13.2-build.21+mc1.18.2
 */
@Restriction(require = @Condition(type = Condition.Type.MOD, value = "buildinggadgets"))
@Pseudo
@Mixin(targets = "com.direwolf20.buildinggadgets.common.tainted.inventory.HandlerInsertProvider", remap = false)
public abstract class MixinHandlerInsertProvider {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Shadow
    private IItemHandler remoteInventory;

    /**
     * @author VaultAdditions
     * @reason Fix slot-index parameter order and add exception safety + AE2 blacklist.
     */
    @Overwrite(remap = false)
    public int insert(ItemStack stack, int count, boolean simulate) {
        if (isAe2Handler(remoteInventory)) {
            if (!ServerConfigs.ENABLE_AE2_BUILDING_GADGETS_PROVIDER.get()) {
                LOGGER.debug("[VaultAdditions/BG] AE2 provider disabled via config — skipping insert of {}×{} (handler: {})",
                        count, stack.getItem().getRegistryName(), remoteInventory.getClass().getSimpleName());
                return 0;
            }
            LOGGER.debug("[VaultAdditions/BG] AE2 provider enabled — attempting insert of {}×{} into {}",
                    count, stack.getItem().getRegistryName(), remoteInventory.getClass().getSimpleName());
        }

        int slots = remoteInventory.getSlots();
        for (int i = 0; i < slots; i++) {
            if (stack.isEmpty()) return count;
            try {
                // Fix: was insertItem(count, stack, simulate) — 'count' is NOT a slot index.
                stack = remoteInventory.insertItem(i, stack, simulate);
            } catch (Exception e) {
                LOGGER.error("[VaultAdditions/BG] Provider {} threw {} on slot {}/{} during insert — " +
                                "aborting provider, {} remaining item(s) will go to player inventory or drop",
                        remoteInventory.getClass().getName(),
                        e.getClass().getSimpleName(),
                        i, slots,
                        stack.getCount());
                return 0;
            }
        }
        return count - stack.getCount();
    }

    private static boolean isAe2Handler(IItemHandler handler) {
        return handler.getClass().getName().startsWith("appeng.");
    }
}
