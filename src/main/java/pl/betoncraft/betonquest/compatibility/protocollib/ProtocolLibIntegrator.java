package pl.betoncraft.betonquest.compatibility.protocollib;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.compatibility.Integrator;
import pl.betoncraft.betonquest.compatibility.protocollib.conversation.MenuConvIO;
import pl.betoncraft.betonquest.compatibility.protocollib.conversation.PacketInterceptor;
import pl.betoncraft.betonquest.compatibility.protocollib.hider.NPCHider;
import pl.betoncraft.betonquest.compatibility.protocollib.hider.UpdateVisibilityNowEvent;
import pl.betoncraft.betonquest.exceptions.HookException;
import pl.betoncraft.betonquest.exceptions.UnsupportedVersionException;

@SuppressWarnings("PMD.CommentRequired")
public class ProtocolLibIntegrator implements Integrator {

    private final BetonQuest plugin;

    public ProtocolLibIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    @SuppressWarnings({"PMD.PreserveStackTrace", "PMD.CyclomaticComplexity"})
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public void hook() throws HookException {
        final Plugin protocolLib = Bukkit.getPluginManager().getPlugin("ProtocolLib");
        final String[] qualifierParts = protocolLib.getDescription().getVersion().split("-SNAPSHOT-b");
        final String[] versionParts = qualifierParts[0].split("\\.");
        try {
            final int part1 = Integer.parseInt(versionParts[0]);
            final int part2 = Integer.parseInt(versionParts[1]);
            final int part3 = Integer.parseInt(versionParts[2]);
            final int buildNr = qualifierParts.length == 2 ? Integer.parseInt(qualifierParts[1]) : Integer.MAX_VALUE;
            if (part1 < 4
                    || part1 == 4 && part2 < 7
                    || part1 == 4 && part2 == 7 && part3 < 1
                    || part1 == 4 && part2 == 7 && part3 == 1 && buildNr < 531) {
                throw new UnsupportedVersionException(protocolLib, "4.7.1-SNAPSHOT-b531");
            }
        } catch (final NumberFormatException e) {
            throw new UnsupportedVersionException(protocolLib, "4.7.1-SNAPSHOT-b531");
        }
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
        // Empty
    }

}
