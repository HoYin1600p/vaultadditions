package io.github.a1qs.vaultadditions.network;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.networking.storage.IStorageService;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import io.github.a1qs.vaultadditions.init.ModNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Client → Server: "give me the current ME network item counts for the WAP at this position."
 * Server responds with PacketAe2MaterialListResponse.
 */
public class PacketAe2MaterialListRequest {

    private final BlockPos pos;
    private final String dim;

    public PacketAe2MaterialListRequest(BlockPos pos, String dim) {
        this.pos = pos;
        this.dim = dim;
    }

    public static void encode(PacketAe2MaterialListRequest msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeUtf(msg.dim);
    }

    public static PacketAe2MaterialListRequest decode(FriendlyByteBuf buf) {
        return new PacketAe2MaterialListRequest(buf.readBlockPos(), buf.readUtf());
    }

    public static void handle(PacketAe2MaterialListRequest msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel level = player.getLevel();
            if (!level.dimension().location().toString().equals(msg.dim)) return;

            BlockEntity be = level.getBlockEntity(msg.pos);
            if (!(be instanceof IInWorldGridNodeHost host)) return;

            IGridNode node = null;
            for (Direction dir : Direction.values()) {
                node = host.getGridNode(dir);
                if (node != null) break;
            }
            if (node == null) return;

            IStorageService service = node.getGrid().getStorageService();
            if (service == null) return;

            KeyCounter counter = service.getCachedInventory();
            List<Map.Entry<ItemStack, Long>> counts = new ArrayList<>();
            for (var entry : counter) {
                AEKey key = entry.getKey();
                if (key instanceof AEItemKey itemKey) {
                    long amount = entry.getLongValue();
                    if (amount > 0) {
                        counts.add(Map.entry(itemKey.toStack(1), amount));
                    }
                }
            }

            ModNetwork.sendToClient(new PacketAe2MaterialListResponse(msg.pos, counts), player);
        });
        ctx.get().setPacketHandled(true);
    }
}
