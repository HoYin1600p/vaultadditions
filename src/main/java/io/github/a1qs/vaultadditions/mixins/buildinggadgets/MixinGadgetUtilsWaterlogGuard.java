package io.github.a1qs.vaultadditions.mixins.buildinggadgets;

import com.direwolf20.buildinggadgets.common.tainted.building.BlockData;
import io.github.a1qs.vaultadditions.compat.buildinggadgets.BuildingGadgetsWaterlogGuard;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(require = @Condition(type = Condition.Type.MOD, value = "buildinggadgets"))
@Pseudo
@Mixin(targets = "com.direwolf20.buildinggadgets.common.util.GadgetUtils", remap = false)
public abstract class MixinGadgetUtilsWaterlogGuard {

    @Inject(method = "getToolBlock", at = @At("RETURN"), cancellable = true, remap = false)
    private static void vaultadditions$sanitizeSelectedBlock(ItemStack gadget, CallbackInfoReturnable<BlockData> cir) {
        BlockData original = cir.getReturnValue();
        BlockData sanitized = BuildingGadgetsWaterlogGuard.sanitizeData(original);
        if (sanitized == null || sanitized == original) return;

        CompoundTag tag = gadget.getOrCreateTag();
        tag.put("state", sanitized.serialize(true));
        gadget.setTag(tag);
        cir.setReturnValue(sanitized);
    }
}
