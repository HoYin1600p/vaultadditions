package io.github.a1qs.vaultadditions.mixins.appeng;

import appeng.items.tools.powered.WirelessTerminalItem;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Restriction(require = {
        @Condition(type = Condition.Type.MOD, value = "ae2"),
        @Condition(type = Condition.Type.MOD, value = "curios")
})
@Mixin(value = WirelessTerminalItem.class, remap = false)
public interface InvokerWirelessTerminalItem {

    @Invoker("checkPreconditions")
    boolean vaultadditions$checkPreconditions(ItemStack stack, Player player);
}
