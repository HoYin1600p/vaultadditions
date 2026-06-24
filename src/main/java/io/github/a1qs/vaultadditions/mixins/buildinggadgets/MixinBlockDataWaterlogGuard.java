package io.github.a1qs.vaultadditions.mixins.buildinggadgets;

import com.direwolf20.buildinggadgets.common.tainted.building.tilesupport.ITileEntityData;
import com.direwolf20.buildinggadgets.common.tainted.building.view.BuildContext;
import io.github.a1qs.vaultadditions.compat.buildinggadgets.BuildingGadgetsWaterlogGuard;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Restriction(require = @Condition(type = Condition.Type.MOD, value = "buildinggadgets"))
@Pseudo
@Mixin(targets = "com.direwolf20.buildinggadgets.common.tainted.building.BlockData", remap = false)
public abstract class MixinBlockDataWaterlogGuard {

    @Redirect(
            method = "placeIn",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/direwolf20/buildinggadgets/common/tainted/building/tilesupport/ITileEntityData;placeIn(Lcom/direwolf20/buildinggadgets/common/tainted/building/view/BuildContext;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)Z"
            ),
            remap = false
    )
    private boolean vaultadditions$placeWithoutWaterlogging(ITileEntityData tileData, BuildContext context, BlockState state, BlockPos pos) {
        BlockState sanitized = BuildingGadgetsWaterlogGuard.sanitizeState(state);
        if (BuildingGadgetsWaterlogGuard.isWaterState(sanitized)) {
            return false;
        }
        return tileData.placeIn(context, sanitized, pos);
    }
}
