package io.github.a1qs.vaultadditions.events;

import io.github.a1qs.vaultadditions.util.ZarithReputationBonusHelper;
import iskallia.vault.item.gear.VaultCharmItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZarithCharmSyncEvent {
    @SubscribeEvent
    public static void syncZarithCharmReputation(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level.isClientSide || !(event.player instanceof ServerPlayer player)) {
            return;
        }

        if (player.tickCount % 5 != 0) {
            return;
        }

        boolean changed = syncInventory(player);
        changed |= syncCurios(player);
        if (changed) {
            player.getInventory().setChanged();
            player.inventoryMenu.broadcastChanges();
            player.containerMenu.broadcastChanges();
        }
    }

    private static boolean syncInventory(ServerPlayer player) {
        boolean changed = false;
        Inventory inventory = player.getInventory();

        for (ItemStack stack : inventory.items) {
            changed |= ZarithReputationBonusHelper.syncCharm(stack, player);
        }

        for (ItemStack stack : inventory.offhand) {
            changed |= ZarithReputationBonusHelper.syncCharm(stack, player);
        }

        return changed;
    }

    private static boolean syncCurios(ServerPlayer player) {
        boolean changed = false;
        List<SlotResult> charms = CuriosApi.getCuriosHelper().findCurios(player, stack -> stack.getItem() instanceof VaultCharmItem);
        for (SlotResult charm : charms) {
            changed |= ZarithReputationBonusHelper.syncCharm(charm.stack(), player);
        }
        return changed;
    }
}
