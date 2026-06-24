package io.github.a1qs.vaultadditions.mixins.buildinggadgets;

import com.direwolf20.buildinggadgets.common.tainted.building.BlockData;
import io.github.a1qs.vaultadditions.compat.buildinggadgets.BuildingGadgetsWaterlogGuard;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Restriction(require = @Condition(type = Condition.Type.MOD, value = "buildinggadgets"))
@Pseudo
@Mixin(targets = "com.direwolf20.buildinggadgets.common.tileentities.EffectBlockTileEntity", remap = false)
public abstract class MixinEffectBlockTileEntityWaterlogGuard {

    @ModifyVariable(
            method = "initializeData",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0,
            remap = false
    )
    private BlockData vaultadditions$sanitizeEffectBlockData(BlockData data) {
        return BuildingGadgetsWaterlogGuard.sanitizeData(data);
    }
}
