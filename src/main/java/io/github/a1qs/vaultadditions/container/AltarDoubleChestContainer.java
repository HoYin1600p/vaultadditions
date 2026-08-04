package io.github.a1qs.vaultadditions.container;

import io.github.a1qs.vaultadditions.init.ModContainers;
import io.github.a1qs.vaultadditions.mixins.accessors.AccessorSlotPosition;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.Slot;

public class AltarDoubleChestContainer extends ChestMenu {
    public static final int CHEST_COLUMNS = 9;
    public static final int CHEST_ROWS = 10;
    public static final int VISIBLE_ROWS = 5;
    public static final int CHEST_SLOT_COUNT = CHEST_COLUMNS * CHEST_ROWS;

    public AltarDoubleChestContainer(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CHEST_SLOT_COUNT));
    }

    public AltarDoubleChestContainer(int containerId, Inventory playerInventory, Container chest) {
        super(ModContainers.ALTAR_DOUBLE_CHEST_CONTAINER, containerId, playerInventory, chest, CHEST_ROWS);
        for (int index = 0; index < CHEST_SLOT_COUNT; index++) {
            Slot slot = this.slots.get(index);
            ((AccessorSlotPosition) slot).vaultadditions$setY(index / CHEST_COLUMNS < VISIBLE_ROWS
                    ? 18 + index / CHEST_COLUMNS * 18
                    : -10_000);
        }

        for (int index = CHEST_SLOT_COUNT; index < CHEST_SLOT_COUNT + 27; index++) {
            Slot slot = this.slots.get(index);
            ((AccessorSlotPosition) slot).vaultadditions$setY(
                    121 + (index - CHEST_SLOT_COUNT) / CHEST_COLUMNS * 18
            );
        }

        for (int index = CHEST_SLOT_COUNT + 27; index < this.slots.size(); index++) {
            ((AccessorSlotPosition) this.slots.get(index)).vaultadditions$setY(179);
        }
    }
}
