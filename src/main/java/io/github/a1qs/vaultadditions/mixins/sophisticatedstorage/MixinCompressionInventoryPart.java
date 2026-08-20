package io.github.a1qs.vaultadditions.mixins.sophisticatedstorage;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryPartitioner;
import net.p3pp3rf1y.sophisticatedstorage.upgrades.compression.CompressionInventoryPart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Restriction(require = @Condition(type = Condition.Type.MOD, value = "sophisticatedstorage"))
@Pseudo
@Mixin(targets = "net.p3pp3rf1y.sophisticatedstorage.upgrades.compression.CompressionInventoryPart", remap = false)
public abstract class MixinCompressionInventoryPart {
    @Unique
    private static final Logger VAULTADDITIONS_LOGGER = LogManager.getLogger("VaultAdditions/CompressionInventoryPart");

    @Shadow
    @Final
    private InventoryHandler parent;

    @Shadow
    @Final
    private InventoryPartitioner.SlotRange slotRange;

    @Shadow
    private Map<Integer, ?> slotDefinitions;

    @Shadow
    @Final
    private Map<Integer, ItemStack> calculatedStacks;

    @Unique
    private long vaultadditions$lastInvalidStateWarning;

    @Inject(
            method = "updateSlotLimits(Ljava/util/Map;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 1,
            remap = false
    )
    private void vaultadditions$useMixedMultiplierSlotLimits(Map<Integer, ?> definitions, CallbackInfo ci) {
        int totalLimit = 0;
        int endSlot = slotRange.firstSlot() + slotRange.numberOfSlots();
        for (int slot = slotRange.firstSlot(); slot < endSlot; slot++) {
            Object rawDefinition = definitions.get(slot);
            if (rawDefinition instanceof AccessorCompressionSlotDefinition definition
                    && definition.vaultadditions$isAccessible()) {
                int baseLimit = parent.getBaseStackLimit(new ItemStack(definition.vaultadditions$getItem()));
                totalLimit = vaultadditions$cappedAdd(
                        baseLimit,
                        vaultadditions$cappedMultiply(definition.vaultadditions$getPrevSlotMultiplier(), totalLimit)
                );
                definition.vaultadditions$setSlotLimit(totalLimit);
            }
        }
        ci.cancel();
    }

    @WrapOperation(
            method = "insertIntoInternalAndCalculated(IJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/p3pp3rf1y/sophisticatedstorage/upgrades/compression/CompressionInventoryPart;getPrevSlotMultiplier(I)I"
            ),
            require = 1,
            remap = false
    )
    private int vaultadditions$validateMultiplier(
            CompressionInventoryPart instance,
            int slot,
            Operation<Integer> original
    ) {
        Object rawDefinition = slotDefinitions.get(slot);
        if (!(rawDefinition instanceof AccessorCompressionSlotDefinition definition)
                || !definition.vaultadditions$isAccessible()
                || definition.vaultadditions$getPrevSlotMultiplier() <= 0) {
            throw new InvalidCompressionStateException(slot);
        }
        return original.call(instance, slot);
    }

    @WrapMethod(
            method = "insertItem(ILnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/item/ItemStack;",
            require = 1,
            remap = false
    )
    private ItemStack vaultadditions$rejectInvalidInsertion(
            int slot,
            ItemStack stack,
            boolean simulate,
            Operation<ItemStack> original
    ) {
        try {
            ItemStack result = original.call(slot, stack, simulate);
            if (!simulate && result != stack) {
                vaultadditions$refreshCalculatedSlots();
            }
            return result;
        } catch (InvalidCompressionStateException exception) {
            vaultadditions$warnInvalidState(slot, exception.slot);
            return stack;
        }
    }

    @Inject(
            method = "extractItem(IIZLjava/util/function/ToIntFunction;)Lnet/minecraft/world/item/ItemStack;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/p3pp3rf1y/sophisticatedstorage/upgrades/compression/CompressionInventoryPart;extractFromInternal(II)V",
                    shift = At.Shift.AFTER
            ),
            require = 1,
            remap = false
    )
    private void vaultadditions$refreshCalculatedSlotTracking(
            int slot,
            int amount,
            boolean simulate,
            java.util.function.ToIntFunction<ItemStack> getLimit,
            CallbackInfoReturnable<ItemStack> cir
    ) {
        vaultadditions$refreshCalculatedSlots();
    }

    @Unique
    private void vaultadditions$refreshCalculatedSlots() {
        calculatedStacks.forEach((calculatedSlot, stack) -> {
            parent.getSlotTracker().removeAndSetSlotIndexes(parent, calculatedSlot, stack);
            parent.triggerOnChangeListeners(calculatedSlot);
        });
    }

    @Unique
    private int vaultadditions$cappedMultiply(int left, int right) {
        return (int) Math.min(Integer.MAX_VALUE, (long) left * right);
    }

    @Unique
    private int vaultadditions$cappedAdd(int left, int right) {
        return (int) Math.min(Integer.MAX_VALUE, (long) left + right);
    }

    @Unique
    private void vaultadditions$warnInvalidState(int requestedSlot, int missingSlot) {
        long now = System.currentTimeMillis();
        if (now - vaultadditions$lastInvalidStateWarning < 60_000L) {
            return;
        }
        vaultadditions$lastInvalidStateWarning = now;
        VAULTADDITIONS_LOGGER.error(
                "Rejected a Compression insertion without consuming items: requested slot {}, invalid definition slot {}, range {}+{}, definitions {}",
                requestedSlot,
                missingSlot,
                slotRange.firstSlot(),
                slotRange.numberOfSlots(),
                slotDefinitions.keySet()
        );
    }

    @Unique
    private static final class InvalidCompressionStateException extends RuntimeException {
        private final int slot;

        private InvalidCompressionStateException(int slot) {
            this.slot = slot;
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }
}
