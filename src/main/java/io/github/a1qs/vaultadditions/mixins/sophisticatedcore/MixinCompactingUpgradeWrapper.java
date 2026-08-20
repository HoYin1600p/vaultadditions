package io.github.a1qs.vaultadditions.mixins.sophisticatedcore;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.p3pp3rf1y.sophisticatedcore.inventory.IItemHandlerSimpleInserter;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashSet;
import java.util.Set;

/**
 * Serializes compacting callbacks and keeps compacting away from Compression's
 * virtual slots. Compression already performs its own recipe conversion.
 */
@Restriction(require = @Condition(type = Condition.Type.MOD, value = "sophisticatedcore"))
@Pseudo
@Mixin(targets = "net.p3pp3rf1y.sophisticatedcore.upgrades.compacting.CompactingUpgradeWrapper", remap = false)
public abstract class MixinCompactingUpgradeWrapper {
    @Unique
    private final Set<Integer> vaultadditions$slotsToCompactAfterCurrent = new HashSet<>();

    @Unique
    private boolean vaultadditions$compacting;

    @WrapMethod(
            method = "compactSlot(Lnet/p3pp3rf1y/sophisticatedcore/inventory/IItemHandlerSimpleInserter;I)V",
            require = 1,
            remap = false
    )
    private void vaultadditions$compactSlotAndQueued(
            IItemHandlerSimpleInserter inventoryHandler,
            int slot,
            Operation<Void> original
    ) {
        if (vaultadditions$isCompressionSlot(inventoryHandler, slot)) {
            return;
        }

        if (vaultadditions$compacting) {
            vaultadditions$slotsToCompactAfterCurrent.add(slot);
            return;
        }

        vaultadditions$compacting = true;
        try {
            original.call(inventoryHandler, slot);
            while (!vaultadditions$slotsToCompactAfterCurrent.isEmpty()) {
                Set<Integer> queuedSlots = new HashSet<>(vaultadditions$slotsToCompactAfterCurrent);
                vaultadditions$slotsToCompactAfterCurrent.clear();
                for (int queuedSlot : queuedSlots) {
                    if (!vaultadditions$isCompressionSlot(inventoryHandler, queuedSlot)) {
                        original.call(inventoryHandler, queuedSlot);
                    }
                }
            }
        } finally {
            vaultadditions$slotsToCompactAfterCurrent.clear();
            vaultadditions$compacting = false;
        }
    }

    @Unique
    private boolean vaultadditions$isCompressionSlot(IItemHandlerSimpleInserter inventoryHandler, int slot) {
        return inventoryHandler instanceof InventoryHandler handler
                && "compression".equals(handler.getInventoryPartitioner().getPartBySlot(slot).getName());
    }
}
