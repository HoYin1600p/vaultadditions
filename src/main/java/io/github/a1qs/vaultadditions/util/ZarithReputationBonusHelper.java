package io.github.a1qs.vaultadditions.util;

import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.item.gear.VaultCharmItem;
import iskallia.vault.world.data.PlayerReputationData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class ZarithReputationBonusHelper {
    private ZarithReputationBonusHelper() {
    }

    public static boolean isZarith(VaultGod god) {
        return god != null && ("Zarith".equalsIgnoreCase(god.getName()) || "zarith".equalsIgnoreCase(god.getSerializedName()));
    }

    public static int getEffectiveReputation(Player player, VaultGod god) {
        return PlayerReputationData.getReputation(player.getUUID(), god);
    }

    public static boolean syncCharm(ItemStack stack, Player player) {
        if (player == null || stack == null || stack.isEmpty() || !(stack.getItem() instanceof VaultCharmItem)) {
            return false;
        }

        VaultGod god = VaultCharmItem.getGod(stack).orElse(null);
        if (!isZarith(god)) {
            return false;
        }

        int reputation = getEffectiveReputation(player, god);
        boolean changed = false;
        if (VaultCharmItem.getGodReputation(stack) != reputation) {
            VaultCharmItem.setGodReputation(stack, reputation);
            changed = true;
        }

        if (syncChaosCharmData(stack, reputation)) {
            changed = true;
        }

        return changed;
    }

    private static boolean syncChaosCharmData(ItemStack stack, int reputation) {
        if (!stack.hasTag() || !stack.getOrCreateTag().contains("vaultchaosgod:chaos_charm_data")) {
            return false;
        }

        int currentReputation = stack.getOrCreateTagElement("vaultchaosgod:chaos_charm_data").getInt("godReputation");
        if (currentReputation == reputation) {
            return false;
        }

        stack.getOrCreateTagElement("vaultchaosgod:chaos_charm_data").putInt("godReputation", reputation);
        return true;
    }
}
