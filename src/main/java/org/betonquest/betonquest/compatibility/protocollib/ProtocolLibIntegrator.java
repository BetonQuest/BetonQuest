package org.betonquest.betonquest.compatibility.protocollib;

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

    public ProtocolLibIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() throws HookException {
        final Plugin protocolLib = Bukkit.getPluginManager().getPlugin("ProtocolLib");
        final Version protocolLibVersion = new Version(protocolLib.getDescription().getVersion());
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR, "SNAPSHOT-");
        if (comparator.isOtherNewerThanCurrent(protocolLibVersion, new Version("5.0.0-SNAPSHOT-636"))) {
            throw new UnsupportedVersionException(protocolLib, "5.0.0-SNAPSHOT-636");
        }
        // if Citizens is hooked, start NPCHider
        if (Compatibility.getHooked().contains("Citizens")) {
            NPCHider.start(BetonQuest.getInstance().getLoggerFactory().create(NPCHider.class));
            plugin.registerEvents("updatevisibility", UpdateVisibilityNowEvent.class);
        }
        if (Compatibility.getHooked().contains("MythicMobs")) {
            MythicHider.start();
        }
        plugin.registerConversationIO("menu", MenuConvIO.class);
        plugin.registerInterceptor("packet", PacketInterceptor.class);
        plugin.registerEvents("freeze", FreezeEvent.class);
    }

    @Override
    public void reload() {
        //if NPCHider is running, reload it
        if (NPCHider.getInstance() != null) {
            NPCHider.start(BetonQuest.getInstance().getLoggerFactory().create(NPCHider.class));
        }
        //if MythicHider is running, reload it
        if (MythicHider.getInstance() != null) {
            MythicHider.start();
        }
    }

    @Override
    public void close() {
        // Empty
    }
}
