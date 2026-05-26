package io.github.a1qs.vaultadditions.mixins.armor_effects;

import io.github.a1qs.vaultadditions.config.Configs;
import io.github.a1qs.vaultadditions.util.ZarithReputationBonusHelper;
import io.github.a1qs.vaultadditions.vault.gear.effect.ZarithReputationTransmogEffect;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.world.data.PlayerReputationData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

@Mixin(value = PlayerReputationData.class, remap = false)
public class MixinPlayerReputationData {
    @Inject(method = "getReputation", at = @At("RETURN"), cancellable = true)
    private static void vaultadditions$addZarithReputationBonus(UUID playerId, VaultGod god, CallbackInfoReturnable<Integer> cir) {
        if (!ZarithReputationBonusHelper.isZarith(god) || Configs.TRANSMOG_EFFECTS_CONFIG == null) {
            return;
        }

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }

        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        if (player == null) {
            return;
        }

        List<ZarithReputationTransmogEffect> effects = Configs.TRANSMOG_EFFECTS_CONFIG.getEffects(player, ZarithReputationTransmogEffect.class);
        int bonus = effects.stream().mapToInt(ZarithReputationTransmogEffect::getAmount).sum();
        if (bonus != 0) {
            cir.setReturnValue(cir.getReturnValueI() + bonus);
        }
    }
}
