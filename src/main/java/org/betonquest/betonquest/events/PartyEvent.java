package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.utils.Utils;

import java.util.List;

/**
 * Fires specified events for every player in the party
 */
@SuppressWarnings("PMD.CommentRequired")
public class PartyEvent extends QuestEvent {

    private final ConditionID[] conditions;
    private final EventID[] events;
    private final VariableNumber range;

    public PartyEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        range = instruction.getVarNum();
        conditions = instruction.getList(e -> instruction.getCondition(e)).toArray(new ConditionID[0]);
        events = instruction.getList(e -> instruction.getEvent(e)).toArray(new EventID[0]);
    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final List<OnlineProfile> members = Utils.getParty(profile.getOnlineProfile().get(), range.getDouble(profile), instruction.getPackage()
                .getQuestPath(), conditions);
        for (final OnlineProfile memberID : members) {
            for (final EventID event : events) {
                BetonQuest.event(memberID, event);
            }
        }
        return null;
    }

}
