package org.betonquest.betonquest.quest.objective.damage;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.argument.parser.NumberParser;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveProperties;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.lib.argument.type.TimeUnit;
import org.betonquest.betonquest.lib.profile.ProfileKeyMap;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;
import java.util.Map;

/**
 * The player must deal or take a specific amount of damage.
 */
public class DamageObjective extends DefaultObjective {

    /**
     * The target amount of damage to be received or dealt.
     */
    private final Argument<Number> targetAmount;

    /**
     * The action specifying whether the player deals or takes damage.
     */
    private final Argument<DamageAction> action;

    /**
     * The list of allowed damage types or causes.
     */
    private final Argument<List<EntityDamageEvent.DamageCause>> type;

    /**
     * The minimum amount of damage required per event.
     */
    private final Argument<Number> minAmount;

    /**
     * The minimum time interval in milliseconds between valid damage events.
     */
    private final Argument<Number> interval;

    /**
     * The unit of time used to convert the interval duration.
     */
    private final Argument<TimeUnit> timeUnit;

    /**
     * Stores the last damage event timestamp in milliseconds for each profile.
     */
    private final Map<Profile, Long> intervalTimes;

    /**
     * Constructs a new {@code DamageObjective}.
     *
     * @param service      the objective service
     * @param targetAmount the target amount of damage required
     * @param action       the action specifying whether the player deals or takes damage
     * @param type         the list of damage types or causes allowed
     * @param minAmount    the minimum amount of damage required per event
     * @param interval     the time interval in milliseconds between valid damage events
     * @param timeUnit     the unit of time used to convert the interval duration
     * @throws QuestException if the instruction is invalid
     */
    public DamageObjective(final ObjectiveService service, final Argument<Number> targetAmount,
                           final Argument<DamageAction> action, final Argument<List<EntityDamageEvent.DamageCause>> type,
                           final Argument<Number> minAmount, final Argument<Number> interval,
                           final Argument<TimeUnit> timeUnit) throws QuestException {
        super(service);
        this.targetAmount = targetAmount;
        this.action = action;
        this.type = type;
        this.minAmount = minAmount;
        this.interval = interval;
        this.timeUnit = timeUnit;
        this.intervalTimes = new ProfileKeyMap<>(service.getProfileProvider());

        service.setDefaultData(this::getDefaultDataInstruction);
        final ObjectiveProperties properties = service.getProperties();
        properties.setProperty("amount", profile -> String.valueOf(getDamageAmount(profile).current));
        properties.setProperty("left", profile -> String.valueOf(getRemainingDamage(profile)));
        properties.setProperty("total", profile -> String.valueOf(getDamageAmount(profile).target));
    }

    /**
     * Handles when a player deals damage and updates objective progress.
     *
     * @param event         the Bukkit entity damage event
     * @param onlineProfile the profile of the player dealing damage
     * @throws QuestException if evaluating event arguments fails
     */
    public void onDamageDealt(final EntityDamageEvent event, final OnlineProfile onlineProfile) throws QuestException {
        processDamageEvent(event, onlineProfile, DamageAction.DEAL);
    }

    /**
     * Handles when a player takes damage and updates objective progress.
     *
     * @param event         the Bukkit entity damage event
     * @param onlineProfile the profile of the player taking damage
     * @throws QuestException if evaluating event arguments fails
     */
    public void onDamageTaken(final EntityDamageEvent event, final OnlineProfile onlineProfile) throws QuestException {
        processDamageEvent(event, onlineProfile, DamageAction.TAKE);
    }

    private void processDamageEvent(final EntityDamageEvent event, final OnlineProfile profile, final DamageAction expectedAction)
            throws QuestException {

        final DamageAction action = this.action.getValue(profile);
        if (action != expectedAction && action != DamageAction.BOTH) {
            return;
        }

        final double finalDmg = event.getFinalDamage();
        if (checkIsInvalidType(profile, event.getCause())
                || checkIsInvalidMin(profile, finalDmg)
                || checkIsOnCooldown(profile)) {
            return;
        }

        add(profile, finalDmg);

        if (isCompleted(profile)) {
            getService().complete(profile);
        }
    }

    private boolean checkIsInvalidType(final OnlineProfile profile, final EntityDamageEvent.DamageCause currentCause)
            throws QuestException {
        final List<EntityDamageEvent.DamageCause> allowedTypes = type.getValue(profile);
        return !allowedTypes.contains(currentCause);
    }

    private boolean checkIsInvalidMin(final OnlineProfile profile, final double damage) throws QuestException {
        final double min = minAmount.getValue(profile).doubleValue();
        return damage < min;
    }

    private boolean checkIsOnCooldown(final OnlineProfile profile) throws QuestException {
        final Long expirationTimeMillis = intervalTimes.get(profile);
        if (expirationTimeMillis != null && System.currentTimeMillis() < expirationTimeMillis) {
            return true;
        }

        final long rawAmount = interval.getValue(profile).longValue();
        final TimeUnit unit = timeUnit.getValue(profile);
        final long requiredIntervalMillis = unit.getTicks(rawAmount) * 50;

        if (requiredIntervalMillis <= 0) {
            return false;
        }

        final long currentTimeMillis = System.currentTimeMillis();
        intervalTimes.put(profile, currentTimeMillis + requiredIntervalMillis);
        return false;
    }

    private String getDefaultDataInstruction(final Profile profile) throws QuestException {
        return String.valueOf(targetAmount.getValue(profile).doubleValue());
    }

    private double getRemainingDamage(final Profile profile) throws QuestException {
        final Amount amount = getDamageAmount(profile);
        return amount.target - amount.current;
    }

    private boolean isCompleted(final Profile profile) throws QuestException {
        final Amount amount = getDamageAmount(profile);
        return amount.current >= amount.target;
    }

    private void add(final Profile profile, final double toAdd) throws QuestException {
        final Amount amount = getDamageAmount(profile);
        final double newAmount = amount.current + toAdd;
        getService().getData().put(profile, newAmount + "/" + amount.target);
        getService().updateData(profile);
    }

    private Amount getDamageAmount(final Profile profile) throws QuestException {
        final String stringData = getService().getData().get(profile);
        if (stringData == null) {
            throw new QuestException("Profile should have data!");
        }
        final String[] split = stringData.split("/");
        final double amount;
        final double targetAmount;
        final int initLength = 1;
        if (split.length == initLength) {
            amount = 0;
            targetAmount = NumberParser.DEFAULT.apply(split[0]).doubleValue();
        } else {
            amount = NumberParser.DEFAULT.apply(split[0]).doubleValue();
            targetAmount = NumberParser.DEFAULT.apply(split[1]).doubleValue();
        }
        return new Amount(amount, targetAmount);
    }

    /**
     * Represents a numerical progress tracking structure containing the current value
     * and the target objective value.
     *
     * @param current the current progress value achieved so far
     * @param target  the final target value required to complete the objective
     */
    private record Amount(double current, double target) {

    }
}
