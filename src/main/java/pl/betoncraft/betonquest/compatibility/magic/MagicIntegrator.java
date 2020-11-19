package pl.betoncraft.betonquest.compatibility.magic;

import com.elmakers.mine.bukkit.api.event.SpellInventoryEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;
import pl.betoncraft.betonquest.utils.PlayerConverter;


public class MagicIntegrator implements Integrator, Listener {

    private final BetonQuest plugin;

    public MagicIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.registerConditions("wand", WandCondition.class);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void reload() {

    }

    @Override
    public void close() {

    }

    @EventHandler(ignoreCancelled = true)
    public void onSpellInventoryEvent(final SpellInventoryEvent event) {
        if (!event.isOpening()) {
            final String playerID = PlayerConverter.getID(event.getMage().getPlayer());
            BetonQuest.getInstance().getPlayerData(playerID).getJournal().update();
        }
    }
}
