package org.betonquest.betonquest.compatibility.protocollib;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.protocollib.conversation.MenuConvIO;
import org.betonquest.betonquest.compatibility.protocollib.conversation.PacketInterceptor;
import org.betonquest.betonquest.compatibility.protocollib.hider.NPCGlowing;
import org.betonquest.betonquest.compatibility.protocollib.hider.NPCHider;
import org.betonquest.betonquest.compatibility.protocollib.hider.UpdateVisibilityNowEvent;
import org.betonquest.betonquest.exceptions.HookException;
import org.betonquest.betonquest.exceptions.UnsupportedVersionException;
import org.betonquest.betonquest.utils.versioning.UpdateStrategy;
import org.betonquest.betonquest.utils.versioning.Version;
import org.betonquest.betonquest.utils.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("PMD.CommentRequired")
public class ProtocolLibIntegrator implements Integrator {

    private final BetonQuest plugin;

    public ProtocolLibIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public void hook() throws HookException {
        final Plugin protocolLib = Bukkit.getPluginManager().getPlugin("ProtocolLib");
        final Version protocolLibVersion = new Version(protocolLib.getDescription().getVersion());
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR, "SNAPSHOT-b");
        if (comparator.isOtherNewerThanCurrent(protocolLibVersion, new Version("4.7.1-SNAPSHOT-b531"))) {
            throw new UnsupportedVersionException(protocolLib, "4.7.1-SNAPSHOT-b531");
        }
        // if Citizens is hooked, start NPCHider
        if (Compatibility.getHooked().contains("Citizens")) {
            NPCHider.start();
            NPCGlowing.start();
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
        if(NPCGlowing.getInstance() != null){
            NPCGlowing.start();
        }
    }

    @Override
    public void close() {
        // Empty
    }

}
