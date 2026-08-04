package org.betonquest.betonquest.quest.condition.section;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.NullableConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.id.IdentifierUtil;
import org.betonquest.betonquest.quest.condition.logik.ConjunctionCondition;

import java.util.List;

/**
 * Factory to create section grouped conditions from {@link Instruction}s.
 */
public class SectionConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * The condition manager.
     */
    private final ConditionManager conditionManager;

    /**
     * Factory to create identifier from section keys.
     */
    private final IdentifierFactory<ConditionIdentifier> identifierFactory;

    /**
     * Create the section condition factory.
     *
     * @param conditionManager  the condition manager
     * @param identifierFactory the factory to create identifier from section keys
     */
    public SectionConditionFactory(final ConditionManager conditionManager, final IdentifierFactory<ConditionIdentifier> identifierFactory) {
        this.conditionManager = conditionManager;
        this.identifierFactory = identifierFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    private NullableConditionAdapter parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<List<ConditionIdentifier>> conditionIDs = instruction.identifier(ConditionIdentifier.class).map(this::map).get();
        return new NullableConditionAdapter(new ConjunctionCondition(conditionIDs, conditionManager));
    }

    private List<ConditionIdentifier> map(final ConditionIdentifier identifier) throws QuestException {
        return IdentifierUtil.subsectionIdentifiers(identifierFactory, identifier);
    }
}
