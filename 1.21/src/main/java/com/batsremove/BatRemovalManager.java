package com.batsremove;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.WorldSavePath;

/**
 * Core logic: auto-clear bats on first world load (once per save), plus the shared
 * "remove all bats" routine used by the command.
 *
 * <p>Save marking deliberately uses a plain marker file ({@code <world>/data/bats_removed.dat})
 * instead of {@link net.minecraft.world.PersistentStateManager} so that the exact same code
 * works across every Minecraft version, independent of that API's signature changes.
 */
public final class BatRemovalManager {

    private static final String MARKER_FILE = "bats_removed.dat";

    private BatRemovalManager() {
    }

    /**
     * Called on {@code ServerLifecycleEvents.SERVER_STARTED}. Clears existing bats the first
     * time a save is loaded and marks it, so subsequent loads skip the automatic clear.
     */
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
        for (ServerWorld world : server.getWorlds()) {
            total += removeBatsFromWorld(world);
        }
        return total;
    }

    private static int removeBatsFromWorld(ServerWorld world) {
        // Snapshot the bats first, because discard() mutates the entity list.
        List<BatEntity> bats = new ArrayList<>();
        for (Entity entity : world.getEntitiesByType(TypeFilter.instanceOf(Entity.class), e -> true)) {
            if (entity instanceof BatEntity bat) {
                bats.add(bat);
            }
        }
        for (BatEntity bat : bats) {
            bat.discard();
        }
        return bats.size();
    }

    private static Path getMarkerPath(MinecraftServer server) {
        Path worldRoot = server.getSavePath(WorldSavePath.ROOT);
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
