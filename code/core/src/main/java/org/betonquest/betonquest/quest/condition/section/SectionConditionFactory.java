package org.betonquest.betonquest.quest.condition.section;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.NullableCondition;
import org.betonquest.betonquest.api.quest.condition.NullableConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.id.IdentifierUtil;
import org.betonquest.betonquest.quest.condition.logik.ConjunctionCondition;
import org.betonquest.betonquest.quest.condition.number.Operation;
import org.jetbrains.annotations.Nullable;

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
        final Argument<Operation> operation = instruction.parse(Operation::fromSymbol).get("operation", Operation.GREATER_EQUAL);
        final Argument<Number> amount = instruction.number().atLeast(1).get("amount").orElse(null);
        if (amount == null) {
            return new NullableConditionAdapter(new ConjunctionCondition(conditionIDs, conditionManager));
        }
        return new NullableConditionAdapter(new SectionCondition(conditionManager, conditionIDs, operation, amount));
    }

    private List<ConditionIdentifier> map(final ConditionIdentifier identifier) throws QuestException {
        return IdentifierUtil.subsectionIdentifiers(identifierFactory, identifier);
    }

    /**
     * Checks specified conditions against a comparison operation.
     *
     * @param conditionManager the condition manager
     * @param conditionIDs     the conditions to check
     * @param operation        the check on the actual satisfied conditions and the amount
     * @param amount           the amount which needs to be matched
     */
    private record SectionCondition(ConditionManager conditionManager, Argument<List<ConditionIdentifier>> conditionIDs,
                                    Argument<Operation> operation, Argument<Number> amount)
            implements NullableCondition {

        @Override
        public boolean check(@Nullable final Profile profile) throws QuestException {
            final List<ConditionIdentifier> conditions = conditionIDs.getValue(profile);
            final Operation operation = this.operation.getValue(profile);
            final int amount = this.amount.getValue(profile).intValue();
            return switch (operation) {
                case LESS, LESS_EQUAL -> less(profile, conditions, operation, amount);
                case EQUAL, NOT_EQUAL -> equal(profile, conditions, operation, amount);
                case GREATER, GREATER_EQUAL -> greater(profile, conditions, operation, amount);
            };
        }

        private boolean less(@Nullable final Profile profile, final List<ConditionIdentifier> conditions,
                             final Operation operation, final int amount) {
            int satisfied = 0;
            for (final ConditionIdentifier condition : conditions) {
                if (conditionManager.test(profile, condition)) {
                    satisfied++;
                    if (!operation.check(satisfied, amount)) {
                        return false;
                    }
                }
            }
            return true;
        }

        private boolean equal(@Nullable final Profile profile, final List<ConditionIdentifier> conditions,
                              final Operation operation, final int amount) {
            int satisfied = 0;
            for (final ConditionIdentifier condition : conditions) {
                if (conditionManager.test(profile, condition)) {
                    satisfied++;
                    if (satisfied > amount) {
                        break;
                    }
                }
            }
            return operation.check(satisfied, amount);
        }

        private boolean greater(@Nullable final Profile profile, final List<ConditionIdentifier> conditions,
                                final Operation operation, final int amount) {
            int satisfied = 0;
            for (final ConditionIdentifier condition : conditions) {
                if (conditionManager.test(profile, condition)) {
                    satisfied++;
                    if (operation.check(satisfied, amount)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
