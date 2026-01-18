package io.github.a1qs.vaultadditions.mixins.raid_plaques;

import io.github.a1qs.vaultadditions.events.VaultCommonEvents;
import iskallia.vault.core.vault.challenge.ChallengeManager;
import iskallia.vault.core.vault.challenge.raid.RaidChallengeManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(RaidChallengeManager.class)
public abstract class MixinRaidChallengeManager extends ChallengeManager {
    @Inject(method = "onTick", at = @At(value = "INVOKE", target = "Liskallia/vault/core/vault/challenge/raid/RaidSpawner;onCompleteWave()V"), remap = false)
    public void callWaveCompletedEvent(ServerLevel world, CallbackInfo ci) {
        PlayerList playerList = ServerLifecycleHooks.getCurrentServer().getPlayerList();
        for (UUID uuid : this.players) {
            ServerPlayer player = playerList.getPlayer(uuid);
            if (player != null) {
                VaultCommonEvents.RAID_WAVE_COMPLETED.invoke(world, player);
            }
        }
    }
}
