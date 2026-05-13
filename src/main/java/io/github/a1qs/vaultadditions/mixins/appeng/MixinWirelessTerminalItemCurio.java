package io.github.a1qs.vaultadditions.mixins.appeng;

import appeng.items.tools.powered.WirelessTerminalItem;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

@Restriction(require = {
        @Condition(type = Condition.Type.MOD, value = "ae2"),
        @Condition(type = Condition.Type.MOD, value = "curios")
})
@Mixin(value = WirelessTerminalItem.class, remap = false)
public abstract class MixinWirelessTerminalItemCurio implements ICurioItem {
}
