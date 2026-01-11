package org.betonquest.betonquest.quest.action.stage;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.quest.objective.stage.StageObjective;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Factory to create stage actions to modify a StageObjective.
 */
public class StageActionFactory implements PlayerActionFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Creates the stage action factory.
     *
     * @param questTypeApi the Quest Type API
     */
    public StageActionFactory(final QuestTypeApi questTypeApi) {
        this.questTypeApi = questTypeApi;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<ObjectiveIdentifier> objectiveID = instruction.identifier(ObjectiveIdentifier.class).get();
        final String operation = instruction.string().get().getValue(null);
        return switch (operation.toLowerCase(Locale.ROOT)) {
            case "set" -> createSetAction(instruction, objectiveID);
            case "increase" -> createIncreaseAction(instruction, objectiveID);
            case "decrease" -> createDecreaseAction(instruction, objectiveID);
            default -> throw new QuestException("Unknown operation '" + operation + "'");
        };
    }

    private PlayerAction createSetAction(final Instruction instruction, final Argument<ObjectiveIdentifier> objectiveID) throws QuestException {
        final Argument<String> stage = instruction.string().get();
        return new StageAction(profile -> getStageObjective(objectiveID.getValue(profile)).setStage(profile, stage.getValue(profile)));
    }

    private PlayerAction createIncreaseAction(final Instruction instruction, final Argument<ObjectiveIdentifier> objectiveID) throws QuestException {
        final Argument<Number> amount = getNumberArgument(instruction);
        return new StageAction(profile -> getStageObjective(objectiveID.getValue(profile)).increaseStage(profile, getAmount(profile, amount)));
    }

    private PlayerAction createDecreaseAction(final Instruction instruction, final Argument<ObjectiveIdentifier> objectiveID) throws QuestException {
        final Argument<Number> amount = getNumberArgument(instruction);
        return new StageAction(profile -> getStageObjective(objectiveID.getValue(profile)).decreaseStage(profile, getAmount(profile, amount)));
    }

    @Nullable
    private Argument<Number> getNumberArgument(final Instruction instruction) throws QuestException {
        if (instruction.hasNext()) {
            final String stringAmount = instruction.nextElement();
            if (!stringAmount.startsWith("conditions:")) {
                return instruction.chainForArgument(stringAmount).number().get();
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

    private StageObjective getStageObjective(final ObjectiveIdentifier objectiveID) throws QuestException {
        if (questTypeApi.getObjective(objectiveID) instanceof final StageObjective stageObjective) {
            return stageObjective;
        }
        throw new QuestException("Objective '" + objectiveID + "' is not a stage objective");
    }
}
