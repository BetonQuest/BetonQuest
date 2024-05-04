package org.betonquest.betonquest.compatibility.citizens;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.citizens.events.move.CitizensMoveEvent;
import org.betonquest.betonquest.compatibility.citizens.events.move.CitizensMoveEventFactory;
import org.betonquest.betonquest.compatibility.citizens.events.move.CitizensMoveListener;
import org.betonquest.betonquest.compatibility.citizens.events.move.CitizensStopEventFactory;
import org.betonquest.betonquest.compatibility.protocollib.hider.NPCHider;
import org.betonquest.betonquest.compatibility.protocollib.hider.UpdateVisibilityNowEvent;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitScheduler;

@SuppressWarnings("PMD.CommentRequired")
public class CitizensIntegrator implements Integrator {
    private final BetonQuest plugin;

    private CitizensListener citizensListener;

    /**
     * Handles NPC movement of the {@link CitizensMoveEvent}.
     */
    private CitizensMoveListener citizensMoveListener;

    public CitizensIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        final BetonQuestLoggerFactory loggerFactory = BetonQuest.getInstance().getLoggerFactory();
        citizensListener = new CitizensListener(loggerFactory, loggerFactory.create(CitizensListener.class));
        new CitizensWalkingListener();

        // if ProtocolLib is hooked, start NPCHider
        if (Compatibility.getHooked().contains("ProtocolLib")) {
            NPCHider.start(loggerFactory.create(NPCHider.class));
            plugin.registerEvents("updatevisibility", UpdateVisibilityNowEvent.class);
        }
        plugin.registerObjectives("npckill", NPCKillObjective.class);
        plugin.registerObjectives("npcinteract", NPCInteractObjective.class);
        plugin.registerObjectives("npcrange", NPCRangeObjective.class);
        final Server server = plugin.getServer();
        final BukkitScheduler scheduler = server.getScheduler();
        citizensMoveListener = new CitizensMoveListener(loggerFactory.create(CitizensMoveListener.class));
        server.getPluginManager().registerEvents(citizensMoveListener, plugin);
        plugin.registerNonStaticEvent("movenpc", new CitizensMoveEventFactory(server, scheduler, plugin, citizensMoveListener));
        plugin.registerEvents("teleportnpc", NPCTeleportEvent.class);
        plugin.registerEvent("stopnpc", new CitizensStopEventFactory(server, scheduler, plugin));
        plugin.registerConversationIO("chest", CitizensInventoryConvIO.class);
        plugin.registerConversationIO("combined", CitizensInventoryConvIO.CitizensCombined.class);
        plugin.registerVariable("citizen", CitizensVariable.class);
        plugin.registerConditions("npcdistance", NPCDistanceCondition.class);
        plugin.registerConditions("npclocation", NPCLocationCondition.class);
        if (Compatibility.getHooked().contains("WorldGuard")) {
            plugin.registerConditions("npcregion", NPCRegionCondition.class);
        }
    }

    @Override
    public void reload() {
        citizensListener.reload();
    }

    @Override
    public void close() {
        HandlerList.unregisterAll(citizensMoveListener);
    }
}
