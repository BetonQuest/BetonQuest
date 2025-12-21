package org.betonquest.betonquest.quest.objective.data;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.PlayerObjectiveChangeEvent;
import org.betonquest.betonquest.api.bukkit.event.PlayerUpdatePointEvent;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.ObjectiveData;
import org.betonquest.betonquest.api.quest.objective.ObjectiveDataFactory;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveState;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.quest.condition.number.Operation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Player needs to get a certain amount of points.
 */
public class PointObjective extends Objective implements Listener {

    /**
     * The Factory for the Point Data.
     */
    private static final ObjectiveDataFactory POINT_FACTORY = PointData::new;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * The point category.
     */
    private final Variable<String> category;

    /**
     * The amount of points required for completion.
     */
    private final Variable<Number> targetAmount;

    /**
     * Starting mode to eventually offset from the already present value at objective start.
     */
    private final Variable<CountingMode> mode;

    /**
     * The compare operand used for comparing.
     */
    private final Variable<Operation> operation;

    /**
     * Create a new objective to have specified amount of points.
     *
     * @param instruction       Instruction object representing the objective
     * @param playerDataStorage the storage for player data
     * @param category          the point category to check for
     * @param targetAmount      the target amount of points required for completion
     * @param mode              the starting mode to eventually offset from the already present value at start
     * @param operation         the operation to use for comparing
     * @throws QuestException if the syntax is wrong or any error happens while parsing
     */
    public PointObjective(final Instruction instruction, final PlayerDataStorage playerDataStorage, final Variable<String> category,
                          final Variable<Number> targetAmount, final Variable<CountingMode> mode, final Variable<Operation> operation)
            throws QuestException {
        super(instruction, POINT_FACTORY);
        this.playerDataStorage = playerDataStorage;
        this.category = category;
        this.targetAmount = targetAmount;
        this.mode = mode;
        this.operation = operation;
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) throws QuestException {
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
        final PointData data = (PointData) dataMap.get(profile);
        if (data == null) {
            return "";
        }
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
     * @param event the event to listen
     */
    @EventHandler
    public void onPointUpdate(final PlayerUpdatePointEvent event) {
        if (!containsPlayer(event.getProfile())) {
            return;
        }
        qeHandler.handle(() -> {
            if (event.getCategory().equals(category.getValue(event.getProfile()))) {
                checkProgress(event.getProfile(), event.getNewCount());
            }
        });
    }

    /**
     * Checks for objective completion when it is started.
     *
     * @param event the event to listen
     */
    @EventHandler
    public void onStart(final PlayerObjectiveChangeEvent event) {
        if (event.getState() != ObjectiveState.ACTIVE || !containsPlayer(event.getProfile())) {
            return;
        }
        qeHandler.handle(() -> {
            final PlayerData playerData = playerDataStorage.get(event.getProfile());
            final Optional<Integer> points = playerData.getPointsFromCategory(category.getValue(event.getProfile()));
            if (points.isPresent()) {
                checkProgress(event.getProfile(), points.get());
            }
        });
    }

    private void checkProgress(final Profile profile, final int count) throws QuestException {
        final PointData data = Objects.requireNonNull((PointData) dataMap.get(profile));
        if (operation.getValue(profile).check(count, data.getPoints()) && checkConditions(profile)) {
            completeObjective(profile);
        }
    }

    /**
     * Data class for the PointObjective.
     */
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
            this.points = DefaultArgumentParsers.NUMBER.apply(instruction).intValue();
        }

        private int getPoints() {
            return points;
        }
    }
}
