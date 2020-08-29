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
package pl.betoncraft.betonquest;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.MetadataValue;
import pl.betoncraft.betonquest.id.ItemID;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.util.List;
import java.util.logging.Level;

public class CustomDropListener implements Listener {

    public CustomDropListener() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(final EntityDeathEvent event) {
        int dropIndex = 0;
        while (event.getEntity().hasMetadata("betonquest-drops-" + dropIndex)) {
            try {
                final List<MetadataValue> metadata = event.getEntity().getMetadata("betonquest-drops-" + dropIndex);
                for (final MetadataValue m : metadata) {
                    final String item = m.asString();
                    final int separatorIndex = item.indexOf(':');
                    event.getDrops().add(new QuestItem(new ItemID(null, item.substring(0, separatorIndex)))
                            .generate(Integer.valueOf(item.substring(separatorIndex + 1))));
                }
            } catch (Exception e) {
                LogUtils.getLogger().log(Level.WARNING, "Error when dropping custom item from entity: " + e.getMessage());
                LogUtils.logThrowable(e);
            }
            dropIndex++;
        }
    }

}
