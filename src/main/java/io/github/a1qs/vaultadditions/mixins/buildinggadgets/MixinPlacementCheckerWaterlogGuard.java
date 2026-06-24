package io.github.a1qs.vaultadditions.mixins.buildinggadgets;

import com.direwolf20.buildinggadgets.common.tainted.building.PlacementTarget;
import com.direwolf20.buildinggadgets.common.tainted.building.view.BuildContext;
import io.github.a1qs.vaultadditions.compat.buildinggadgets.BuildingGadgetsWaterlogGuard;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BiPredicate;

@SuppressWarnings({"rawtypes", "unchecked"})
@Restriction(require = @Condition(type = Condition.Type.MOD, value = "buildinggadgets"))
@Pseudo
@Mixin(targets = "com.direwolf20.buildinggadgets.common.tainted.building.PlacementChecker", remap = false)
public abstract class MixinPlacementCheckerWaterlogGuard {

    @Redirect(
            method = "checkPositionWithResult",
            at = @At(value = "INVOKE", target = "Ljava/util/function/BiPredicate;test(Ljava/lang/Object;Ljava/lang/Object;)Z"),
            remap = false
    )
    private boolean vaultadditions$rejectWaterStatesBeforeCost(BiPredicate placeCheck, Object context, Object target) {
        if (context instanceof BuildContext buildContext && target instanceof PlacementTarget placementTarget) {
            if (BuildingGadgetsWaterlogGuard.isWaterData(placementTarget.getData())) {
                return false;
            }
            return placeCheck.test(buildContext, placementTarget);
        }
        return placeCheck.test(context, target);
    }
}
