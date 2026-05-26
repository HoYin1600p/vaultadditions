package io.github.a1qs.vaultadditions.mixins.patreon;

import iskallia.vault.config.GearModelRollRaritiesConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.configs.gear.CustomVaultGearModelRollRaritiesConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(value = GearModelRollRaritiesConfig.class, remap = false)
public class MixinGearModelRollRaritiesConfig {
    @Shadow
    Map<String, List<String>> ARMOR_MODEL_ROLLS;
    @Shadow Map<String, List<String>> SWORD_MODEL_ROLLS;
    @Shadow Map<String, List<String>> AXE_MODEL_ROLLS;
    @Shadow Map<String, List<String>> SHIELD_MODEL_ROLLS;
    @Shadow Map<String, List<String>> WAND_MODEL_ROLLS;
    @Shadow Map<String, List<String>> FOCUS_MODEL_ROLLS;
    @Shadow Map<String, List<String>> MAGNETS_MODEL_ROLLS;

    @Inject(method = "reset", at = @At("TAIL"))
    private void vaultAdditions$patchReset(CallbackInfo ci) {
        vaultAdditions$patchRolls();
    }

    @Inject(method = "getRolls(Lnet/minecraft/world/item/ItemStack;)Ljava/util/Map;", at = @At("HEAD"), cancellable = true)
    private void vaultAdditions$patchStackRolls(ItemStack stack, CallbackInfoReturnable<Map<String, List<String>>> cir) {
        vaultAdditions$patchRolls();
        vaultAdditions$patchWoldRolls();
        vaultAdditions$getWoldRolls(stack.getItem(), cir);
    }

    @Inject(method = "getRolls(Lnet/minecraft/world/item/Item;)Ljava/util/Map;", at = @At("HEAD"), cancellable = true)
    private void vaultAdditions$patchItemRolls(Item item, CallbackInfoReturnable<Map<String, List<String>>> cir) {
        vaultAdditions$patchRolls();
        vaultAdditions$patchWoldRolls();
        vaultAdditions$getWoldRolls(item, cir);
    }

    @Unique
    private void vaultAdditions$patchRolls() {
        //Armor Rolls
        addRoll(ARMOR_MODEL_ROLLS, "SPECIAL", "the_vault:gear/armor/hoy");
        addRoll(ARMOR_MODEL_ROLLS, "SPECIAL", "the_vault:gear/armor/hoy_with_grogu");
        addRoll(ARMOR_MODEL_ROLLS, "SPECIAL", "the_vault:gear/armor/dindjarin");
        addRoll(ARMOR_MODEL_ROLLS, "SPECIAL", "the_vault:gear/armor/hokage_robes");
        addRoll(ARMOR_MODEL_ROLLS, "SPECIAL", "the_vault:gear/armor/hokage_robes_maskless");
        addRoll(ARMOR_MODEL_ROLLS, "SPECIAL", "the_vault:gear/armor/celestial");
        addRoll(ARMOR_MODEL_ROLLS, "SPECIAL", "the_vault:gear/armor/viking");
        addRoll(ARMOR_MODEL_ROLLS, "SPECIAL", "the_vault:gear/armor/bokatan");
        addRoll(ARMOR_MODEL_ROLLS, "SPECIAL", "the_vault:gear/armor/kvothe");
        addRoll(ARMOR_MODEL_ROLLS, "SPECIAL", "the_vault:gear/armor/darkest");
        addRoll(ARMOR_MODEL_ROLLS, "SPECIAL", "the_vault:gear/armor/guardian");
        addRoll(ARMOR_MODEL_ROLLS, "SPECIAL", "the_vault:gear/armor/spacemarine");
        addRoll(ARMOR_MODEL_ROLLS, "SPECIAL", "the_vault:gear/armor/grogu");
        addRoll(ARMOR_MODEL_ROLLS, "SPECIAL", "the_vault:gear/armor/eldritch");

        //Sword Rolls
        addRoll(SWORD_MODEL_ROLLS, "SPECIAL", "the_vault:gear/sword/chain_sword");
        addRoll(SWORD_MODEL_ROLLS, "SPECIAL", "the_vault:gear/sword/executioner");

        //Axe Rolls

        //Shield Rolls
        addRoll(SHIELD_MODEL_ROLLS,"SPECIAL","the_vault:gear/shield/relicshield");
        addRoll(SHIELD_MODEL_ROLLS,"SPECIAL","the_vault:gear/shield/guardianshield");

        //Wand Rolls
        addRoll(WAND_MODEL_ROLLS,"SPECIAL","the_vault:gear/wand/sidearm");
        addRoll(WAND_MODEL_ROLLS,"SPECIAL","the_vault:gear/wand/eldritch_shield");

        //Focus Rolls
        addRoll(FOCUS_MODEL_ROLLS,"SPECIAL","the_vault:gear/focus/madness");

        //Magnet Rolls

    }

    @Unique
    private static void vaultAdditions$patchWoldRolls() {
        addRoll(CustomVaultGearModelRollRaritiesConfig.BATTLESTAFF_MODEL_ROLLS, "SPECIAL", "the_vault:gear/battlestaff/guardiantrident");
        addRoll(CustomVaultGearModelRollRaritiesConfig.TRIDENT_MODEL_ROLLS, "SPECIAL", "the_vault:gear/trident/guardiantrident");
    }

    @Unique
    private static void vaultAdditions$getWoldRolls(Item item, CallbackInfoReturnable<Map<String, List<String>>> cir) {
        if (item == xyz.iwolfking.woldsvaults.init.ModItems.BATTLESTAFF) {
            cir.setReturnValue(CustomVaultGearModelRollRaritiesConfig.BATTLESTAFF_MODEL_ROLLS);
        } else if (item == xyz.iwolfking.woldsvaults.init.ModItems.TRIDENT) {
            cir.setReturnValue(CustomVaultGearModelRollRaritiesConfig.TRIDENT_MODEL_ROLLS);
        }
    }

    @Unique
    private static void addRoll(Map<String, List<String>> map, String rarity, String id) {
        if (map == null) return;
        List<String> list = map.computeIfAbsent(rarity, k -> new ArrayList<>());
        if (!list.contains(id)) list.add(id);
    }

}
