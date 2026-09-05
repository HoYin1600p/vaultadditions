package io.github.a1qs.vaultadditions.mixins.rune_boss;

import iskallia.vault.block.entity.BossRunePillarTileEntity;
import iskallia.vault.core.world.storage.BlockCuboid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BossRunePillarTileEntity.Config.class)
public interface AccessorBossRunePillarConfig {
    @Accessor(value = "zone", remap = false)
    BlockCuboid vaultadditions$getZone();
}
