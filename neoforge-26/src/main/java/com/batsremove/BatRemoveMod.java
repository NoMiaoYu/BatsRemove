package com.batsremove;

import com.mojang.logging.LogUtils;

import net.minecraft.world.entity.ambient.Bat;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import org.slf4j.Logger;

@Mod(BatRemoveMod.MOD_ID)
public class BatRemoveMod {

    public static final String MOD_ID = "batsremove";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BatRemoveMod(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.register(this);
    }

    /** Prevents natural bat spawning. PositionCheck only fires for natural spawns, so /summon is unaffected. */
    @SubscribeEvent
    public void onNaturalSpawnCheck(MobSpawnEvent.PositionCheck event) {
        if (event.getEntity() instanceof Bat) {
            event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        BatsRemoveCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        BatRemovalManager.onServerStarted(event.getServer());
    }
}
