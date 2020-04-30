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
package pl.betoncraft.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.api.events.HeroKillCharacterEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.MobKillNotifier;

/**
 * Handles kills done by Heroes plugin and passes them to MobKillNotifier.
 *
 * @author Jakub Sapalski
 */
public class HeroesMobKillListener implements Listener {

    public HeroesMobKillListener() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler(ignoreCancelled = true)
    public void onHeroesKill(HeroKillCharacterEvent event) {
        MobKillNotifier.addKill(event.getAttacker().getPlayer(), event.getDefender().getEntity());
    }

}
