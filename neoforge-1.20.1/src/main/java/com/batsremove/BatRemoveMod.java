package com.batsremove;

import com.mojang.logging.LogUtils;

import net.minecraft.world.entity.ambient.Bat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.slf4j.Logger;

@Mod(BatRemoveMod.MOD_ID)
public class BatRemoveMod {

    public static final String MOD_ID = "batsremove";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BatRemoveMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /** Prevents natural bat spawning (1.20.1 Forge API: Event.Result style). */
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
