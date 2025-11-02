package org.betonquest.betonquest.quest.event.stage;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.quest.objective.stage.StageObjective;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Factory to create stage events to modify a StageObjective.
 */
public class StageEventFactory implements PlayerEventFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Creates the stage event factory.
     *
     * @param questTypeApi the Quest Type API
     */
    public StageEventFactory(final QuestTypeApi questTypeApi) {
        this.questTypeApi = questTypeApi;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<ObjectiveID> objectiveID = instruction.get(ObjectiveID::new);
        final String action = instruction.get(Argument.STRING).getValue(null);
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "set" -> createSetEvent(instruction, objectiveID);
            case "increase" -> createIncreaseEvent(instruction, objectiveID);
            case "decrease" -> createDecreaseEvent(instruction, objectiveID);
            default -> throw new QuestException("Unknown action '" + action + "'");
        };
    }

    private PlayerEvent createSetEvent(final Instruction instruction, final Variable<ObjectiveID> objectiveID) throws QuestException {
        final Variable<String> variableString = instruction.get(Argument.STRING);
        return new StageEvent(profile -> getStageObjective(objectiveID.getValue(profile)).setStage(profile, variableString.getValue(profile)));
    }

    private PlayerEvent createIncreaseEvent(final Instruction instruction, final Variable<ObjectiveID> objectiveID) throws QuestException {
        final Variable<Number> amount = getVariableNumber(instruction);
        return new StageEvent(profile -> getStageObjective(objectiveID.getValue(profile)).increaseStage(profile, getAmount(profile, amount)));
    }

    private PlayerEvent createDecreaseEvent(final Instruction instruction, final Variable<ObjectiveID> objectiveID) throws QuestException {
        final Variable<Number> amount = getVariableNumber(instruction);
        return new StageEvent(profile -> getStageObjective(objectiveID.getValue(profile)).decreaseStage(profile, getAmount(profile, amount)));
    }

    @Nullable
    private Variable<Number> getVariableNumber(final Instruction instruction) throws QuestException {
        if (instruction.hasNext()) {
            final String stringAmount = instruction.next();
            if (!stringAmount.matches("condition(s)?:.+")) {
                return instruction.get(stringAmount, Argument.NUMBER);
            }
        }
        return null;
    }

    private int getAmount(final Profile profile, @Nullable final Variable<Number> amount) throws QuestException {
        if (amount == null) {
            return 1;
        }
        final int targetAmount = amount.getValue(profile).intValue();
        if (targetAmount <= 0) {
            throw new QuestException("Amount must be greater than 0");
        }
        return targetAmount;
    }

    private StageObjective getStageObjective(final ObjectiveID objectiveID) throws QuestException {
        if (questTypeApi.getObjective(objectiveID) instanceof final StageObjective stageObjective) {
            return stageObjective;
        }
        throw new QuestException("Objective '" + objectiveID + "' is not a stage objective");
    }
}
