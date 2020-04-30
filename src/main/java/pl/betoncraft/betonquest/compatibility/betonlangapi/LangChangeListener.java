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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.betoncraft.betonlangapi.PlayerLanguageChangeEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Listens for BetonLangAPI language changes and updates player's language in
 * BetonQuest.
 *
 * @author Jakub Sapalski
 */
public class LangChangeListener implements Listener {

    public LangChangeListener() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler(ignoreCancelled = true)
    public void onLanguageChange(PlayerLanguageChangeEvent event) {
        if (Config.getLanguages().contains(event.getLanguage())) {
            BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(event.getPlayer()))
                    .setLanguage(event.getLanguage());
        }
    }

}
