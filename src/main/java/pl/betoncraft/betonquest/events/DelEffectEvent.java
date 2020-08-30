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
package pl.betoncraft.betonquest.events;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.Collections;
import java.util.List;

/**
 * Removes potion effects from the player
 *
 * @author Jakub Sapalski
 */
public class DelEffectEvent extends QuestEvent {

    private final List<PotionEffectType> effects;
    private final boolean any;

    public DelEffectEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String next = instruction.next();

        if (next.equalsIgnoreCase("any")) {
            any = true;
            effects = Collections.emptyList();
        } else {
            any = false;
            effects = instruction.getList(next, type -> {
                final PotionEffectType effect = PotionEffectType.getByName(type.toUpperCase());
                if (effect == null) {
                    throw new InstructionParseException("Effect type '" + type + "' does not exist");
                } else {
                    return effect;
                }
            });
        }
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        if (any) {
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        } else {
            effects.forEach(player::removePotionEffect);
        }
        return null;
    }

}
