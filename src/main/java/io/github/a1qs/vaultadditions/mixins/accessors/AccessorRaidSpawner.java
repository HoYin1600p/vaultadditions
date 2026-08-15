package io.github.a1qs.vaultadditions.mixins.accessors;

import iskallia.vault.core.vault.challenge.raid.RaidSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;
import java.util.UUID;

@Mixin(value = RaidSpawner.class, remap = false)
public interface AccessorRaidSpawner {
    @Accessor("spawnedWave")
    Set<UUID> vaultadditions$getSpawnedWave();
}
