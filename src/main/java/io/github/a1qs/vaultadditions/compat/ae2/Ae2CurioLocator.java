package io.github.a1qs.vaultadditions.compat.ae2;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.core.AELog;
import appeng.helpers.WirelessCraftingTerminalMenuHost;
import appeng.helpers.WirelessTerminalMenuHost;
import appeng.items.tools.powered.WirelessCraftingTerminalItem;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;

/*
 * Portions of the Curios locator flow are derived from AE2WTLib by Mari_023
 * under the MIT License:
 * https://github.com/Mari023/AE2WirelessTerminalLibrary
 */
public record Ae2CurioLocator(String identifier, int index) implements MenuLocator {

    public Ae2CurioLocator(SlotContext slotContext) {
        this(slotContext.identifier(), slotContext.index());
    }

    @Override
    public <T> @Nullable T locate(Player player, Class<T> hostInterface) {
        ItemStack stack = locateItem(player);

        if (stack.isEmpty()) {
            AELog.warn("Item in Curio slot with ID {} and index {} of {} is missing.", identifier, index, player);
            return null;
        }

        ItemMenuHost menuHost = createHost(player, stack);

        if (hostInterface.isInstance(menuHost)) {
            return hostInterface.cast(menuHost);
        }

        if (menuHost != null) {
            AELog.warn("Item in Curio slot with ID {} and index {} of {} did not create a compatible menu of type {}: {}",
                    identifier, index, player, hostInterface, menuHost);
        } else {
            AELog.warn("Item in Curio slot with ID {} and index {} of {} is not a supported AE2 wireless terminal: {}",
                    identifier, index, player, stack);
        }

        return null;
    }

    public ItemStack locateItem(Player player) {
        List<SlotResult> slotResults = CuriosApi.getCuriosHelper().findCurios(player, identifier);

        for (SlotResult result : slotResults) {
            if (result.slotContext().index() == index) {
                return result.stack();
            }
        }

        return ItemStack.EMPTY;
    }

    private ItemMenuHost createHost(Player player, ItemStack stack) {
        if (stack.getItem() instanceof WirelessCraftingTerminalItem) {
            return new CurioWirelessCraftingTerminalHost(player, stack, this);
        }

        if (stack.getItem() instanceof WirelessTerminalItem) {
            return new CurioWirelessTerminalHost(player, stack, this);
        }

        return null;
    }

    public void writeToPacket(FriendlyByteBuf buffer) {
        buffer.writeUtf(identifier);
        buffer.writeInt(index);
    }

    public static Ae2CurioLocator readFromPacket(FriendlyByteBuf buffer) {
        return new Ae2CurioLocator(buffer.readUtf(), buffer.readInt());
    }

    private static class CurioWirelessTerminalHost extends WirelessTerminalMenuHost {
        private final Ae2CurioLocator locator;

        private CurioWirelessTerminalHost(Player player, ItemStack stack, Ae2CurioLocator locator) {
            super(player, null, stack, (menuPlayer, subMenu) -> {
                if (stack.getItem() instanceof WirelessTerminalItem terminal) {
                    MenuOpener.returnTo(terminal.getMenuType(), menuPlayer, locator);
                }
            });
            this.locator = locator;
        }

        @Override
        public boolean onBroadcastChanges(net.minecraft.world.inventory.AbstractContainerMenu menu) {
            return Ae2CuriosCompat.isStillPresent(getPlayer(), getItemStack(), this.locator)
                    && super.onBroadcastChanges(menu);
        }
    }

    private static class CurioWirelessCraftingTerminalHost extends WirelessCraftingTerminalMenuHost {
        private final Ae2CurioLocator locator;

        private CurioWirelessCraftingTerminalHost(Player player, ItemStack stack, Ae2CurioLocator locator) {
            super(player, null, stack, (menuPlayer, subMenu) -> {
                if (stack.getItem() instanceof WirelessTerminalItem terminal) {
                    MenuOpener.returnTo(terminal.getMenuType(), menuPlayer, locator);
                }
            });
            this.locator = locator;
        }

        @Override
        public boolean onBroadcastChanges(net.minecraft.world.inventory.AbstractContainerMenu menu) {
            return Ae2CuriosCompat.isStillPresent(getPlayer(), getItemStack(), this.locator)
                    && super.onBroadcastChanges(menu);
        }
    }
}
