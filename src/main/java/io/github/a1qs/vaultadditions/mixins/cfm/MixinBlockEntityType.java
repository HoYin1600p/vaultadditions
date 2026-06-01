package io.github.a1qs.vaultadditions.mixins.cfm;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(require = @Condition(type = Condition.Type.MOD, value = "cfm"))
@Mixin(BlockEntityType.class)
public abstract class MixinBlockEntityType {
    private static final ResourceLocation CFM_MAIL_BOX_ENTITY = new ResourceLocation("cfm", "mail_box");
    private static final String CFM_MAIL_BOX_BLOCK_CLASS = "com.mrcrayfish.furniture.block.MailBoxBlock";

    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void vaultadditions$allowGeneratedCfmMailBoxes(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        BlockEntityType<?> self = (BlockEntityType<?>) (Object) this;
        if (CFM_MAIL_BOX_ENTITY.equals(self.getRegistryName()) && vaultadditions$isCfmMailBoxBlock(state.getBlock().getClass())) {
            cir.setReturnValue(true);
        }
    }

    private static boolean vaultadditions$isCfmMailBoxBlock(Class<?> blockClass) {
        Class<?> current = blockClass;
        while (current != null) {
            if (CFM_MAIL_BOX_BLOCK_CLASS.equals(current.getName())) {
                return true;
            }
            current = current.getSuperclass();
        }
        return false;
    }
}
