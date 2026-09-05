package io.github.a1qs.vaultadditions.mixins.rune_boss;

import iskallia.vault.block.entity.BossRunePillarTileEntity;
import iskallia.vault.core.world.storage.BlockCuboid;
import iskallia.vault.core.world.storage.WorldZone;
import iskallia.vault.world.data.WorldZonesData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossRunePillarTileEntity.class)
public abstract class MixinBossRunePillarTileEntity extends BlockEntity {
    @Shadow(remap = false)
    @Final
    private BossRunePillarTileEntity.Config config;

    @Shadow(remap = false)
    private int zoneId;

    @Shadow(remap = false)
    private boolean populated;

    protected MixinBossRunePillarTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "onLoad", at = @At("TAIL"), remap = false)
    private void vaultadditions$repairZoneAfterLoad(CallbackInfo ci) {
        vaultadditions$ensureZoneRegistered();
    }

    @Inject(method = "load", at = @At("RETURN"))
    private void vaultadditions$repairZoneAfterNbt(CompoundTag nbt, CallbackInfo ci) {
        if (!nbt.contains("config")) {
            populated = false;
            if (level instanceof ServerLevel) {
                ((BossRunePillarTileEntity) (Object) this).onPopulate();
            }
        }

        vaultadditions$ensureZoneRegistered();
    }

    private void vaultadditions$ensureZoneRegistered() {
        if (zoneId > 0 || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockCuboid zone = ((AccessorBossRunePillarConfig) (Object) config).vaultadditions$getZone();
        if (zone == null) {
            return;
        }

        zoneId = WorldZonesData.get(serverLevel.getServer())
                .getOrCreate(serverLevel.dimension())
                .add(new WorldZone().add(zone.offset(worldPosition)).setModify(false));
        setChanged();
    }
}
