package org.betonquest.betonquest.quest.condition.section;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.NullableCondition;
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
        return new NullableConditionAdapter(parseInstruction(instruction));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parseInstruction(instruction));
    }

    private NullableCondition parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<List<ConditionIdentifier>> identifiers = instruction.identifier(ConditionIdentifier.class).map(this::map).get();
        final Argument<Number> min = instruction.number().atLeast(0).get("min").orElse(null);
        final Argument<Number> max = instruction.number().atLeast(0).get("max").orElse(null);
        if (min != null && max != null) {
            return new SectionCondition(conditionManager, identifiers, min, max);
        }
        if (min != null) {
            return new SectionCondition(conditionManager, identifiers, min, profile -> Integer.MAX_VALUE);
        }
        if (max != null) {
            return new SectionCondition(conditionManager, identifiers, profile -> 0, max);
        }
        return new ConjunctionCondition(identifiers, conditionManager);
    }

    private List<ConditionIdentifier> map(final ConditionIdentifier identifier) throws QuestException {
        return IdentifierUtil.subsectionIdentifiers(identifierFactory, identifier);
    }
}
