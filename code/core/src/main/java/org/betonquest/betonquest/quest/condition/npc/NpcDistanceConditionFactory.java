package org.betonquest.betonquest.quest.condition.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.service.npc.NpcManager;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArguments;

/**
 * Factory to create {@link NpcLocationCondition}s with a player location from {@link Instruction}s.
 */
public class NpcDistanceConditionFactory implements PlayerConditionFactory {

    /**
     * The npc manager.
     */
    private final NpcManager npcManager;

    /**
     * Create a new factory for NPC Distance Conditions.
     *
     * @param npcManager the npc manager
     */
    public NpcDistanceConditionFactory(final NpcManager npcManager) {
        this.npcManager = npcManager;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<NpcIdentifier> npcId = instruction.identifier(NpcIdentifier.class).get();
        final Argument<Number> distance = instruction.number().get();
        final NpcLocationCondition condition = new NpcLocationCondition(npcManager, npcId, DefaultArguments.PLAYER_LOCATION, distance);
        return new OnlineConditionAdapter(condition::check);
    }
}
