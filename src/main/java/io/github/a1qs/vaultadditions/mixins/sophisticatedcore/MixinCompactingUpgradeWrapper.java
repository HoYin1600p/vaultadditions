package io.github.a1qs.vaultadditions.mixins.sophisticatedcore;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Prevents compacting fit checks from re-entering live storage insertion.
 *
 * <p>SophisticatedCore's no-remaining-items compacting path checks whether the
 * compacted result fits by calling InventoryHelper.insertIntoInventory(result,
 * handler, true) on the live handler. For InventoryHandler this still goes
 * through the global slot tracker / overflow path, which can recurse through
 * controller or AE2 external-storage insertion during an onAfterInsert compact.
 */
@Restriction(require = @Condition(type = Condition.Type.MOD, value = "sophisticatedcore"))
@Pseudo
@Mixin(targets = "net.p3pp3rf1y.sophisticatedcore.upgrades.compacting.CompactingUpgradeWrapper", remap = false)
public abstract class MixinCompactingUpgradeWrapper {

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
