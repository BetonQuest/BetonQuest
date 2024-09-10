package org.betonquest.betonquest.quest.variable.npc;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.registry.processor.NpcProcessor;
import org.betonquest.betonquest.quest.variable.location.LocationFormationMode;

/**
 * Factory to create {@link NpcVariable}s from {@link Instruction}s.
 * <p>
 * Format:
 * {@code %<variableName>.<id>.<argument>.<mode>.<precision>%}
 * <p>
 * Arguments:<br>
 * * name - Return npc name<br>
 * * full_name - Full npc name<br>
 * * location - Return npc location, defaults to ulfLong<br>
 * Modes: refer to LocationVariable documentation for details.<br>
 *
 * @see org.betonquest.betonquest.quest.variable.location.LocationVariable
 */
public class NpcVariableFactory implements PlayerlessVariableFactory {

    /**
     * Providing a new NPC Adapter from an id.
     */
    private final NpcProcessor npcProcessor;

    /**
     * Create a new factory to create NPC Variables.
     *
     * @param npcProcessor the supplier providing the npc adapter
     */
    public NpcVariableFactory(final NpcProcessor npcProcessor) {
        this.npcProcessor = npcProcessor;
    }

    @Override
    public PlayerlessVariable parsePlayerless(final Instruction instruction) throws QuestException {
        final NpcID npcID = instruction.getID(NpcID::new);
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
        return new NpcVariable(npcProcessor, npcID, key, locationFormationMode, decimalPlaces);
    }
}
