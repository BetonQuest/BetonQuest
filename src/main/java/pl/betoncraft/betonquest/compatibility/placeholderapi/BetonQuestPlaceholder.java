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
package pl.betoncraft.betonquest.compatibility.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class BetonQuestPlaceholder extends PlaceholderExpansion {

    /**
     * Persist through reloads
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * We can always register
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * Name of person who created the expansion
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor() {
        return BetonQuest.getInstance().getDescription().getAuthors().toString();
    }

    /**
     * The identifier for PlaceHolderAPI to link to this expansion
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier() {
        return "betonquest";
    }

    /**
     * Version of the expansion
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion() {
        return BetonQuest.getInstance().getDescription().getVersion();
    }

    /**
     * A placeholder request has occurred and needs a value
     *
     * @param player     A {@link org.bukkit.entity.Player Player}.
     * @param identifier A String containing the identifier/value.
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        String pack;
        if (identifier.contains(":")) {
            pack = identifier.substring(0, identifier.indexOf(':'));
            identifier = identifier.substring(identifier.indexOf(':') + 1);
        } else {
            pack = Config.getDefaultPackage().getName();
        }
        return BetonQuest.getInstance().getVariableValue(pack, '%' + identifier + '%', PlayerConverter.getID(player));
    }

}
