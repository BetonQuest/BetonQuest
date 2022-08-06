package org.betonquest.betonquest.compatibility.protocollib;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.protocollib.conversation.MenuConvIO;
import org.betonquest.betonquest.compatibility.protocollib.conversation.PacketInterceptor;
import org.betonquest.betonquest.compatibility.protocollib.hider.MythicHider;
import org.betonquest.betonquest.compatibility.protocollib.hider.NPCHider;
import org.betonquest.betonquest.compatibility.protocollib.hider.UpdateVisibilityNowEvent;
import org.betonquest.betonquest.exceptions.HookException;
import org.betonquest.betonquest.exceptions.UnsupportedVersionException;
import org.betonquest.betonquest.modules.versioning.UpdateStrategy;
import org.betonquest.betonquest.modules.versioning.Version;
import org.betonquest.betonquest.modules.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("PMD.CommentRequired")
public class ProtocolLibIntegrator implements Integrator {

    private final BetonQuest plugin;
    private NPCGlow npcGlow;

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
            plugin.registerEvents("updatevisibility", UpdateVisibilityNowEvent.class);
            npcGlow = new NPCGlow();
        }
        if (Compatibility.getHooked().contains("MythicMobs")) {
            MythicHider.start();
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
        //if MythicHider is running, reload it
        if (MythicHider.getInstance() != null) {
            MythicHider.start();
        }
        //refresh NPCGlow instance
        npcGlow.refresh();
    }

    @Override
    public void close() {
        // Empty
    }

}
