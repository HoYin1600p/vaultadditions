package io.github.a1qs.vaultadditions.compat.ae2;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Client-side IItemHandler backed by a snapshot of ME network counts received
 * from the server via PacketAe2MaterialListResponse.
 * Each slot corresponds to one unique item type in the snapshot.
 */
public class CachedAe2ItemHandler implements IItemHandler {

    private final List<Map.Entry<ItemStack, Long>> entries;

    public CachedAe2ItemHandler(List<Map.Entry<ItemStack, Long>> entries) {
        this.entries = entries;
    }

    @Override
    public int getSlots() {
        return entries.size();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= entries.size()) return ItemStack.EMPTY;
        Map.Entry<ItemStack, Long> e = entries.get(slot);
        ItemStack template = e.getKey();
        long available = e.getValue();
        if (available <= 0) return ItemStack.EMPTY;
        ItemStack result = template.copy();
        result.setCount((int) Math.min(available, Integer.MAX_VALUE));
        return result;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot < 0 || slot >= entries.size()) return ItemStack.EMPTY;
        Map.Entry<ItemStack, Long> e = entries.get(slot);
        long available = e.getValue();
        int extracted = (int) Math.min(amount, available);
        if (extracted <= 0) return ItemStack.EMPTY;
        ItemStack result = e.getKey().copy();
        result.setCount(extracted);
        return result;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return stack.copy();
    }

    @Override
    public int getSlotLimit(int slot) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return false;
    }
}
