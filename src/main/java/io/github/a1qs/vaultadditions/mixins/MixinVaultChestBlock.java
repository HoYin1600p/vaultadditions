package io.github.a1qs.vaultadditions.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.a1qs.vaultadditions.container.AltarDoubleChestContainer;
import io.github.a1qs.vaultadditions.init.vault.ModGearAttributes;
import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.block.entity.VaultChestTileEntity;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(value = VaultChestBlock.class, remap = false, priority = 100)
public class MixinVaultChestBlock extends ChestBlock {
    public MixinVaultChestBlock(Properties p_51490_, Supplier<BlockEntityType<? extends ChestBlockEntity>> p_51491_) {
        super(p_51490_, p_51491_);
    }

    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true, remap = true)
    private void vaultadditions$allowAltarDoubleChest(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        if (!vaultadditions$isAltarChest((Block) (Object) this)) {
            return;
        }

        BlockState vanillaState = super.getStateForPlacement(context);
        if (vanillaState != null) {
            cir.setReturnValue(vanillaState.setValue(VaultChestBlock.VARIANT, VaultChestBlock.Variant.NORMAL));
        }
    }

    @Inject(method = "getMenuProvider", at = @At("HEAD"), cancellable = true, remap = true)
    private void vaultadditions$useDoubleAltarMenu(
            BlockState state,
            Level level,
            BlockPos pos,
            CallbackInfoReturnable<MenuProvider> cir
    ) {
        if (!vaultadditions$isAltarChest(state.getBlock()) || state.getValue(TYPE) == ChestType.SINGLE) {
            return;
        }

        Container container = ChestBlock.getContainer((ChestBlock) (Object) this, state, level, pos, false);
        if (container == null || container.getContainerSize() != AltarDoubleChestContainer.CHEST_SLOT_COUNT) {
            cir.setReturnValue(null);
            return;
        }

        cir.setReturnValue(new SimpleMenuProvider(
                (containerId, playerInventory, player) ->
                        new AltarDoubleChestContainer(containerId, playerInventory, container),
                new TranslatableComponent("container.vaultadditions.altar_double_chest")
        ));
    }

    private static boolean vaultadditions$isAltarChest(Block block) {
        return block == ModBlocks.ALTAR_CHEST || block == ModBlocks.ALTAR_CHEST_PLACEABLE;
    }

    @Redirect(method = "onDestroyedByPlayer", at = @At(value = "INVOKE", target = "Liskallia/vault/block/VaultChestBlock;hasStepBreaking(Liskallia/vault/block/entity/VaultChestTileEntity;)Z"))
    private boolean onDestroyedBreaching(VaultChestBlock instance, VaultChestTileEntity tileEntity, @Local(argsOnly = true) Player player) {
        ItemStack mainHandItem = player.getMainHandItem();
        return (!AttributeGearData.hasData(mainHandItem) || !VaultGearData.read(mainHandItem).hasAttribute(ModGearAttributes.BREACHING)) && instance.hasStepBreaking(tileEntity);
    }

    @Redirect(method = "playerDestroy", at = @At(value = "INVOKE", target = "Liskallia/vault/block/VaultChestBlock;hasStepBreaking(Liskallia/vault/block/entity/VaultChestTileEntity;)Z", remap = false), remap = true)
    private boolean playerDestroyBreaching(VaultChestBlock instance, VaultChestTileEntity tileEntity, @Local(argsOnly = true) Player player) {
        ItemStack mainHandItem = player.getMainHandItem();
        return (!AttributeGearData.hasData(mainHandItem) || !VaultGearData.read(mainHandItem).hasAttribute(ModGearAttributes.BREACHING)) && instance.hasStepBreaking(tileEntity);
    }

    @Override
    public float getDestroyProgress(BlockState pState, Player pPlayer, BlockGetter pLevel, BlockPos pPos) {
        boolean hasBreach = GearDataCache.of(pPlayer.getMainHandItem()).hasAttribute(ModGearAttributes.BREACHING);
        if (!hasBreach) {
            return super.getDestroyProgress(pState, pPlayer, pLevel, pPos);
        }

        float f = Math.max(40.0F, pState.getDestroySpeed(pLevel, pPos));
        int i = ForgeHooks.isCorrectToolForDrops(pState, pPlayer) ? 30 : 100;
        return pPlayer.getDigSpeed(pState, pPos) / f / (float) i;
    }
}
