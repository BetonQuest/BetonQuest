package org.betonquest.betonquest.compatibility.magic;

import com.elmakers.mine.bukkit.api.event.SpellInventoryEvent;
import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

/**
 * Integrator for the Magic plugin.
 */
public class MagicIntegrator implements Integration {

    /**
     * Creates a new Integrator.
     */
    public MagicIntegrator() {
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final MagicAPI magicApi = Objects.requireNonNull((MagicAPI) Bukkit.getPluginManager().getPlugin("Magic"));
        api.conditions().registry().register("wand", new WandConditionFactory(magicApi));
        api.bukkit().registerEvents(new InventoryListener(BetonQuest.getInstance().getPlayerDataStorage(), api.profiles()));
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }

    /**
     * Updates quest status on Magic interaction.
     *
     * @param playerDataStorage The BetonQuest plugin instance.
     * @param profileProvider   The profile provider instance.
     */
    public record InventoryListener(PlayerDataStorage playerDataStorage, ProfileProvider profileProvider)
            implements Listener {

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
