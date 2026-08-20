package io.github.a1qs.vaultadditions.mixins.sophisticatedstorage;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Restriction(require = @Condition(type = Condition.Type.MOD, value = "sophisticatedstorage"))
@Pseudo
@Mixin(targets = "net.p3pp3rf1y.sophisticatedstorage.upgrades.compression.CompressionInventoryPart$SlotDefinition", remap = false)
public interface AccessorCompressionSlotDefinition {
    @Accessor("item")
    Item vaultadditions$getItem();

    @Accessor("prevSlotMultiplier")
    int vaultadditions$getPrevSlotMultiplier();

    @Accessor("isAccessible")
    boolean vaultadditions$isAccessible();

    @Invoker("setSlotLimit")
    void vaultadditions$setSlotLimit(int slotLimit);
}
