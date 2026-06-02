package io.github.a1qs.vaultadditions.mixins.sophisticatedcore;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Prevents compacting from re-entering live storage insertion.
 *
 * <p>SophisticatedCore's no-remaining-items compacting path checks whether the
 * compacted result fits by calling InventoryHelper.insertIntoInventory(result,
 * handler, true) on the live handler. For InventoryHandler this still goes
 * through the global slot tracker / overflow path, which can recurse through
 * controller or AE2 external-storage insertion during an onAfterInsert compact.
 *
 * <p>The actual compact result insert can also fire onAfterInsert immediately
 * while compacting is already in progress. Guard compactSlot per thread so a
 * compacting upgrade does not recursively compact while it is inserting its own
 * result back into the same storage network.
 */
@Restriction(require = @Condition(type = Condition.Type.MOD, value = "sophisticatedcore"))
@Pseudo
@Mixin(targets = "net.p3pp3rf1y.sophisticatedcore.upgrades.compacting.CompactingUpgradeWrapper", remap = false)
public abstract class MixinCompactingUpgradeWrapper {
    private static final ThreadLocal<Boolean> VAULTADDITIONS_COMPACTING = ThreadLocal.withInitial(() -> false);

    @Inject(
            method = "compactSlot(Lnet/p3pp3rf1y/sophisticatedcore/inventory/IItemHandlerSimpleInserter;I)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0,
            remap = false
    )
    private void vaultadditions$skipNestedCompacting(Object itemHandler, int slot, CallbackInfo ci) {
        if (VAULTADDITIONS_COMPACTING.get()) {
            ci.cancel();
            return;
        }

        VAULTADDITIONS_COMPACTING.set(true);
    }

    @Inject(
            method = "compactSlot(Lnet/p3pp3rf1y/sophisticatedcore/inventory/IItemHandlerSimpleInserter;I)V",
            at = @At("RETURN"),
            require = 0,
            remap = false
    )
    private void vaultadditions$clearCompactingGuard(Object itemHandler, int slot, CallbackInfo ci) {
        VAULTADDITIONS_COMPACTING.set(false);
    }

    @Redirect(
            method = "fitsResultAndRemainingItems",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/p3pp3rf1y/sophisticatedcore/util/InventoryHelper;insertIntoInventory(Lnet/minecraft/world/item/ItemStack;Lnet/minecraftforge/items/IItemHandler;Z)Lnet/minecraft/world/item/ItemStack;",
                    ordinal = 1
            ),
            require = 0,
            remap = false
    )
    private ItemStack vaultadditions$checkResultFitInSnapshot(ItemStack stack, IItemHandler handler, boolean simulate) {
        ItemStack remaining = stack.copy();
        IItemHandler snapshot = vaultadditions$copyForFitCheck(handler);

        for (int slot = 0; slot < snapshot.getSlots() && !remaining.isEmpty(); slot++) {
            remaining = snapshot.insertItem(slot, remaining, false);
        }

        return remaining;
    }

    private IItemHandler vaultadditions$copyForFitCheck(IItemHandler original) {
        ItemStackHandler snapshot = new ItemStackHandler(original.getSlots()) {
            @Override
            public int getSlotLimit(int slot) {
                return original.getSlotLimit(slot);
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return original.isItemValid(slot, stack);
            }
        };

        for (int slot = 0; slot < original.getSlots(); slot++) {
            snapshot.setStackInSlot(slot, original.getStackInSlot(slot).copy());
        }

        return snapshot;
    }
}
