package io.github.a1qs.vaultadditions.compat.ae2;

import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import io.github.a1qs.vaultadditions.mixins.appeng.InvokerWirelessTerminalItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;
import java.util.function.Predicate;

public final class Ae2CuriosCompat {
    private static boolean registered;

    private Ae2CuriosCompat() {
    }

    public static void register() {
        if (registered || !ModList.get().isLoaded("ae2") || !ModList.get().isLoaded("curios")) {
            return;
        }

        MenuLocators.register(Ae2CurioLocator.class, Ae2CurioLocator::writeToPacket, Ae2CurioLocator::readFromPacket);
        registered = true;
    }

    public static boolean openTerminalFromCurio(Player player, Predicate<ItemStack> predicate) {
        List<SlotResult> results = CuriosApi.getCuriosHelper().findCurios(player,
                stack -> stack.getItem() instanceof WirelessTerminalItem && predicate.test(stack));

        for (SlotResult result : results) {
            ItemStack stack = result.stack();
            WirelessTerminalItem terminal = (WirelessTerminalItem) stack.getItem();

            if (!((InvokerWirelessTerminalItem) terminal).vaultadditions$checkPreconditions(stack, player)) {
                continue;
            }

            if (MenuOpener.open(terminal.getMenuType(), player, new Ae2CurioLocator(result.slotContext()))) {
                return true;
            }
        }

        return false;
    }

    public static boolean isStillPresent(Player player, ItemStack expectedStack, Ae2CurioLocator locator) {
        ItemStack currentStack = locator.locateItem(player);
        return !currentStack.isEmpty()
                && currentStack.getCount() == expectedStack.getCount()
                && ItemStack.isSameItemSameTags(currentStack, expectedStack);
    }
}
