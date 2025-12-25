package org.betonquest.betonquest.quest.event.stage;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
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
        final Argument<ObjectiveID> objectiveID = instruction.parse(ObjectiveID::new).get();
        final String action = instruction.string().get().getValue(null);
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "set" -> createSetEvent(instruction, objectiveID);
            case "increase" -> createIncreaseEvent(instruction, objectiveID);
            case "decrease" -> createDecreaseEvent(instruction, objectiveID);
            default -> throw new QuestException("Unknown action '" + action + "'");
        };
    }

    private PlayerEvent createSetEvent(final Instruction instruction, final Argument<ObjectiveID> objectiveID) throws QuestException {
        final Argument<String> variableString = instruction.string().get();
        return new StageEvent(profile -> getStageObjective(objectiveID.getValue(profile)).setStage(profile, variableString.getValue(profile)));
    }

    private PlayerEvent createIncreaseEvent(final Instruction instruction, final Argument<ObjectiveID> objectiveID) throws QuestException {
        final Argument<Number> amount = getVariableNumber(instruction);
        return new StageEvent(profile -> getStageObjective(objectiveID.getValue(profile)).increaseStage(profile, getAmount(profile, amount)));
    }

    private PlayerEvent createDecreaseEvent(final Instruction instruction, final Argument<ObjectiveID> objectiveID) throws QuestException {
        final Argument<Number> amount = getVariableNumber(instruction);
        return new StageEvent(profile -> getStageObjective(objectiveID.getValue(profile)).decreaseStage(profile, getAmount(profile, amount)));
    }

    @Nullable
    private Argument<Number> getVariableNumber(final Instruction instruction) throws QuestException {
        if (instruction.hasNext()) {
            final String stringAmount = instruction.nextElement();
            if (!stringAmount.startsWith("conditions:")) {
                return instruction.get(stringAmount, instruction.getParsers().number());
            }
        }
        return null;
    }

    private int getAmount(final Profile profile, @Nullable final Argument<Number> amount) throws QuestException {
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
