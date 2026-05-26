package io.github.a1qs.vaultadditions.mixins.patreon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.configs.gear.CustomVaultGearModelRollRaritiesConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(value = CustomVaultGearModelRollRaritiesConfig.class, remap = false)
public class MixinCustomVaultGearModelRollRaritiesConfig {
    @Shadow
    public static Map<String, List<String>> BATTLESTAFF_MODEL_ROLLS;
    @Shadow
    public static Map<String, List<String>> TRIDENT_MODEL_ROLLS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void vaultAdditions$patchWoldModelRolls(CallbackInfo ci) {
        vaultAdditions$addRoll(BATTLESTAFF_MODEL_ROLLS, "SPECIAL", "the_vault:gear/battlestaff/guardiantrident");
        vaultAdditions$addRoll(TRIDENT_MODEL_ROLLS, "SPECIAL", "the_vault:gear/trident/guardiantrident");
    }

    @Unique
    private static void vaultAdditions$addRoll(Map<String, List<String>> map, String rarity, String id) {
        if (map == null) {
            return;
        }
        List<String> rolls = map.computeIfAbsent(rarity, key -> new ArrayList<>());
        if (!rolls.contains(id)) {
            rolls.add(id);
        }
    }
}
