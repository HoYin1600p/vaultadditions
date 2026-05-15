package io.github.a1qs.vaultadditions.network;

import io.github.a1qs.vaultadditions.compat.ae2.Ae2MaterialListCache;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Server → Client: ME network item counts for a linked WAP position.
 * Populates Ae2MaterialListCache so the material list can show real counts.
 */
public class PacketAe2MaterialListResponse {

    private final BlockPos pos;
    private final List<Map.Entry<ItemStack, Long>> counts;

    public PacketAe2MaterialListResponse(BlockPos pos, List<Map.Entry<ItemStack, Long>> counts) {
        this.pos = pos;
        this.counts = counts;
    }

    public static void encode(PacketAe2MaterialListResponse msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeInt(msg.counts.size());
        for (Map.Entry<ItemStack, Long> entry : msg.counts) {
            buf.writeItem(entry.getKey());
            buf.writeLong(entry.getValue());
        }
    }

    public static PacketAe2MaterialListResponse decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int size = buf.readInt();
        List<Map.Entry<ItemStack, Long>> counts = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ItemStack stack = buf.readItem();
            long amount = buf.readLong();
            counts.add(Map.entry(stack, amount));
        }
        return new PacketAe2MaterialListResponse(pos, counts);
    }

    public static void handle(PacketAe2MaterialListResponse msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (FMLEnvironment.dist.isClient()) {
                Ae2MaterialListCache.store(msg.pos, msg.counts);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
