package io.github.a1qs.vaultadditions.events;

import io.github.a1qs.vaultadditions.VaultAdditions;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.ItemShardPouch;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = VaultAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SoulShardPouchStorageTransferEvent {
    private static final Capability<IItemHandler> ITEM_HANDLER = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

    private SoulShardPouchStorageTransferEvent() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack pouch = event.getItemStack();
        if (!(pouch.getItem() instanceof ItemShardPouch)
                || !event.getPlayer().isShiftKeyDown()
                || event.getPlayer().isSpectator()
                || event.getUseBlock() == Event.Result.DENY
                || event.getUseItem() == Event.Result.DENY
                || !isSupportedStorage(ForgeRegistries.BLOCKS.getKey(
                        event.getWorld().getBlockState(event.getPos()).getBlock()))) {
            return;
        }

        event.setCancellationResult(InteractionResult.sidedSuccess(event.getWorld().isClientSide()));
        event.setCanceled(true);

        if (event.getWorld().isClientSide() || !(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        if (!player.mayUseItemAt(event.getPos(), event.getFace(), pouch)) {
            return;
        }

        BlockEntity blockEntity = event.getWorld().getBlockEntity(event.getPos());
        if (blockEntity == null) {
            showStatus(player, "This storage cannot provide Soul Shards.", ChatFormatting.RED);
            return;
        }

        IItemHandler source = blockEntity.getCapability(ITEM_HANDLER, null).orElse(null);
        if (source == null) {
            showStatus(player, "This storage cannot provide Soul Shards.", ChatFormatting.RED);
            return;
        }

        TransferResult result;
        try {
            result = transfer(player, pouch, source);
        } catch (RuntimeException exception) {
            VaultAdditions.LOGGER.error("Failed to transfer Soul Shards from storage at {} for {}",
                    event.getPos(), player.getGameProfile().getName(), exception);
            showStatus(player, "Soul Shard transfer failed safely.", ChatFormatting.RED);
            return;
        }

        if (result.transferred() > 0) {
            player.getInventory().setChanged();
            player.inventoryMenu.broadcastChanges();
            player.containerMenu.broadcastChanges();

            String suffix = result.pouchFull() ? " Pouch is full." : "";
            showStatus(player, "Transferred " + result.transferred() + " Soul Shards." + suffix, ChatFormatting.GREEN);
        } else if (result.failed()) {
            showStatus(player, "Soul Shard transfer was stopped without changing the pouch.", ChatFormatting.RED);
        } else if (result.pouchFull()) {
            showStatus(player, "Soul Shard Pouch is full.", ChatFormatting.GOLD);
        } else if (!result.shardsFound()) {
            showStatus(player, "No Soul Shards found in this storage.", ChatFormatting.YELLOW);
        }
    }

    private static TransferResult transfer(ServerPlayer player, ItemStack pouch, IItemHandler source) {
        IItemHandler pouchInventory = pouch.getCapability(ITEM_HANDLER).orElse(null);
        if (pouchInventory == null || pouchInventory.getSlots() < 1) {
            return TransferResult.failedResult();
        }

        ItemStack contained = pouchInventory.getStackInSlot(0);
        int currentCount = isSoulShard(contained) ? contained.getCount() : 0;
        int capacity = pouchInventory.getSlotLimit(0);
        if (capacity <= 0 || currentCount >= capacity) {
            return new TransferResult(0, true, false, false);
        }

        long remainingCapacity = (long) capacity - currentCount;
        int transferred = 0;
        boolean shardsFound = false;

        for (int slot = 0; slot < source.getSlots() && remainingCapacity > 0; slot++) {
            while (remainingCapacity > 0) {
                ItemStack available = source.getStackInSlot(slot);
                if (!isSoulShard(available) || available.getCount() <= 0) {
                    break;
                }

                shardsFound = true;
                int requested = (int) Math.min(remainingCapacity, (long) available.getCount());
                ItemStack simulated = source.extractItem(slot, requested, true);
                if (simulated.isEmpty()) {
                    break;
                }
                if (!isSoulShard(simulated) || simulated.getCount() > requested) {
                    VaultAdditions.LOGGER.error(
                            "Storage handler returned an invalid simulated Soul Shard extraction: slot={}, requested={}, returned={}",
                            slot, requested, simulated);
                    return new TransferResult(transferred, false, true, true);
                }

                int approved = Math.min(simulated.getCount(), requested);
                if (approved <= 0) {
                    break;
                }

                ItemStack extracted = source.extractItem(slot, approved, false);
                if (extracted.isEmpty()) {
                    break;
                }
                if (!isSoulShard(extracted) || extracted.getCount() > approved) {
                    VaultAdditions.LOGGER.error(
                            "Storage handler violated its Soul Shard extraction simulation: slot={}, approved={}, returned={}",
                            slot, approved, extracted);
                    restoreExtraction(player, source, slot, extracted);
                    return new TransferResult(transferred, false, true, true);
                }

                int extractedCount = extracted.getCount();
                long updatedCount = (long) currentCount + extractedCount;
                if (updatedCount > capacity) {
                    VaultAdditions.LOGGER.error(
                            "Soul Shard extraction exceeded pouch capacity: current={}, extracted={}, capacity={}",
                            currentCount, extractedCount, capacity);
                    restoreExtraction(player, source, slot, extracted);
                    return new TransferResult(transferred, false, true, true);
                }

                ItemStack updatedContents = new ItemStack(ModItems.SOUL_SHARD, (int) updatedCount);
                ItemShardPouch.setContainedStack(pouch, updatedContents);
                currentCount = (int) updatedCount;
                remainingCapacity = (long) capacity - currentCount;
                transferred += extractedCount;
            }
        }

        return new TransferResult(transferred, currentCount >= capacity, shardsFound, false);
    }

    private static void restoreExtraction(ServerPlayer player, IItemHandler source, int slot, ItemStack extracted) {
        if (extracted.isEmpty()) {
            return;
        }

        ItemStack remainder = source.insertItem(slot, extracted, false);
        if (remainder.isEmpty()) {
            return;
        }

        VaultAdditions.LOGGER.error("Storage handler could not restore {} after a failed Soul Shard transfer", remainder);
        player.getInventory().add(remainder);
        if (!remainder.isEmpty()) {
            player.drop(remainder, false);
        }
    }

    private static boolean isSoulShard(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == ModItems.SOUL_SHARD;
    }

    private static boolean isSupportedStorage(ResourceLocation blockId) {
        if (blockId == null) {
            return false;
        }

        String namespace = blockId.getNamespace();
        String path = blockId.getPath();
        if ("sophisticatedbackpacks".equals(namespace)) {
            return "backpack".equals(path) || path.endsWith("_backpack");
        }
        if ("sophisticatedstorage".equals(namespace)) {
            return path.contains("barrel");
        }
        if ("storagedrawers".equals(namespace)) {
            return path.contains("drawers");
        }
        if ("framedcompactdrawers".equals(namespace)) {
            return path.startsWith("framed_full_")
                    || path.startsWith("framed_half_")
                    || "framed_compact_drawer".equals(path);
        }
        return ForgeRegistries.BLOCKS.getKey(Blocks.BARREL).equals(blockId);
    }

    private static void showStatus(ServerPlayer player, String message, ChatFormatting color) {
        player.displayClientMessage(new TextComponent(message).withStyle(color), true);
    }

    private record TransferResult(int transferred, boolean pouchFull, boolean shardsFound, boolean failed) {
        private static TransferResult failedResult() {
            return new TransferResult(0, false, false, true);
        }
    }
}
