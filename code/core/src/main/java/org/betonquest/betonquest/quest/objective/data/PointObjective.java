package org.betonquest.betonquest.quest.objective.data;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.PlayerObjectiveChangeEvent;
import org.betonquest.betonquest.api.bukkit.event.PlayerUpdatePointEvent;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.argument.parser.NumberParser;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.ObjectiveState;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.quest.condition.number.Operation;

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
        service.getProperties().setProperty("amount", profile -> String.valueOf(getPoints(profile)));
        service.getProperties().setProperty("left", profile -> String.valueOf(getRemainingPoints(profile)));
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
        if (operation.getValue(profile).check(count, getPoints(profile))) {
            getService().complete(profile);
        }
    }

    private int getRemainingPoints(final Profile profile) throws QuestException {
        return getPoints(profile) - playerDataStorage.getOffline(profile)
                .getPointsFromCategory(category.getValue(profile))
                .orElse(0);
    }

    private int getPoints(final Profile profile) throws QuestException {
        final String data = getService().getData().get(profile);
        return NumberParser.DEFAULT.apply(data).intValue();
    }
}
