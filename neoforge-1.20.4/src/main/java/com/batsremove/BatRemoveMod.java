package com.batsremove;

import com.mojang.logging.LogUtils;

import net.minecraft.world.entity.ambient.Bat;
import net.neoforged.bus.api.Event;
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

    /** Prevents natural bat spawning. 1.20.2-1.20.4 use the bus Event.Result style. */
    @SubscribeEvent
    public void onNaturalSpawnCheck(MobSpawnEvent.PositionCheck event) {
        if (event.getEntity() instanceof Bat) {
            event.setResult(Event.Result.DENY);
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
