package pl.betoncraft.betonquest.compatibility.protocollib;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.compatibility.Integrator;
import pl.betoncraft.betonquest.compatibility.protocollib.conversation.MenuConvIO;
import pl.betoncraft.betonquest.compatibility.protocollib.conversation.PacketInterceptor;


@SuppressWarnings("PMD.CommentRequired")
public class ProtocolLibIntegrator implements Integrator {

    private final BetonQuest plugin;

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
        plugin.registerConversationIO("menu", MenuConvIO.class);
        plugin.registerInterceptor("packet", PacketInterceptor.class);
    }

    @Override
    public void reload() {
        //if NPCHider is running, reload it
        if (NPCHider.getInstance() != null) {
            NPCHider.start();
        }
    }

    @Override
    public void close() {

    }

}
