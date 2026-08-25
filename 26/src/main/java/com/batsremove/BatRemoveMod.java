package com.batsremove;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityTypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatRemoveMod implements ModInitializer {

    public static final String MOD_ID = "batsremove";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // 26.x: bats naturally spawn from biome spawn lists. Remove them from every biome
        // so they no longer spawn naturally. Player-summoned bats are unaffected.
        BiomeModifications.create(Identifier.fromNamespaceAndPath(MOD_ID, "remove_bats"))
            .add(
                ModificationPhase.REMOVALS,
                selectionContext -> true,
                context -> context.getMobSpawnSettings().removeSpawnsOfEntityType(EntityTypes.BAT)
            );

        // /batsremove command
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            BatsRemoveCommand.register(dispatcher)
        );

        // Auto-clear existing bats the first time a save is loaded.
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
    }

    private void onServerStarted(MinecraftServer server) {
        BatRemovalManager.onServerStarted(server);
    }
}
