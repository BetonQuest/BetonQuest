package org.betonquest.betonquest.compatibility.magic;

import com.elmakers.mine.bukkit.api.event.SpellInventoryEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Integrator for the Magic plugin.
 */
public class MagicIntegrator implements Integrator, Listener {
    /**
     * The BetonQuest plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * The default constructor.
     */
    public MagicIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.getQuestRegistries().getConditionTypes().register("wand", WandCondition.class);
        Bukkit.getPluginManager().registerEvents(this, plugin);
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
            final OnlineProfile onlineProfile = PlayerConverter.getID(event.getMage().getPlayer());
            plugin.getPlayerData(onlineProfile).getJournal().update();
        }
    }
}
