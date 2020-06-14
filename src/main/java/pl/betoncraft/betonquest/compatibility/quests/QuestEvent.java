/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016 Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.compatibility.quests;

import java.util.logging.Level;

import me.blackvein.quests.Quest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;


/**
 * Starts a quests in Quests plugin.
 *
 * @author Jakub Sapalski
 */
public class QuestEvent extends pl.betoncraft.betonquest.api.QuestEvent {

	private final String	questName;
	private final boolean	override;

	public QuestEvent(final Instruction instruction) throws InstructionParseException {
		super(instruction, true);
		questName = instruction.next();
		override = instruction.hasArgument("check-requirements");
	}

	@Override
	protected Void execute(final String playerID) {
		Quest quest = null;
		for(final Quest q : QuestsIntegrator.getQuestsInstance().getQuests()) {
			if(q.getName().replace(' ', '_').equalsIgnoreCase(questName)) {
				quest = q;
				break;
			}
		}
		if(quest == null) {
			LogUtils.getLogger().log(Level.WARNING, "Quest '" + questName + "' is not defined");
			return null;
		}
		QuestsIntegrator.getQuestsInstance().getQuester(PlayerConverter.getName(playerID)).takeQuest(quest, override);
		return null;
	}

}
