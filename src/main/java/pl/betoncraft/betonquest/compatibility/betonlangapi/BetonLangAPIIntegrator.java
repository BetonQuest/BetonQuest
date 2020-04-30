/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.compatibility.betonlangapi;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.betoncraft.betonlangapi.BetonLangAPI;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.utils.PlayerConverter;


public class BetonLangAPIIntegrator implements Integrator {

    private BetonQuest plugin;

    public BetonLangAPIIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        new LangChangeListener();
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateLang(player);
        }
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler(ignoreCancelled = true)
            public void onJoin(PlayerJoinEvent event) {
                updateLang(event.getPlayer());
            }
        }, BetonQuest.getInstance());
        plugin.registerEvents("language", BetonLangAPIEvent.class);
    }

    @Override
    public void reload() {

    }

    @Override
    public void close() {

    }

    /**
     * Updates the player's language to match the one specified in BetonLangAPI.
     *
     * @param player the player whose language needs to be changed
     */
    private void updateLang(Player player) {
        String lang = BetonLangAPI.getLanguage(player);
        if (Config.getLanguages().contains(lang)) {
            PlayerData data = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player));
            if (!data.getLanguage().equals(lang)) {
                data.setLanguage(lang);
            }
        }
    }

}
