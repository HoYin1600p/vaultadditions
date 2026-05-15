package io.github.a1qs.vaultadditions.mixins.buildinggadgets;

import appeng.api.networking.IInWorldGridNodeHost;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Client-side fix: the material list in Building Gadgets calls InventoryHelper.index()
 * from the client thread, where AE2's grid nodes do not exist (they are server-only).
 * MixinInventoryLinker therefore falls back to the WAP's empty upgrade-card inventory,
 * making the material list show 0/X for every block even when the ME network has them.
 *
 * Fix: when the gadget is linked to an AE2 block (IInWorldGridNodeHost) and we are on
 * the client side, return a CreativeItemIndex which shows all required materials as
 * fully available.  The actual availability check and extraction happen server-side.
 *
 * Targets: buildinggadgets-3.13.2-build.21+mc1.18.2 (client side only)
 */
@Restriction(require = {
        @Condition(type = Condition.Type.MOD, value = "buildinggadgets"),
        @Condition(type = Condition.Type.MOD, value = "appliedenergistics2")
})
@Pseudo
@Mixin(targets = "com.direwolf20.buildinggadgets.common.tainted.inventory.InventoryHelper", remap = false)
public abstract class MixinInventoryHelperAe2Client {

    @Inject(method = "index", at = @At("RETURN"), cancellable = true, remap = false)
    private static void vaultadditions$creativeIndexForAe2(
            ItemStack gadget,
            Player player,
            CallbackInfoReturnable<Object> cir) {

        Level level = player.level;
        if (!level.isClientSide()) return;

        CompoundTag tag = gadget.getTag();
        if (tag == null || !tag.contains("bound_te_pos") || !tag.contains("bound_te_dim")) return;

        String linkedDim = tag.getString("bound_te_dim");
        if (!level.dimension().location().toString().equals(linkedDim)) return;

        BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("bound_te_pos"));
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof IInWorldGridNodeHost)) return;

        // Linked to AE2: show all materials as available in the client UI.
        // Real availability is enforced server-side when the paste executes.
        try {
            Object creativeIndex = Class
                    .forName("com.direwolf20.buildinggadgets.common.tainted.inventory.CreativeItemIndex")
                    .getDeclaredConstructor()
                    .newInstance();
            cir.setReturnValue(creativeIndex);
        } catch (ReflectiveOperationException ignored) {
            // BG version mismatch — leave default behaviour
        }
    }
}
