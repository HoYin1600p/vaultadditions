package io.github.a1qs.vaultadditions.mixins.buildinggadgets;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.networking.storage.IStorageService;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.logging.LogUtils;
import io.github.a1qs.vaultadditions.compat.ae2.Ae2MaterialListCache;
import io.github.a1qs.vaultadditions.compat.ae2.Ae2NetworkItemHandler;
import io.github.a1qs.vaultadditions.compat.ae2.CachedAe2ItemHandler;
import io.github.a1qs.vaultadditions.config.ServerConfigs;
import io.github.a1qs.vaultadditions.init.ModNetwork;
import io.github.a1qs.vaultadditions.network.PacketAe2MaterialListRequest;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Map;

/**
 * When enableAe2BuildingGadgetsProvider = true and the gadget is linked to an
 * AE2 block (e.g. a Wireless Access Point), replace BG's default handler —
 * which only sees the WAP's tiny internal upgrade-card inventory — with one
 * that wraps the full ME network storage.
 *
 * Server side: returns Ae2NetworkItemHandler backed by live MEStorage.
 * Client side: returns CachedAe2ItemHandler backed by counts received from the
 *   server via PacketAe2MaterialListRequest/Response.  If no cache is present,
 *   fires a request packet and returns the original (shows 0 until the response
 *   arrives and ScrollingMaterialList's timer triggers a refresh).
 *
 * NBT keys used by InventoryLinker (BG 3.13.2):
 *   "bound_te_pos" — CompoundTag encoding the linked BlockPos
 *   "bound_te_dim" — String encoding the dimension ResourceLocation
 */
@Restriction(require = @Condition(type = Condition.Type.MOD, value = "buildinggadgets"))
@Pseudo
@Mixin(targets = "com.direwolf20.buildinggadgets.common.tainted.inventory.InventoryLinker", remap = false)
public abstract class MixinInventoryLinker {

    private static final Logger LOGGER = LogUtils.getLogger();

    @ModifyReturnValue(
            method = "getLinkedInventory(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraftforge/common/util/LazyOptional;",
            at = @At("RETURN"),
            remap = false
    )
    private static LazyOptional<IItemHandler> vaultadditions$useAe2NetworkStorage(
            LazyOptional<IItemHandler> original,
            @Local(argsOnly = true, ordinal = 0) Level level,
            @Local(argsOnly = true, ordinal = 0) ItemStack gadget) {

        if (!ServerConfigs.ENABLE_AE2_BUILDING_GADGETS_PROVIDER.get()) return original;

        CompoundTag tag = gadget.getTag();
        if (tag == null || !tag.contains("bound_te_pos") || !tag.contains("bound_te_dim")) return original;

        String linkedDim = tag.getString("bound_te_dim");
        if (!level.dimension().location().toString().equals(linkedDim)) return original;

        BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("bound_te_pos"));
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof IInWorldGridNodeHost host)) return original;

        // ── Client side: use cached counts from server ───────────────────────
        if (level.isClientSide()) {
            List<Map.Entry<ItemStack, Long>> cached = Ae2MaterialListCache.get(pos);
            if (cached != null) {
                return LazyOptional.of(() -> new CachedAe2ItemHandler(cached));
            }
            // No fresh cache — fire a request if not already pending
            if (Ae2MaterialListCache.shouldRequest(pos)) {
                Ae2MaterialListCache.markPending(pos);
                ModNetwork.sendToServer(new PacketAe2MaterialListRequest(pos, linkedDim));
                LOGGER.debug("[VaultAdditions/BG-AE2] Requested ME counts from server for WAP at {}", pos);
            }
            // Return empty so material list shows 0 until response arrives
            return LazyOptional.empty();
        }

        // ── Server side: use live MEStorage ──────────────────────────────────
        IGridNode node = null;
        for (Direction dir : Direction.values()) {
            node = host.getGridNode(dir);
            if (node != null) break;
        }
        if (node == null) {
            LOGGER.debug("[VaultAdditions/BG-AE2] Grid node at {} is null — network may be offline", pos);
            return original;
        }

        IStorageService storageService = node.getGrid().getStorageService();
        if (storageService == null) return original;

        Ae2NetworkItemHandler handler = new Ae2NetworkItemHandler(storageService);
        LOGGER.debug("[VaultAdditions/BG-AE2] Linked gadget at {} → ME network ({} item types)",
                pos, storageService.getCachedInventory().size());
        return LazyOptional.of(() -> handler);
    }
}
