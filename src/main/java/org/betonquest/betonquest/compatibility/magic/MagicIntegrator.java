package org.betonquest.betonquest.compatibility.magic;

import com.elmakers.mine.bukkit.api.event.SpellInventoryEvent;
import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.Objects;

/**
 * Integrator for the Magic plugin.
 */
public class MagicIntegrator implements Integrator, Listener {
    /**
     * The BetonQuest plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * The default constructor.
     */
    public MagicIntegrator() {
        plugin = BetonQuest.getInstance();
        profileProvider = plugin.getProfileProvider();
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final Server server = plugin.getServer();
        final PluginManager manager = server.getPluginManager();
        final MagicAPI magicApi = Objects.requireNonNull((MagicAPI) manager.getPlugin("Magic"));
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
        api.getQuestRegistries().condition().register("wand", new WandConditionFactory(api.getLoggerFactory(), magicApi, data));
        manager.registerEvents(this, plugin);
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }

    /**
     * Updates the player's journal when the spell inventory closes.
     *
     * @param event the even to listen
     */
    @EventHandler(ignoreCancelled = true)
    public void onSpellInventoryEvent(final SpellInventoryEvent event) {
        if (!event.isOpening()) {
            final OnlineProfile onlineProfile = profileProvider.getProfile(event.getMage().getPlayer());
            plugin.getPlayerDataStorage().get(onlineProfile).getJournal().update();
        }
    }
}
