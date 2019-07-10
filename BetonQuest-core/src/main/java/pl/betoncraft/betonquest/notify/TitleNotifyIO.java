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

package pl.betoncraft.betonquest.notify;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.Collection;
import java.util.Map;

/**
 * Use Title Popup for Notification
 * <p>
 * Data Valuues:
 * * fadeIn: seconds to fade in
 * * stay: seconds to stay
 * * fadeOut: seconds to fade out
 * * subTitle: the subtitle to show, else blank
 */
public class TitleNotifyIO extends NotifyIO {


    // Variables

    private int fadeIn;
    private int stay;
    private int fadeOut;
    private String subTitle;


    public TitleNotifyIO(Map<String, String> data) {
        super(data);

        fadeIn = Integer.valueOf(data.getOrDefault("fadein", "10"));
        stay = Integer.valueOf(data.getOrDefault("stay", "70"));
        fadeOut = Integer.valueOf(data.getOrDefault("fadeout", "20"));
        subTitle = getData().getOrDefault("subtitle", "").replace("_", " ");
    }

    @Override
    public void sendNotify(String message, Collection<? extends Player> players) {
        for (Player player : players) {
            player.sendTitle(Utils.format(message), Utils.format(subTitle), fadeIn, stay, fadeOut);
        }

        super.sendNotify(message, players);
    }
}
