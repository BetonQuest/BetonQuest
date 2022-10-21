package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import lombok.CustomLog;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.holograms.HologramSubIntegrator;
import org.betonquest.betonquest.exceptions.HookException;
import org.bukkit.Bukkit;

@CustomLog
public class HolographicDisplaysIntegrator extends HologramSubIntegrator {
    public HolographicDisplaysIntegrator() {
        super("HolographicDisplays", HolographicDisplaysHologram.class, "3.0.0-SNAPSHOT-b000", "SNAPSHOT-b");
    }

    @Override
    protected void init() throws HookException {
        super.init(); //Calling super validates version
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            LOG.warn("Holograms from HolographicDisplays won't be able to hide from players without ProtocolLib plugin! "
                    + "Install it to use conditioned holograms.");
        }
        final HolographicDisplaysAPI api = HolographicDisplaysAPI.get(BetonQuest.getInstance());
        api.registerIndividualPlaceholder("bq", new HologramPlaceholder());
        api.registerGlobalPlaceholder("bqg", new HologramGlobalPlaceholder());
    }
}
