package io.github.a1qs.vaultadditions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.a1qs.vaultadditions.config.SophisticatedControllerRangeConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

public class SophisticatedStorageCommands {
    public SophisticatedStorageCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("vaultadditions")
                .requires(sender -> sender.hasPermission(this.getRequiredPermissionLevel()))
                .then(Commands.literal("sophisticated-range")
                        .then(Commands.argument("value", IntegerArgumentType.integer(1, SophisticatedControllerRangeConfig.MAX_RANGE))
                                .executes(this::setRangeWithWarning)
                        )
                        .then(Commands.literal("confirm")
                                .then(Commands.argument("value", IntegerArgumentType.integer(1, SophisticatedControllerRangeConfig.MAX_RANGE))
                                        .executes(this::setRangeConfirmed)
                                )
                        )
                )
        );
    }

    public int getRequiredPermissionLevel() {
        return 2;
    }

    private int setRangeWithWarning(CommandContext<CommandSourceStack> context) {
        int range = IntegerArgumentType.getInteger(context, "value");
        if (range > SophisticatedControllerRangeConfig.WARNING_RANGE) {
            MutableComponent message = new TextComponent("Warning: controller ranges above "
                    + SophisticatedControllerRangeConfig.WARNING_RANGE
                    + " can be expensive. ")
                    .withStyle(ChatFormatting.YELLOW)
                    .append(new TextComponent("[YES]")
                            .withStyle(Style.EMPTY
                                    .withColor(ChatFormatting.GREEN)
                                    .withBold(true)
                                    .withClickEvent(new ClickEvent(
                                            ClickEvent.Action.RUN_COMMAND,
                                            "/vaultadditions sophisticated-range confirm " + range
                                    ))
                            ))
                    .append(new TextComponent(" to save " + range + ".").withStyle(ChatFormatting.YELLOW));
            context.getSource().sendFailure(message);
            return 0;
        }

        return saveRange(context, range);
    }

    private int setRangeConfirmed(CommandContext<CommandSourceStack> context) {
        return saveRange(context, IntegerArgumentType.getInteger(context, "value"));
    }

    private int saveRange(CommandContext<CommandSourceStack> context, int range) {
        SophisticatedControllerRangeConfig.setControllerRange(range);
        context.getSource().sendSuccess(new TextComponent("Sophisticated Storage controller range saved as "
                + SophisticatedControllerRangeConfig.getControllerRange()
                + " in "
                + SophisticatedControllerRangeConfig.getConfigPath()).withStyle(ChatFormatting.GREEN), true);
        return Command.SINGLE_SUCCESS;
    }
}
