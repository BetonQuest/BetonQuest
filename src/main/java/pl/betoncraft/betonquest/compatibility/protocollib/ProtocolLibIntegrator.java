package pl.betoncraft.betonquest.compatibility.protocollib;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.compatibility.Integrator;
import pl.betoncraft.betonquest.compatibility.protocollib.conversation.MenuConvIO;
import pl.betoncraft.betonquest.compatibility.protocollib.conversation.PacketInterceptor;
import pl.betoncraft.betonquest.compatibility.protocollib.hider.NPCHider;
import pl.betoncraft.betonquest.compatibility.protocollib.hider.PlayerHider;
import pl.betoncraft.betonquest.compatibility.protocollib.hider.UpdateVisibilityNowEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.util.logging.Level;


@SuppressWarnings("PMD.CommentRequired")
public class ProtocolLibIntegrator implements Integrator {

    private final BetonQuest plugin;
    private PlayerHider playerHider;

    public ProtocolLibIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        // if Citizens is hooked, start NPCHider
        if (Compatibility.getHooked().contains("Citizens")) {
            NPCHider.start();
            plugin.registerEvents("updatevisibility", UpdateVisibilityNowEvent.class);
        }

        try {
            playerHider = new PlayerHider();
            UpdateVisibilityNowEvent.setHider(playerHider);
        } catch (InstructionParseException e) {
            LogUtils.getLogger().log(Level.SEVERE, "Could not start PlayerHider! " + e.getMessage(), e);
        }
        plugin.registerConversationIO("menu", MenuConvIO.class);
        plugin.registerInterceptor("packet", PacketInterceptor.class);
    }

    @Override
    public void reload() {
        //if NPCHider is running, reload it
        if (NPCHider.getInstance() != null) {
            NPCHider.start();
        }
        playerHider.stop();
        try {
            playerHider = new PlayerHider();
            UpdateVisibilityNowEvent.setHider(playerHider);
        } catch (InstructionParseException e) {
            LogUtils.getLogger().log(Level.SEVERE, "Could not start PlayerHider! " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        // Empty
    }

}
