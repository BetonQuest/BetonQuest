package org.betonquest.betonquest.compatibility.citizens;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.protocollib.hider.NPCHider;
import org.betonquest.betonquest.compatibility.protocollib.hider.UpdateVisibilityNowEvent;

import java.util.Arrays;


@SuppressWarnings("PMD.CommentRequired")
public class CitizensIntegrator implements Integrator {

    private final BetonQuest plugin;

    private CitizensListener citizensListener;

    public CitizensIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        citizensListener = new CitizensListener();
        new CitizensWalkingListener();
        if (Compatibility.getHooked().contains("EffectLib")) {
            new CitizensParticle();
        }

        // if HolographicAPI is hooked, start CitizensHologram
        if (Compatibility.getHooked().contains("HolographicDisplays")) {
            new CitizensHologram();
        }

        // if ProtocolLib is hooked, start NPCHider
        if (Compatibility.getHooked().contains("ProtocolLib")) {
            NPCHider.start();
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
        if (Compatibility.getHooked().containsAll(Arrays.asList("Citizens", "EffectLib"))) {
            CitizensParticle.reload();
        }

        if (Compatibility.getHooked().containsAll(Arrays.asList("Citizens", "HolographicDisplays"))) {
            CitizensHologram.reload();
        }
        if (Compatibility.getHooked().contains("Citizens")) {
            citizensListener.reload();
        }
    }

    @Override
    public void close() {
        // Empty
    }

}
