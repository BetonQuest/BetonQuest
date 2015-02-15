/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
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
package pl.betoncraft.betonquest.events;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * 
 * @author Co0sh
 */
public class EffectEvent extends QuestEvent {

    private PotionEffectType effect;
    private int duration;
    private int amplifier;
    private boolean ambient = false;

    /**
     * Constructor method
     * 
     * @param playerID
     * @param instructions
     */
    public EffectEvent(String playerID, String instructions) {
        super(playerID, instructions);
        effect = PotionEffectType.getByName(instructions.split(" ")[1]);
        duration = Integer.parseInt(instructions.split(" ")[2]);
        amplifier = Integer.parseInt(instructions.split(" ")[3]);
        if (instructions.contains("--ambient")) {
            ambient = true;
        }
        PlayerConverter.getPlayer(playerID).addPotionEffect(
                new PotionEffect(effect, duration * 20, amplifier - 1, ambient));
    }

}
