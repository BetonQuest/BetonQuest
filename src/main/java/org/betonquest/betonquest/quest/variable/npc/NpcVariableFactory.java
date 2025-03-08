package org.betonquest.betonquest.quest.variable.npc;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariable;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariableAdapter;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.variable.location.LocationFormationMode;
import org.betonquest.betonquest.quest.variable.name.NpcNameVariable;

/**
 * Factory to create {@link NpcVariable}s and {@link NpcNameVariable}s from {@link Instruction}s.
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
public class NpcVariableFactory implements PlayerVariableFactory, PlayerlessVariableFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * Create a new factory to create NPC Variables.
     *
     * @param questTypeAPI the Quest Type API
     */
    public NpcVariableFactory(final QuestTypeAPI questTypeAPI) {
        this.questTypeAPI = questTypeAPI;
    }

    @Override
    public PlayerlessVariable parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullableVariableAdapter(parseInstruction(instruction));
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        return new NullableVariableAdapter(parseInstruction(instruction));
    }

    private NullableVariable parseInstruction(final Instruction instruction) throws QuestException {
        if (!instruction.hasNext() || instruction.size() == 2 && "conversation".equals(instruction.getPart(1))) {
            final NpcNameVariable npcNameVariable = new NpcNameVariable();
            return profile -> {
                if (profile == null) {
                    throw new QuestException("Profile can't be null for conversation!");
                }
                return npcNameVariable.getValue(profile);
            };
        }
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
        final NpcVariable npcVariable = new NpcVariable(questTypeAPI, npcID, key, locationFormationMode, decimalPlaces);
        return profile -> npcVariable.getValue();
    }
}
