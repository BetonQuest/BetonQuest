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

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.util.logging.Level;

/**
 * Gives journal to the player.
 *
 * @author Jakub Sapalski
 */
public class GiveJournalEvent extends QuestEvent {

    private int journalSlot;

    public GiveJournalEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        try {
            journalSlot = Integer.parseInt(Config.getString("config.default_journal_slot"));
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, "Could not read default_journal_slot: " + e.getMessage());
            LogUtils.logThrowable(e);
            journalSlot = -1;
        }
    }

    @Override
    public void run(String playerID) {
        BetonQuest.getInstance().getPlayerData(playerID).getJournal().addToInv(journalSlot);
    }

}
