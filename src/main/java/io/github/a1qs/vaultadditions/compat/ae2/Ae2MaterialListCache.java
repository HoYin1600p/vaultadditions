package io.github.a1qs.vaultadditions.compat.ae2;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client-side cache of ME network item counts for AE2-linked Building Gadgets.
 * Populated by PacketAe2MaterialListResponse, consumed by MixinInventoryLinker
 * to return a real IItemHandler for the material list display.
 */
public class Ae2MaterialListCache {

    private static final long CACHE_TTL_MS = 10_000;
    private static final long REQUEST_COOLDOWN_MS = 2_000;

    private record Entry(List<Map.Entry<ItemStack, Long>> counts, long timestamp) {}

    private static final Map<BlockPos, Entry> CACHE = new HashMap<>();
    private static final Map<BlockPos, Long> PENDING = new HashMap<>();

    public static void store(BlockPos pos, List<Map.Entry<ItemStack, Long>> counts) {
        CACHE.put(pos, new Entry(counts, System.currentTimeMillis()));
        PENDING.remove(pos);
    }

    /** Returns cached counts if present and not stale, otherwise null. */
    public static List<Map.Entry<ItemStack, Long>> get(BlockPos pos) {
        Entry entry = CACHE.get(pos);
        if (entry == null) return null;
        if (System.currentTimeMillis() - entry.timestamp() > CACHE_TTL_MS) {
            CACHE.remove(pos);
            return null;
        }
        return Collections.unmodifiableList(entry.counts());
    }

    /** True if a request should be sent (no pending request within cooldown). */
    public static boolean shouldRequest(BlockPos pos) {
        Long lastRequest = PENDING.get(pos);
        if (lastRequest == null) return true;
        return System.currentTimeMillis() - lastRequest > REQUEST_COOLDOWN_MS;
    }

    public static void markPending(BlockPos pos) {
        PENDING.put(pos, System.currentTimeMillis());
    }

    public static void clear() {
        CACHE.clear();
        PENDING.clear();
    }
}
