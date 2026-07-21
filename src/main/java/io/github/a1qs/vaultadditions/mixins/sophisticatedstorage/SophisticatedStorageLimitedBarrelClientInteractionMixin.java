package io.github.a1qs.vaultadditions.mixins.sophisticatedstorage;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;

@Restriction(require = @Condition(type = Condition.Type.MOD, value = "sophisticatedstorage"))
@Pseudo
@Mixin(MultiPlayerGameMode.class)
public abstract class SophisticatedStorageLimitedBarrelClientInteractionMixin {
    @Unique
    private static final String vaultadditions$LIMITED_BARREL_BLOCK_CLASS = "net.p3pp3rf1y.sophisticatedstorage.block.LimitedBarrelBlock";

    @Unique
    private static Class<?> vaultadditions$limitedBarrelBlockClass = null;

    @Unique
    private static Method vaultadditions$getFacingMethod = null;

    @Unique
    private boolean vaultadditions$spoofedLimitedBarrelSneakRelease = false;

    @Unique
    private InteractionHand vaultadditions$limitedBarrelInteractionHand = null;

    @ModifyVariable(
            method = "useItemOn",
            at = @At("HEAD"),
            argsOnly = true,
            index = 4
    )
    private BlockHitResult vaultadditions$trackLimitedBarrelInteractionHand(
            BlockHitResult hitResult,
            LocalPlayer player,
            ClientLevel level,
            InteractionHand hand
    ) {
        this.vaultadditions$limitedBarrelInteractionHand = hand;
        return hitResult;
    }

    @ModifyArg(
            method = "useItemOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/game/ServerboundUseItemOnPacket;<init>(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)V"
            ),
            index = 1
    )
    private BlockHitResult vaultadditions$sneakOpenLimitedBarrelFrontFacePacket(BlockHitResult hitResult) {
        if (this.vaultadditions$limitedBarrelInteractionHand != InteractionHand.MAIN_HAND) {
            return hitResult;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;
        if (player == null || level == null || minecraft.getConnection() == null) {
            return hitResult;
        }

        BlockHitResult rewrittenHitResult = vaultadditions$rewriteLimitedBarrelFrontFace(hitResult, player, level);
        if (rewrittenHitResult == hitResult) {
            return hitResult;
        }

        // Temporarily release sneak so empty-main-hand front-face clicks open the limited barrel menu.
        minecraft.getConnection().send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.RELEASE_SHIFT_KEY));
        this.vaultadditions$spoofedLimitedBarrelSneakRelease = true;
        return rewrittenHitResult;
    }

    @Inject(method = "useItemOn", at = @At("RETURN"), cancellable = true)
    private void vaultadditions$restoreLimitedBarrelSneak(
            LocalPlayer player,
            ClientLevel level,
            InteractionHand hand,
            BlockHitResult hitResult,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (!this.vaultadditions$spoofedLimitedBarrelSneakRelease) {
            return;
        }

        this.vaultadditions$spoofedLimitedBarrelSneakRelease = false;
        this.vaultadditions$limitedBarrelInteractionHand = null;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() != null && player.isSecondaryUseActive()) {
            minecraft.getConnection().send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.PRESS_SHIFT_KEY));
        }

        cir.setReturnValue(InteractionResult.SUCCESS);
    }

    @Inject(method = "useItemOn", at = @At("RETURN"))
    private void vaultadditions$clearLimitedBarrelInteractionHand(
            LocalPlayer player,
            ClientLevel level,
            InteractionHand hand,
            BlockHitResult hitResult,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        this.vaultadditions$limitedBarrelInteractionHand = null;
    }

    private static BlockHitResult vaultadditions$rewriteLimitedBarrelFrontFace(BlockHitResult hitResult, LocalPlayer player, ClientLevel level) {
        BlockState state = level.getBlockState(hitResult.getBlockPos());
        Direction facing = vaultadditions$getLimitedBarrelFacing(state);
        if (facing == null) {
            return hitResult;
        }

        if (!player.isSecondaryUseActive() || hitResult.getDirection() != facing || !player.getMainHandItem().isEmpty()) {
            return hitResult;
        }

        return hitResult.withDirection(vaultadditions$getMenuOpenDirection(facing));
    }

    private static Direction vaultadditions$getLimitedBarrelFacing(BlockState state) {
        Object block = state.getBlock();
        if (!vaultadditions$isLimitedBarrelBlock(block)) {
            return null;
        }

        try {
            Method getFacingMethod = vaultadditions$getFacingMethod;
            if (getFacingMethod == null) {
                getFacingMethod = vaultadditions$limitedBarrelBlockClass.getMethod("getFacing", BlockState.class);
                vaultadditions$getFacingMethod = getFacingMethod;
            }

            Object facing = getFacingMethod.invoke(block, state);
            return facing instanceof Direction direction ? direction : null;
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return null;
        }
    }

    private static boolean vaultadditions$isLimitedBarrelBlock(Object block) {
        Class<?> limitedBarrelBlockClass = vaultadditions$limitedBarrelBlockClass;
        if (limitedBarrelBlockClass == null) {
            try {
                limitedBarrelBlockClass = Class.forName(vaultadditions$LIMITED_BARREL_BLOCK_CLASS, false, block.getClass().getClassLoader());
                vaultadditions$limitedBarrelBlockClass = limitedBarrelBlockClass;
            } catch (ClassNotFoundException | LinkageError ignored) {
                return false;
            }
        }

        return limitedBarrelBlockClass.isInstance(block);
    }

    private static Direction vaultadditions$getMenuOpenDirection(Direction facing) {
        return facing.getAxis().isVertical() ? Direction.NORTH : Direction.UP;
    }
}
