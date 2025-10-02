package org.betonquest.betonquest.compatibility.magic;

import com.elmakers.mine.bukkit.api.event.SpellInventoryEvent;
import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.Objects;

/**
 * Integrator for the Magic plugin.
 */
public class MagicIntegrator implements Integrator, Listener {

    /**
     * The default constructor.
     */
    public MagicIntegrator() {
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final BetonQuest plugin = BetonQuest.getInstance();
        final PluginManager manager = plugin.getServer().getPluginManager();
        final MagicAPI magicApi = Objects.requireNonNull((MagicAPI) manager.getPlugin("Magic"));
        final PrimaryServerThreadData data = api.getPrimaryServerThreadData();
        api.getQuestRegistries().condition().register("wand", new WandConditionFactory(api.getLoggerFactory(), magicApi, data));
        manager.registerEvents(new InventoryListener(plugin.getPlayerDataStorage(), api.getProfileProvider()), plugin);
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
     * Updates quest status on Magic interaction.
     *
     * @param playerDataStorage The BetonQuest plugin instance.
     * @param profileProvider   The profile provider instance.
     */
    private record InventoryListener(
            PlayerDataStorage playerDataStorage,
            ProfileProvider profileProvider
    ) implements Listener {

        /**
         * Updates the player's journal when the spell inventory closes.
         *
         * @param event the even to listen
         */
        @EventHandler(ignoreCancelled = true)
        public void onSpellInventoryEvent(final SpellInventoryEvent event) {
            if (!event.isOpening() && event.getMage().getPlayer() != null) {
                final OnlineProfile onlineProfile = profileProvider.getProfile(event.getMage().getPlayer());
                playerDataStorage.get(onlineProfile).getJournal().update();
            }
        }
    }
}
