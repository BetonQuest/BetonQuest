package org.betonquest.betonquest.compatibility.protocollib;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.protocollib.conversation.MenuConvIO;
import org.betonquest.betonquest.compatibility.protocollib.conversation.PacketInterceptor;
import org.betonquest.betonquest.exceptions.HookException;
import org.betonquest.betonquest.exceptions.UnsupportedVersionException;
import org.betonquest.betonquest.modules.versioning.UpdateStrategy;
import org.betonquest.betonquest.modules.versioning.Version;
import org.betonquest.betonquest.modules.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Integrator for ProtocolLib.
 */
public class ProtocolLibIntegrator implements Integrator {
    /**
     * The BetonQuest plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * The default constructor.
     */
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

        plugin.registerConversationIO("menu", MenuConvIO.class);
        plugin.registerInterceptor("packet", PacketInterceptor.class);
        plugin.getQuestRegistries().getEventTypes().register("freeze", FreezeEvent.class);
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }
}
