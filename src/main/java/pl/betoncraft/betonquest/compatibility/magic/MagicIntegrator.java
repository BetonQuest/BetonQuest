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
package pl.betoncraft.betonquest.compatibility.magic;

import com.elmakers.mine.bukkit.api.event.SpellInventoryEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;
import pl.betoncraft.betonquest.utils.PlayerConverter;


public class MagicIntegrator implements Integrator, Listener {

    private BetonQuest plugin;

    public MagicIntegrator() {
        plugin = BetonQuest.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void hook() {
        plugin.registerConditions("wand", WandCondition.class);
    }

    @Override
    public void reload() {

    }

    @Override
    public void close() {

    }

    @EventHandler
    public void onSpellInventoryEvent(SpellInventoryEvent event) {
        if (!event.isOpening()) {
            String playerID = PlayerConverter.getID(event.getMage().getPlayer());
            BetonQuest.getInstance().getPlayerData(playerID).getJournal().update();
        }
    }
}
