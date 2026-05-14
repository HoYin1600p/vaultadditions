package io.github.a1qs.vaultadditions.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.a1qs.vaultadditions.config.ServerConfigs;
import io.github.a1qs.vaultadditions.data.PlayerAdditionalVaultStatData;
import io.github.a1qs.vaultadditions.data.PlayerPowersData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PowerSkillCommands {
    public PowerSkillCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("vaultadditions")
                .requires(sender -> sender.hasPermission(this.getRequiredPermissionLevel()))
                .then(Commands.literal("power")
                        .then(Commands.literal("resetPowers")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(this::resetPowers)
                                )
                        )
                        .then(Commands.literal("addPoints")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(this::addPoints)
                                        )

                                )
                        )
                        .then(Commands.literal("globe-expander")
                                .then(Commands.literal("on")
                                        .executes(context -> setGlobeExpander(context, true))
                                )
                                .then(Commands.literal("off")
                                        .executes(context -> setGlobeExpander(context, false))
                                )
                        )
                )

        );
    }



    private int resetPowers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        PlayerPowersData data = PlayerPowersData.getServer();
        data.resetPowers(player);
        context.getSource().sendSuccess(new TextComponent("Reset " + player.getName().getString() + " Power Skills"), true);

        return 0;
    }

    private int addPoints(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int amount = IntegerArgumentType.getInteger(context, "amount");
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        PlayerAdditionalVaultStatData data = PlayerAdditionalVaultStatData.get(player.getLevel());
        data.addPowerPoints(player, amount);
        context.getSource().sendSuccess(new TextComponent("Added " + amount + " of Power points to " + player.getName().getString()), true);

        return 0;
    }

    private int setGlobeExpander(CommandContext<CommandSourceStack> context, boolean enabled) {
        ServerConfigs.GLOBE_EXPANDER_ENABLED.set(enabled);
        ServerConfigs.SPEC.save();
        context.getSource().sendSuccess(new TextComponent("Globe Expander power crystal mode is now " + (enabled ? "On" : "Off")), true);
        return 0;
    }




    public int getRequiredPermissionLevel() {
        return 2;
    }

}
