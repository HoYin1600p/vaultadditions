package io.github.a1qs.vaultadditions.item;

import io.github.a1qs.vaultadditions.block.GlobeExpanderBlock;
import io.github.a1qs.vaultadditions.data.PlayerAdditionalVaultStatData;
import iskallia.vault.core.vault.VaultUtils;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class PowerCrystal extends Item {
    public PowerCrystal(Properties pProperties) {
        super(pProperties);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
        if (VaultBarOverlay.vaultLevel >= 100) {
            tooltip.add(new TextComponent("Consume to gain one").withStyle(ChatFormatting.YELLOW)
                    .append(new TextComponent(" Power Point").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(16724414)))));
        } else {
            tooltip.add(new TextComponent("I seem to be too weak to make use of this...").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.RED));
        }
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack heldItemStack = player.getItemInHand(hand);
        if (VaultUtils.isVaultLevel(level)) {
            return InteractionResultHolder.fail(heldItemStack);
        }

        if (GlobeExpanderBlock.isEnabled()) {
            return InteractionResultHolder.fail(heldItemStack);
        }

        int consumeAmount = player.isShiftKeyDown() ? heldItemStack.getCount() : 1;
        if (level.isClientSide()) {
            return InteractionResultHolder.sidedSuccess(heldItemStack, true);
        }

        ServerLevel serverLevel = (ServerLevel) level;
        ServerPlayer serverPlayer = (ServerPlayer) player;
        PlayerVaultStatsData statsData = PlayerVaultStatsData.get(serverLevel);
        if (statsData.getVaultStats(serverPlayer).getVaultLevel() < 100) {
            return InteractionResultHolder.fail(heldItemStack);
        }

        PlayerAdditionalVaultStatData additionalStatsData = PlayerAdditionalVaultStatData.get(serverLevel);
        additionalStatsData.addPowerPoints(serverPlayer, consumeAmount);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            heldItemStack.shrink(consumeAmount);
        }

        return InteractionResultHolder.sidedSuccess(heldItemStack, false);
    }
}
