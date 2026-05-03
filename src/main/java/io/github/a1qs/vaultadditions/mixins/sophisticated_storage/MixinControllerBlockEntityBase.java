package io.github.a1qs.vaultadditions.mixins.sophisticated_storage;

import io.github.a1qs.vaultadditions.config.SophisticatedControllerRangeConfig;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Restriction(require = @Condition(type = Condition.Type.MOD, value = "sophisticatedcore"))
@Pseudo
@Mixin(targets = "net.p3pp3rf1y.sophisticatedcore.controller.ControllerBlockEntityBase", remap = false)
public abstract class MixinControllerBlockEntityBase {
    @ModifyConstant(method = "isWithinRange", constant = @Constant(intValue = 15), require = 3, remap = false)
    private int vaultadditions$useConfiguredControllerRange(int original) {
        return SophisticatedControllerRangeConfig.getControllerRange();
    }
}
