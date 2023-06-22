package org.betonquest.betonquest.compatibility.citizens;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.protocollib.hider.NPCHider;
import org.betonquest.betonquest.compatibility.protocollib.hider.UpdateVisibilityNowEvent;

@SuppressWarnings("PMD.CommentRequired")
public class CitizensIntegrator implements Integrator {
    private final BetonQuest plugin;

    private CitizensListener citizensListener;

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
            NPCHider.start(BetonQuest.getInstance().getLoggerFactory().create(NPCHider.class));
            plugin.registerEvents("updatevisibility", UpdateVisibilityNowEvent.class);
        }
        plugin.registerObjectives("npckill", NPCKillObjective.class);
        plugin.registerObjectives("npcinteract", NPCInteractObjective.class);
        plugin.registerObjectives("npcrange", NPCRangeObjective.class);
        plugin.registerEvents("movenpc", NPCMoveEvent.class);
        plugin.registerEvents("teleportnpc", NPCTeleportEvent.class);
        plugin.registerEvents("stopnpc", NPCStopEvent.class);
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
        // Empty
    }

}
