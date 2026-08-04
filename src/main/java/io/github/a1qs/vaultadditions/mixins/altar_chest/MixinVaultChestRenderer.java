package io.github.a1qs.vaultadditions.mixins.altar_chest;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import io.github.a1qs.vaultadditions.client.render.AltarDoubleChestModel;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.block.render.VaultChestRenderer;
import iskallia.vault.init.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VaultChestRenderer.class, remap = false)
public abstract class MixinVaultChestRenderer {
    @Inject(
            method = "render(Lnet/minecraft/world/level/block/entity/ChestBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void vaultadditions$renderConnectedDoubleAltarChest(
            ChestBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay,
            CallbackInfo ci
    ) {
        BlockState state = blockEntity.getBlockState();
        if ((state.getBlock() != ModBlocks.ALTAR_CHEST && state.getBlock() != ModBlocks.ALTAR_CHEST_PLACEABLE)
                || !state.hasProperty(ChestBlock.TYPE)) {
            return;
        }

        ChestType chestType = state.getValue(ChestBlock.TYPE);
        if (chestType == ChestType.SINGLE
                || state.getOptionalValue(VaultChestBlock.VARIANT).orElse(VaultChestBlock.Variant.NORMAL)
                != VaultChestBlock.Variant.NORMAL) {
            return;
        }

        Level level = blockEntity.getLevel();
        float openness;
        int combinedLight = packedLight;
        if (level == null) {
            openness = blockEntity.getOpenNess(partialTick);
        } else {
            DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combined =
                    ((ChestBlock) state.getBlock()).combine(
                            state,
                            level,
                            blockEntity.getBlockPos(),
                            true
                    );
            openness = combined.<Float2FloatFunction>apply(ChestBlock.opennessCombiner(blockEntity))
                    .get(partialTick);
            combinedLight = combined.apply(new BrightnessCombiner<>()).applyAsInt(packedLight);
        }

        openness = 1.0F - openness;
        openness = 1.0F - openness * openness * openness;

        Direction facing = state.getValue(ChestBlock.FACING);
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(-0.5D, -0.5D, -0.5D);
        AltarDoubleChestModel.renderHalf(
                poseStack,
                bufferSource,
                combinedLight,
                packedOverlay,
                chestType,
                openness
        );
        poseStack.popPose();
        ci.cancel();
    }
}
