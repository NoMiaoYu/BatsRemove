package com.batsremove;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.storage.LevelResource;

/**
 * Core logic: auto-clear bats on first world load (once per save), plus the shared
 * "remove all bats" routine used by the command.
 */
public final class BatRemovalManager {

    private static final String MARKER_FILE = "bats_removed.dat";

    private BatRemovalManager() {
    }

    public static void onServerStarted(MinecraftServer server) {
        if (isSaveMarked(server)) {
            BatRemoveMod.LOGGER.info("BatsRemove: save already marked, skipping auto-clear.");
            return;
        }
        int removed = removeAllBats(server);
        markSave(server);
        BatRemoveMod.LOGGER.info("BatsRemove: auto-cleared {} existing bat(s) and marked this save.", removed);
    }

    /** Removes all loaded bats from every loaded world and returns how many were removed. */
    public static int removeAllBats(MinecraftServer server) {
        int total = 0;
        for (ServerLevel world : server.getAllLevels()) {
            total += removeBatsFromWorld(world);
        }
        return total;
    }

    private static int removeBatsFromWorld(ServerLevel world) {
        // Snapshot the bats first, because discard() mutates the entity list.
        List<? extends Bat> bats = world.getEntities(EntityType.BAT, e -> true);
        for (Bat bat : bats) {
            bat.discard();
        }
        return bats.size();
    }

    private static Path getMarkerPath(MinecraftServer server) {
        Path worldRoot = server.getWorldPath(LevelResource.ROOT);
        return worldRoot.resolve("data").resolve(MARKER_FILE);
    }

    private static boolean isSaveMarked(MinecraftServer server) {
        return Files.exists(getMarkerPath(server));
    }

    private static void markSave(MinecraftServer server) {
        try {
            Path marker = getMarkerPath(server);
            Files.createDirectories(marker.getParent());
            Files.write(marker, "bats_removed\n".getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            BatRemoveMod.LOGGER.error("BatsRemove: failed to write save marker", e);
        }
    }
}
