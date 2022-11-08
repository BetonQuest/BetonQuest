package org.betonquest.betonquest.api;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;

/**
 * <p>
 * Superclass for all events. You need to extend it in order to create new
 * custom events.
 * </p>
 * <p>
 * Registering your events is done using the
 * {@link BetonQuest#registerEvent(String, EventFactory, StaticEventFactory) registerEvent()} method.
 * </p>
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
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
     * If anything goes wrong, throw an {@link InstructionParseException} with an error message that helps the user fix
     * the event.
     *
     * @param instruction the {@link Instruction} object for this event; you need to
     *                    extract all required data from it and throw an
     *                    {@link InstructionParseException} if there is anything wrong
     * @param forceSync   If set to true this executes the event on the servers main thread.
     *                    Otherwise, it will keep running on the current thread (which could also be the main thread!).
     * @throws InstructionParseException when there is an error during syntax or argument parsing
     */
    public QuestEvent(final Instruction instruction, final boolean forceSync) throws InstructionParseException {
        super(forceSync);
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
                throw new InstructionParseException("Error while parsing event conditions: " + exception.getMessage(), exception);
            }
        }
    }

    /**
     * This method is called by BetonQuest to execute the event. Therefore, it must contain all logic for firing the event.
     * Use the data parsed in the event's constructor to fire the event based on the users settings.
     *
     * @param profile the {@link Profile} of the player for whom the event will be executed
     * @throws QuestRuntimeException when there is an error while running the event (for example a
     *                               numeric variable resolved to a string)
     */
    @Override
    protected abstract Void execute(Profile profile) throws QuestRuntimeException;

    /**
     * Returns the full id of this event. This includes the package path and the event name, seperated by a dot.
     *
     * @return the full id of this event
     */
    protected String getFullId() {
        return instruction.getID().getFullID();
    }

    /**
     * Fires an event for the profile if it meets the event's conditions.
     *
     * @param profile the {@link Profile} of the player for whom the event will fire
     * @throws QuestRuntimeException passes the exception from the event up the stack
     */
    public final void fire(final Profile profile) throws QuestRuntimeException {
        fire(profile, true);
    }

    /**
     * Fires an event for the profile if it meets the event's conditions.
     * If callBukkitEvent is set to true an {@link EventExecutedEvent} or {@link EventExecutedOnProfileEvent} will be called
     * depending on if a profile is provided or not.
     *
     * @param profile         the {@link Profile} of the player for whom the event will fire
     * @param callBukkitEvent whether to call a Bukkit event
     * @throws QuestRuntimeException passes the exception from the event up the stack
     */
    public final void fire(final Profile profile, final boolean callBukkitEvent) throws QuestRuntimeException {
        final EventID eventID = (EventID) instruction.getID();
        if (profile == null) {
            handleNullProfile(eventID, callBukkitEvent);
        } else if (profile.getOnlineProfile().isEmpty()) {
            handleOfflineProfile(profile, eventID, callBukkitEvent);
        } else {
            handleOnlineProfile(profile, eventID, callBukkitEvent);
        }
    }

    private void handleNullProfile(final EventID eventID, final boolean callBukkitEvent) throws QuestRuntimeException {
        if (staticness) {
            fireEvent(null, eventID, callBukkitEvent);
        } else {
            LOG.debug(instruction.getPackage(), "Static event will be fired once for every player:");
            for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                if (BetonQuest.conditions(onlineProfile, conditions)) {
                    LOG.debug(instruction.getPackage(), "  Firing this static event for player " + onlineProfile.getProfileName());
                    fireEvent(onlineProfile, eventID, callBukkitEvent);
                } else {
                    LOG.debug(instruction.getPackage(), "Event conditions were not met for player " + onlineProfile.getProfileName());
                }
            }
        }
    }

    private void handleOfflineProfile(final Profile profile, final EventID eventID, final boolean callBukkitEvent) throws QuestRuntimeException {
        if (persistent) {
            fireEvent(profile, eventID, callBukkitEvent);
        } else {
            LOG.debug(instruction.getPackage(), "Player " + profile.getPlayer() + " is offline, cannot fire event because it's not persistent.");
        }
    }

    private void handleOnlineProfile(final Profile profile, final EventID eventID, final boolean callBukkitEvent) throws QuestRuntimeException {
        if (BetonQuest.conditions(profile, conditions)) {
            fireEvent(profile, eventID, callBukkitEvent);
        } else {
            LOG.debug(instruction.getPackage(), "Event conditions were not met.");
        }
    }

    private void fireEvent(final Profile profile, final EventID eventID, final boolean callBukkitEvent) throws QuestRuntimeException {
        if (callBukkitEvent) {
            callBukkitEvent(profile, eventID);
        }
        handle(profile);
    }

    private void callBukkitEvent(final Profile profile, final EventID eventID) {
        final BetonQuest instance = BetonQuest.getInstance();

        Bukkit.getScheduler().scheduleAsyncDelayedTask(instance, () -> {
            if (profile == null) {
                Bukkit.getPluginManager().callEvent(new EventExecutedEvent(eventID));
            } else {
                Bukkit.getPluginManager().callEvent(new EventExecutedOnProfileEvent(profile, eventID));
            }
        });
    }

}
