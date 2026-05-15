package io.github.a1qs.vaultadditions.compat.ae2;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Presents an AE2 ME network's storage as a Forge IItemHandler with virtual slots —
 * one slot per unique item type currently stored in the network.
 *
 * Slot indices are rebuilt whenever getSlots() is called, then stay stable until
 * the next getSlots() call.  Building Gadgets calls getSlots() once per paste/undo
 * operation, so the mapping is consistent across the simulate→modulate pair.
 */
public class Ae2NetworkItemHandler implements IItemHandler {

    private final IStorageService service;
    private List<AEItemKey> slotIndex;

    public Ae2NetworkItemHandler(IStorageService service) {
        this.service = service;
        this.slotIndex = buildIndex(service.getCachedInventory());
    }

    private static List<AEItemKey> buildIndex(KeyCounter counter) {
        List<AEItemKey> result = new ArrayList<>();
        for (var entry : counter) {
            AEKey key = entry.getKey();
            if (key instanceof AEItemKey itemKey) {
                result.add(itemKey);
            }
        }
        return result;
    }

    @Override
    public int getSlots() {
        slotIndex = buildIndex(service.getCachedInventory());
        return slotIndex.size();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= slotIndex.size()) return ItemStack.EMPTY;
        AEItemKey key = slotIndex.get(slot);
        long available = service.getCachedInventory().get(key);
        if (available <= 0) return ItemStack.EMPTY;
        return key.toStack((int) Math.min(available, Integer.MAX_VALUE));
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot < 0 || slot >= slotIndex.size()) return ItemStack.EMPTY;
        AEItemKey key = slotIndex.get(slot);
        MEStorage storage = service.getInventory();
        Actionable mode = simulate ? Actionable.SIMULATE : Actionable.MODULATE;
        long extracted = storage.extract(key, amount, mode, IActionSource.empty());
        if (extracted <= 0) return ItemStack.EMPTY;
        return key.toStack((int) extracted);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        AEItemKey key = AEItemKey.of(stack);
        if (key == null) return stack.copy();
        MEStorage storage = service.getInventory();
        Actionable mode = simulate ? Actionable.SIMULATE : Actionable.MODULATE;
        long inserted = storage.insert(key, stack.getCount(), mode, IActionSource.empty());
        if (inserted >= stack.getCount()) return ItemStack.EMPTY;
        if (inserted == 0) return stack.copy();
        ItemStack remainder = stack.copy();
        remainder.setCount((int) (stack.getCount() - inserted));
        return remainder;
    }

    @Override
    public int getSlotLimit(int slot) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }
}
