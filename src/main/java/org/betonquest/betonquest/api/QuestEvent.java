package org.betonquest.betonquest.api;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.instruction.Instruction;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * Superclass for all events. You need to extend it to create new custom events.
 * </p>
 * <p>
 * Registering your events is done using the
 * {@link BetonQuest#registerEvent(String, EventFactory, StaticEventFactory) registerEvent()} method.
 * </p>
 */
public abstract class QuestEvent extends ForceSyncHandler<Void> {
    /**
     * Stores the user-provided instruction for this event.
     */
    protected final Instruction instruction;

    /**
     * Stores conditions that must be met when firing this event.
     */
    protected final ConditionID[] conditions;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Whether the event is static.
     */
    protected boolean staticness;

    /**
     * Whether the event is persistent.
     */
    protected boolean persistent;

    /**
     * Creates a new instance of the event. This constructor must parse the user-provided {@link Instruction}
     * by extracting relevant user input as object variables. These will be used later on
     * when the event is called in {@link #execute(Profile)}.
     * If anything goes wrong, throw an {@link QuestException} with an error message that helps the user fix
     * the event.
     *
     * @param instruction the {@link Instruction} object for this event; you need to
     *                    extract all required data from it and throw an
     *                    {@link QuestException} if there is anything wrong
     * @param forceSync   If set to true, this executes the event on the servers main thread.
     *                    Otherwise, it will keep running on the current thread (which could also be the main thread!).
     * @throws QuestException when there is an error during syntax or argument parsing
     */
    public QuestEvent(final Instruction instruction, final boolean forceSync) throws QuestException {
        super(forceSync);
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());
        this.instruction = instruction;
        final String[] tempConditions1 = instruction.getArray(instruction.getOptional("condition"));
        final String[] tempConditions2 = instruction.getArray(instruction.getOptional("conditions"));
        final int length = tempConditions1.length + tempConditions2.length;
        conditions = new ConditionID[length];
        for (int i = 0; i < length; i++) {
            final String condition = i >= tempConditions1.length ? tempConditions2[i - tempConditions1.length] : tempConditions1[i];
            try {
                conditions[i] = new ConditionID(instruction.getPackage(), condition);
            } catch (final ObjectNotFoundException exception) {
                throw new QuestException("Error while parsing event conditions: " + exception.getMessage(), exception);
            }
        }
    }

    /**
     * This method is called by BetonQuest to execute the event. Therefore, it must contain all logic for firing the event.
     * Use the data parsed in the event's constructor to fire the event based on the user's settings.
     *
     * @param profile the {@link Profile} of the player for whom the event will be executed
     * @throws QuestException when there is an error while running the event (for example, a
     *                        numeric variable resolved to a string)
     */
    @Override
    protected abstract Void execute(Profile profile) throws QuestException;

    /**
     * Returns the full id of this event. This includes the package path and the event name, separated by a dot.
     *
     * @return the full id of this event
     */
    protected String getFullId() {
        return instruction.getID().getFullID();
    }

    /**
     * Fires an event for the profile if it meets the event's conditions.
     * If the profile is null, the event will be fired as a static event.
     *
     * @param profile the {@link Profile} of the player for whom the event will fire
     * @return whether the event was successfully handled or not.
     * @throws QuestException passes the exception from the event up the stack
     */
    public final boolean fire(@Nullable final Profile profile) throws QuestException {
        if (profile == null) {
            return handleNullProfile();
        } else if (profile.getOnlineProfile().isEmpty()) {
            return handleOfflineProfile(profile);
        } else {
            return handleOnlineProfile(profile);
        }
    }

    private boolean handleNullProfile() throws QuestException {
        if (!staticness) {
            log.warn(instruction.getPackage(),
                    "Cannot fire non-static event '" + instruction.getID() + "' without a player!");
            return false;
        }
        log.debug(instruction.getPackage(), "Static event will be fired without a profile.");
        if (!BetonQuest.conditions(null, conditions)) {
            log.debug(instruction.getPackage(), "Event conditions were not met");
            return false;
        }
        handle(null);
        return true;
    }

    private boolean handleOfflineProfile(final Profile profile) throws QuestException {
        if (persistent) {
            log.debug(instruction.getPackage(), "Persistent event will be fired for offline profile.");
            handle(profile);
            return true;
        } else {
            log.debug(instruction.getPackage(), profile + " is offline, cannot fire event because it's not persistent.");
            return false;
        }
    }

    private boolean handleOnlineProfile(final Profile profile) throws QuestException {
        if (!BetonQuest.conditions(profile, conditions)) {
            log.debug(instruction.getPackage(), "Event conditions were not met for " + profile);
            return false;
        }
        handle(profile);
        return true;
    }
}
