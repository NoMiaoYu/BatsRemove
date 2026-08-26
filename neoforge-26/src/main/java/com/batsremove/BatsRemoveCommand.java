package com.batsremove;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;

/**
 * Registers the {@code /batsremove} command (NeoForge).
 */
public final class BatsRemoveCommand {

    private BatsRemoveCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("batsremove")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_ADMIN))
                .executes(context -> execute(context.getSource()))
        );
    }

    private static int execute(CommandSourceStack source) {
        int removed = BatRemovalManager.removeAllBats(source.getServer());
        if (removed > 0) {
            source.sendSuccess(() -> Component.literal("Removed " + removed + " bat(s) from the world."), false);
        } else {
            source.sendSuccess(() -> Component.literal("No bats found to remove."), false);
        }
        return removed;
    }
}
