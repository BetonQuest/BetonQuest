package org.betonquest.betonquest.compatibility.protocollib;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.UnsupportedVersionException;
import org.betonquest.betonquest.compatibility.protocollib.conversation.MenuConvIOFactory;
import org.betonquest.betonquest.compatibility.protocollib.conversation.PacketInterceptorFactory;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.Server;
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

        plugin.getFeatureRegistries().conversationIO().register("menu", new MenuConvIOFactory(plugin.getMessageParser(), plugin.getFontRegistry(), plugin.getPluginConfig(), plugin.getConversationColors()));
        plugin.getFeatureRegistries().interceptor().register("packet", new PacketInterceptorFactory());

        final Server server = plugin.getServer();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
        plugin.getQuestRegistries().event().register("freeze", new FreezeEventFactory(plugin.getLoggerFactory(), data));
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        FreezeEvent.cleanup();
    }
}
