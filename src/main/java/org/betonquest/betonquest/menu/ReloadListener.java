package org.betonquest.betonquest.menu;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.LoadDataEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created on 11.03.2018
 *
 * @author Jonas Blocher
 */
public class ReloadListener implements Listener {

    public ReloadListener() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler
    public void onReload(final LoadDataEvent event) {
        RPGMenu.getInstance().reloadData();
    }
}
