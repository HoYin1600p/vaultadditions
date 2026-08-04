package io.github.a1qs.vaultadditions.mixins.accessors;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Slot.class)
public interface AccessorSlotPosition {
    @Mutable
    @Accessor("x")
    void vaultadditions$setX(int x);

    @Mutable
    @Accessor("y")
    void vaultadditions$setY(int y);
}
