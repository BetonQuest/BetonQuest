package org.betonquest.betonquest.quest.objective.data;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.PlayerObjectiveChangeEvent;
import org.betonquest.betonquest.api.bukkit.event.PlayerUpdatePointEvent;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.argument.parser.NumberParser;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.ObjectiveData;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveState;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.quest.condition.number.Operation;

import java.util.Locale;
import java.util.Optional;

/**
 * Player needs to get a certain number of points.
 */
public class PointObjective extends DefaultObjective {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * The point category.
     */
    private final Argument<String> category;

    /**
     * The amount of points required for completion.
     */
    private final Argument<Number> targetAmount;

    /**
     * Starting mode to eventually offset from the already present value at objective start.
     */
    private final Argument<CountingMode> mode;

    /**
     * The compare operand used for comparing.
     */
    private final Argument<Operation> operation;

    /**
     * Create a new objective to have specified amount of points.
     *
     * @param service           the objective factory service
     * @param playerDataStorage the storage for player data
     * @param category          the point category to check for
     * @param targetAmount      the target amount of points required for completion
     * @param mode              the starting mode to eventually offset from the already present value at start
     * @param operation         the operation to use for comparing
     * @throws QuestException if the syntax is wrong or any error happens while parsing
     */
    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public PointObjective(final ObjectiveFactoryService service, final PlayerDataStorage playerDataStorage, final Argument<String> category,
                          final Argument<Number> targetAmount, final Argument<CountingMode> mode, final Argument<Operation> operation)
            throws QuestException {
        super(service);
        this.playerDataStorage = playerDataStorage;
        this.category = category;
        this.targetAmount = targetAmount;
        this.mode = mode;
        this.operation = operation;
        getService().setDefaultData(this::getDefaultDataInstruction);
    }

    private String getDefaultDataInstruction(final Profile profile) throws QuestException {
        final long targetValue = this.targetAmount.getValue(profile).intValue();
        final CountingMode value = mode.getValue(profile);
        if (value == CountingMode.TOTAL) {
            return String.valueOf(targetValue);
        }
        final Optional<Integer> points = playerDataStorage.getOffline(profile).getPointsFromCategory(category.getValue(profile));
        return String.valueOf(targetValue + points.orElse(0));
    }

    @Override
    public String getProperty(final String name, final Profile profile) throws QuestException {
        final PointData data = new PointData(getService().getData().get(profile), profile, getObjectiveID());
        final int value = switch (name.toLowerCase(Locale.ROOT)) {
            case "amount" -> data.getPoints();
            case "left" -> data.getPoints() - playerDataStorage.getOffline(profile)
                    .getPointsFromCategory(category.getValue(profile))
                    .orElse(0);
            default -> throw new QuestException("Unknown property: " + name);
        };
        return String.valueOf(value);
    }

    /**
     * Handles point updates.
     *
     * @param event   the event to listen
     * @param profile the profile which received the update
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onPointUpdate(final PlayerUpdatePointEvent event, final Profile profile) throws QuestException {
        if (event.getCategory().equals(category.getValue(profile))) {
            checkProgress(profile, event.getNewCount());
        }
    }

    /**
     * Checks for objective completion when it is started.
     *
     * @param event   the event to listen
     * @param profile the profile which started the objective
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onStart(final PlayerObjectiveChangeEvent event, final Profile profile) throws QuestException {
        if (event.getState() != ObjectiveState.ACTIVE) {
            return;
        }
        final PlayerData playerData = playerDataStorage.get(profile);
        final Optional<Integer> points = playerData.getPointsFromCategory(category.getValue(profile));
        if (points.isPresent()) {
            checkProgress(profile, points.get());
        }
    }

    private void checkProgress(final Profile profile, final int count) throws QuestException {
        final PointData data = new PointData(getService().getData().get(profile), profile, getObjectiveID());
        if (operation.getValue(profile).check(count, data.getPoints())) {
            completeObjective(profile);
        }
    }

    /**
     * Data class for the PointObjective.
     *
     * @deprecated do not use this class. it's scheduled for removal in future versions
     */
    @Deprecated
    public static class PointData extends ObjectiveData {

        /**
         * The total required points.
         */
        private final int points;

        /**
         * Constructor for the PointData.
         *
         * @param instruction the data of the objective
         * @param profile     the profile associated with this objective
         * @param objID       the ID of the objective
         * @throws QuestException when the instruction is not a number
         */
        public PointData(final String instruction, final Profile profile, final ObjectiveID objID) throws QuestException {
            super(instruction, profile, objID);
            this.points = NumberParser.DEFAULT.apply(instruction).intValue();
        }

        private int getPoints() {
            return points;
        }
    }
}
