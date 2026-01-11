package org.betonquest.betonquest.quest.action.run;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;

import java.util.Collections;
import java.util.List;

/**
 * Create new {@link RunForAllAction} from instruction.
 */
public class RunForAllActionFactory implements PlayerlessActionFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Create new {@link RunForAllActionFactory}.
     *
     * @param questTypeApi    the Quest Type API
     * @param profileProvider the profile provider instance
     */
    public RunForAllActionFactory(final QuestTypeApi questTypeApi, final ProfileProvider profileProvider) {
        this.questTypeApi = questTypeApi;
        this.profileProvider = profileProvider;
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        final Argument<List<ActionIdentifier>> actions = instruction.identifier(ActionIdentifier.class)
                .list().get("actions", Collections.emptyList());
        final Argument<List<ConditionIdentifier>> conditions = instruction.identifier(ConditionIdentifier.class)
                .list().get("where", Collections.emptyList());
        return new RunForAllAction(profileProvider::getOnlineProfiles, questTypeApi, actions, conditions);
    }
}
