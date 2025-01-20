package org.betonquest.betonquest.compatibility.citizens.variable.npc;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.variable.location.LocationFormationMode;

/**
 * Factory to create {@link CitizensVariable}s from {@link Instruction}s.
 * <p>
 * Format:
 * {@code %citizen.<id>.<argument>.<mode>.<precision>%}
 * <p>
 * Arguments:<br>
 * * name - Return citizen name<br>
 * * full_name - Full Citizen name<br>
 * * location - Return citizen location, defaults to ulfLong<br>
 * Modes: refer to LocationVariable documentation for details.<br>
 */
public class CitizensVariableFactory implements PlayerlessVariableFactory {

    /**
     * Create a new factory to create Citizens Variables.
     */
    public CitizensVariableFactory() {
    }

    @Override
    public PlayerlessVariable parsePlayerless(final Instruction instruction) throws QuestException {
        final int npcId = instruction.getInt();
        if (npcId < 0) {
            throw new QuestException("NPC ID cannot be less than 0");
        }
        final Argument key = instruction.getEnum(Argument.class);
        LocationFormationMode locationFormationMode = null;
        int decimalPlaces = 0;
        if (key == Argument.LOCATION) {
            if (instruction.hasNext()) {
                locationFormationMode = LocationFormationMode.getMode(instruction.next());
            } else {
                locationFormationMode = LocationFormationMode.ULF_LONG;
            }
            if (instruction.hasNext()) {
                decimalPlaces = Integer.parseInt(instruction.next());
            }
        }
        return new CitizensVariable(npcId, key, locationFormationMode, decimalPlaces);
    }
}
