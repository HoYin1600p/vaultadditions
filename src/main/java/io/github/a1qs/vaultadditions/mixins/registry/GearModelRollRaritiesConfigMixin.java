package io.github.a1qs.vaultadditions.mixins.registry;

import io.github.a1qs.vaultadditions.vault.gear.model.armor.layers.*;
import iskallia.vault.config.GearModelRollRaritiesConfig;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(value = GearModelRollRaritiesConfig.class, remap = false)
public class GearModelRollRaritiesConfigMixin {

    @Shadow Map<String, List<String>> ARMOR_MODEL_ROLLS;
    @Shadow Map<String, List<String>> SWORD_MODEL_ROLLS;
    @Shadow Map<String, List<String>> AXE_MODEL_ROLLS;
    @Shadow Map<String, List<String>> SHIELD_MODEL_ROLLS;
    @Shadow Map<String, List<String>> WAND_MODEL_ROLLS;
    @Shadow Map<String, List<String>> FOCUS_MODEL_ROLLS;
    @Shadow Map<String, List<String>> MAGNETS_MODEL_ROLLS;

    @Unique
    private boolean additionaltransmogs$patched = false;

    @Inject(method = "getRolls", at = @At("HEAD"))
    private void vaultadditions$ensurePatched(ItemStack stack, CallbackInfoReturnable<Map<String, List<String>>> cir) {
        if (additionaltransmogs$patched) return;
        additionaltransmogs$patched = true;

        //Swords
        addRoll(SWORD_MODEL_ROLLS, "RARE", "the_vault:gear/sword/executioner");

        //Armor
        addRoll(ARMOR_MODEL_ROLLS,"SPECIAL", "the_vault:gear/armor/hoy");
        addRoll(ARMOR_MODEL_ROLLS,"SPECIAL", "the_vault:gear/armor/hoy_with_grogu");
        addRoll(ARMOR_MODEL_ROLLS,"SPECIAL", "the_vault:gear/armor/dindjarin");
        addRoll(ARMOR_MODEL_ROLLS,"SPECIAL", "the_vault:gear/armor/hokage_robes");
        addRoll(ARMOR_MODEL_ROLLS,"SPECIAL", "the_vault:gear/armor/hokage_robes_maskless");
        addRoll(ARMOR_MODEL_ROLLS,"SPECIAL", "the_vault:gear/armor/celestial");
        addRoll(ARMOR_MODEL_ROLLS,"SPECIAL", "the_vault:gear/armor/viking");
        addRoll(ARMOR_MODEL_ROLLS,"SPECIAL", "the_vault:gear/armor/bokatan");
        addRoll(ARMOR_MODEL_ROLLS,"SPECIAL", "the_vault:gear/armor/spacemarine");
        addRoll(ARMOR_MODEL_ROLLS,"SPECIAL", "the_vault:gear/armor/eldritch");
        addRoll(ARMOR_MODEL_ROLLS,"SPECIAL", "the_vault:gear/armor/grogu");

    }

    @Unique
    private static void addRoll(Map<String, List<String>> map, String rarity, String id) {
        if (map == null) return;
        List<String> list = map.computeIfAbsent(rarity, k -> new ArrayList<>());
        if (!list.contains(id)) list.add(id);
    }

}

