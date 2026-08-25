package com.batsremove;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

/**
 * Registers the {@code /batsremove} command.
 *
 * <p>Requires operator permission level 2 by default. Run {@code /batsremove} to remove every
 * loaded bat from all loaded worlds.
 */
public final class BatsRemoveCommand {

    private BatsRemoveCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            CommandManager.literal("batsremove")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> execute(context.getSource()))
        );
    }

    private static int execute(ServerCommandSource source) {
        int removed = BatRemovalManager.removeAllBats(source.getServer());
        if (removed > 0) {
            source.sendFeedback(Text.literal("Removed " + removed + " bat(s) from the world."), false);
        } else {
            source.sendFeedback(Text.literal("No bats found to remove."), false);
        }
        return removed;
    }
}
