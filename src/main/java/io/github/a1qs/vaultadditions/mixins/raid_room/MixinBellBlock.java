package io.github.a1qs.vaultadditions.mixins.raid_room;

import io.github.a1qs.vaultadditions.mixins.accessors.AccessorRaidSpawner;
import iskallia.vault.core.vault.challenge.raid.RaidChallengeManager;
import iskallia.vault.core.world.storage.BlockCuboid;
import iskallia.vault.world.data.ChallengeData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mixin(BellBlock.class)
public abstract class MixinBellBlock {
    private static final int[][] RETURN_OFFSETS = {
            {2, 2}, {-2, 2}, {2, -2}, {-2, -2},
            {3, 0}, {0, 3}, {-3, 0}, {0, -3}
    };

    @Inject(
            method = "attemptToRing(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z",
            at = @At("RETURN")
    )
    private void vaultadditions$returnEscapedRaidMobs(
            @Nullable Entity source,
            Level level,
            BlockPos bellPos,
            @Nullable Direction direction,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!Boolean.TRUE.equals(cir.getReturnValue()) || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        ChallengeData.get(serverLevel.getServer())
                .getManagers(RaidChallengeManager.class, serverLevel.dimension())
                .stream()
                .filter(manager -> manager.getPhase() == RaidChallengeManager.Phase.IN_WAVE)
                .filter(manager -> vaultadditions$containsBell(manager, bellPos))
                .findFirst()
                .ifPresent(manager -> vaultadditions$returnEscapedMobs(serverLevel, manager));
    }

    private static boolean vaultadditions$containsBell(RaidChallengeManager manager, BlockPos bellPos) {
        BlockCuboid zone = manager.getZone();
        return zone != null && zone.offset(manager.pos).contains(bellPos);
    }

    private static void vaultadditions$returnEscapedMobs(ServerLevel level, RaidChallengeManager manager) {
        BlockCuboid roomZone = manager.getZone().offset(manager.pos).grow(-1, 0, -1);
        BlockCuboid innerZone = roomZone.grow(-2, -2, -2);
        BlockCuboid outerZone = roomZone.grow(4, 4, 4);
        Set<UUID> trackedMobs = new HashSet<>(
                ((AccessorRaidSpawner) (Object) manager.getSpawner()).vaultadditions$getSpawnedWave()
        );

        List<LivingEntity> escapedMobs = level.getEntitiesOfClass(
                LivingEntity.class,
                outerZone.toAABB(),
                entity -> entity.isAlive()
                        && !entity.isRemoved()
                        && !innerZone.contains(entity.blockPosition())
                        && (entity instanceof Vex || trackedMobs.contains(entity.getUUID()))
        );

        for (int i = 0; i < escapedMobs.size(); i++) {
            LivingEntity entity = escapedMobs.get(i);
            Vec3 destination = vaultadditions$findDestination(level, manager.pos, entity, i);
            entity.teleportTo(destination.x, destination.y, destination.z);
            entity.setDeltaMovement(Vec3.ZERO);
            entity.fallDistance = 0.0F;

            if (entity instanceof Mob mob) {
                mob.getNavigation().stop();
                mob.setTarget(null);
            }
        }
    }

    private static Vec3 vaultadditions$findDestination(
            ServerLevel level,
            BlockPos controllerPos,
            LivingEntity entity,
            int startIndex
    ) {
        double y = controllerPos.getY() + 1.0;

        for (int i = 0; i < RETURN_OFFSETS.length; i++) {
            int[] offset = RETURN_OFFSETS[(startIndex + i) % RETURN_OFFSETS.length];
            double x = controllerPos.getX() + offset[0] + 0.5;
            double z = controllerPos.getZ() + offset[1] + 0.5;
            AABB destinationBounds = entity.getBoundingBox().move(
                    x - entity.getX(),
                    y - entity.getY(),
                    z - entity.getZ()
            );

            if (level.noCollision(entity, destinationBounds)) {
                return new Vec3(x, y, z);
            }
        }

        return new Vec3(controllerPos.getX() + 2.5, y, controllerPos.getZ() + 2.5);
    }
}
