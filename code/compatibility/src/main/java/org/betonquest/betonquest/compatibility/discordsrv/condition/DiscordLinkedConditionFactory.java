package org.betonquest.betonquest.compatibility.discordsrv.condition;

import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory to create {@link DiscordLinkedCondition}s from {@link Instruction}s.
 */
public class DiscordLinkedConditionFactory implements PlayerConditionFactory {

    /**
     * The account link manager.
     */
    private final AccountLinkManager linkManager;

    /**
     * Creates a new factory for discord linked conditions.
     *
     * @param linkManager the account link manager
     */
    public DiscordLinkedConditionFactory(final AccountLinkManager linkManager) {
        this.linkManager = linkManager;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new DiscordLinkedCondition(linkManager);
    }
}
