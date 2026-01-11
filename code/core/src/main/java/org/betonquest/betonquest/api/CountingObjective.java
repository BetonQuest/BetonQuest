package org.betonquest.betonquest.api;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.ObjectiveData;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveProperties;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.quest.action.IngameNotificationSender;
import org.betonquest.betonquest.quest.action.NotificationLevel;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An objective that is not completed by doing some action just once, but multiple times. It provides common properties
 * and a versatile data object to track the progress.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public abstract class CountingObjective extends DefaultObjective {

    /**
     * The number of units required for completion.
     */
    protected final Argument<Number> targetAmount;

    /**
     * The message used for notifying the player.
     */
    @Nullable
    private final IngameNotificationSender countSender;

    /**
     * Create a counting objective.
     *
     * @param service           the objective service
     * @param targetAmount      the target amount of units required for completion
     * @param notifyMessageName the message name used for notifying by default
     * @throws QuestException if the syntax is wrong or any error happens while parsing
     */
    public CountingObjective(final ObjectiveService service, final Argument<Number> targetAmount,
                             @Nullable final String notifyMessageName) throws QuestException {
        super(service);
        final BetonQuest instance = BetonQuest.getInstance();
        final BetonQuestLoggerFactory loggerFactory = instance.getLoggerFactory();
        this.targetAmount = targetAmount;
        countSender = notifyMessageName == null ? null : new IngameNotificationSender(loggerFactory.create(CountingObjective.class),
                instance.getPluginMessage(), service.getObjectiveID().getPackage(), service.getObjectiveID().getFull(),
                NotificationLevel.INFO, notifyMessageName);
        service.setDefaultData(this::getDefaultDataInstruction);
        final ObjectiveProperties properties = service.getProperties();
        properties.setProperty("amount", profile -> getProperty("amount", profile));
        properties.setProperty("left", profile -> getProperty("left", profile));
        properties.setProperty("total", profile -> getProperty("total", profile));
        properties.setProperty("absoluteamount", profile -> getProperty("absoluteamount", profile));
        properties.setProperty("absoluteleft", profile -> getProperty("absoluteleft", profile));
        properties.setProperty("absolutetotal", profile -> getProperty("absolutetotal", profile));
    }

    private String getDefaultDataInstruction(final Profile profile) throws QuestException {
        return String.valueOf(targetAmount.getValue(profile).intValue());
    }

    private String getProperty(final String name, final Profile profile) throws QuestException {
        final CountingData countingData = getCountingData(profile);
        if (countingData == null) {
            return "";
        }
        final int data = switch (name.toLowerCase(Locale.ROOT)) {
            case "amount" -> countingData.getCompletedAmount();
            case "left" -> countingData.getAmountLeft();
            case "total" -> countingData.getTargetAmount();
            case "absoluteamount" -> Math.abs(countingData.getCompletedAmount());
            case "absoluteleft" -> Math.abs(countingData.getAmountLeft());
            case "absolutetotal" -> Math.abs(countingData.getTargetAmount());
            default -> throw new QuestException("Unknown property: " + name);
        };
        return Integer.toString(data);
    }

    /**
     * Get the {@link CountingData} objective data for given profile.
     *
     * @param profile the {@link Profile} to get the data for
     * @return counting objective data of the profile
     */
    @Nullable
    public final CountingData getCountingData(final Profile profile) {
        final String data = getService().getData().getOrDefault(profile, "");
        try {
            return new CountingData(data, profile, getObjectiveID());
        } catch (final QuestException e) {
            getLogger().error("Could not access CountingData for profile '" + profile + "': " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Complete the objective if fulfilled or else notify the profile's player if required. It will use the
     * {@link #countSender} if set, otherwise no notification will be sent, even if notification is set to
     * {@code true}.
     *
     * @param profile the {@link Profile} to act for
     * @return {@code true} if the objective is completed; {@code false} otherwise
     */
    protected final boolean completeIfDoneOrNotify(final Profile profile) {
        return completeIfDoneOrNotify(profile, countSender);
    }

    /**
     * Complete the objective if fulfilled or else notify the profile's player if required. It will use the provided
     * notification message name. If it is {@code null}, no notification is sent, even if a
     * {@link #countSender} was set and a notification should have been sent.
     *
     * @param profile            the {@link Profile} to act for
     * @param notificationSender the {@link IngameNotificationSender} to use for sending the notification
     * @return {@code true} if the objective is completed; {@code false} otherwise
     */
    protected final boolean completeIfDoneOrNotify(final Profile profile, @Nullable final IngameNotificationSender notificationSender) {
        final CountingData data = getCountingData(profile);
        if (data.isComplete()) {
            getService().complete(profile);
            return true;
        }
        if (notificationInterval(profile) > 0 && notificationSender != null && shouldNotify(profile, data) && profile.getOnlineProfile().isPresent()) {
            notificationSender.sendNotification(profile, new VariableReplacement("amount", Component.text(Math.abs(data.getAmountLeft()))));
        }
        return false;
    }

    private int notificationInterval(final Profile profile) {
        try {
            return getService().getServiceDataProvider().getNotificationInterval(profile);
        } catch (final QuestException e) {
            return 0;
        }
    }

    private boolean shouldNotify(final Profile profile, final CountingData data) {
        final int newAmount = Math.abs(data.getAmountLeft());
        final int oldAmount = Math.abs(data.getPreviousAmountLeft());
        final int interval = notificationInterval(profile);
        return newAmount > oldAmount && newAmount / interval != oldAmount / interval
                || newAmount < oldAmount && (newAmount - 1) / interval != (oldAmount - 1) / interval;
    }

    /**
     * Objective data for counting objectives.
     *
     * @deprecated do not use this class. it's scheduled for removal in future versions
     */
    @Deprecated
    public static class CountingData extends ObjectiveData {

        /**
         * Custom {@link BetonQuestLogger} instance for this class.
         */
        private final BetonQuestLogger log;

        /**
         * The target amount of units initially required for completion.
         */
        private final int targetAmount;

        /**
         * The direction factor. Enables {@link #progress(int)} and {@link #regress(int)} methods and fuzzy checking for
         * completion so that the target amount can be exceeded in both positive and negative direction.
         */
        private final int directionFactor;

        /**
         * The amount of units left for completion.
         */
        private final AtomicInteger amountLeft;

        /**
         * The last applied to the change of the units left for completion.
         */
        private final AtomicInteger lastChange;

        /**
         * Create a counting objective.
         *
         * @param instruction to {@link Integer} parsable string, containing the units to complete or output from
         *                    {@link #toString()}
         * @param profile     the {@link Profile} to create the data for
         * @param objID       id of the objective, used by BetonQuest to store this {@link ObjectiveData} in the database
         * @throws QuestException when the instruction format is invalid
         */
        public CountingData(final String instruction, final Profile profile, final ObjectiveID objID) throws QuestException {
            super(instruction, profile, objID);
            this.log = BetonQuest.getInstance().getLoggerFactory().create(CountingObjective.CountingData.class);
            final String countingInstruction = instruction.split(";", 2)[0];
            final String[] instructionParts = countingInstruction.split("/");
            switch (instructionParts.length) {
                case 1:
                    final AtomicBoolean dirty = new AtomicBoolean(false);
                    targetAmount = getTargetAmount(countingInstruction, dirty);
                    amountLeft = new AtomicInteger(targetAmount);
                    directionFactor = targetAmount < 0 ? -1 : 1;
                    lastChange = new AtomicInteger();
                    if (dirty.get()) {
                        final ObjectiveService service = BetonQuest.getInstance().getQuestTypeApi().getObjective(objID).getService();
                        update(service);
                    }
                    break;
                case 4:
                    targetAmount = Integer.parseInt(instructionParts[0]);
                    amountLeft = new AtomicInteger(Integer.parseInt(instructionParts[1]));
                    directionFactor = Integer.parseInt(instructionParts[2]);
                    lastChange = new AtomicInteger(Integer.parseInt(instructionParts[3]));
                    break;
                default:
                    throw new QuestException("Invalid instruction string: " + instruction);
            }
        }

        private int getTargetAmount(final String countingInstruction, final AtomicBoolean dirty) {
            try {
                return Integer.parseInt(countingInstruction);
            } catch (final NumberFormatException e) {
                log.warn(objID.getPackage(), "Loaded counting objective '" + objID + "' from database with invalid amount."
                        + " This is probably caused by a change of the objective's implementation."
                        + " The objective will be reset to an amount of 1."
                        + " This is normally the previous amount and can be ignored.");
                log.debug(objID.getPackage(), "Invalid instruction string: '" + instruction + "'");
                dirty.set(true);
                return 1;
            }
        }

        /**
         * Get the initially required amount of units that are needed to complete the objective after it just got
         * started.
         *
         * @return the target amount
         */
        public int getTargetAmount() {
            return targetAmount;
        }

        /**
         * Get the amount of units that are still required to complete the objective.
         *
         * @return the amount left
         */
        public int getAmountLeft() {
            return amountLeft.get();
        }

        /**
         * Get the amount ob units that were already completed since the objective has been started.
         *
         * @return the completed amount
         */
        public int getCompletedAmount() {
            return targetAmount - amountLeft.get();
        }

        /**
         * Get the direction factor. If the initial target amount was positive or zero it is {@code 1}, if it was
         * negative it is {@code -1}. The factor is used to determine completion and to let the objective progress when
         * not knowing if counting up or down is required to get nearer to the completion.
         *
         * @return the direction factor; either 1 or -1
         */
        public int getDirectionFactor() {
            return directionFactor;
        }

        /**
         * Get the last change of the amount of units completed that happened. The change is directed, thus reapplying
         * it can be done by using {@link #add(int)} and undoing by using {@link #subtract(int)}.
         *
         * @return the last change of the amount
         */
        public int getLastChange() {
            return lastChange.get();
        }

        /**
         * Get the amount of units that were still required to complete the objective before the last change.
         *
         * @return the amount left
         */
        public int getPreviousAmountLeft() {
            return amountLeft.get() - lastChange.get();
        }

        /**
         * Check for completion of the objective. It is true if the target amount has been reached or exceeded.
         *
         * @return true if the objective is completed; false otherwise
         */
        public boolean isComplete() {
            return amountLeft.get() * directionFactor <= 0;
        }

        /**
         * Move the one of unit towards completion of the objective. This is direction-aware.
         *
         * @return self for chaining statements
         */
        public CountingData progress() {
            return progress(1);
        }

        /**
         * Move the given amount of units towards completion of the objective. This is direction-aware.
         *
         * @param amount units to progress
         * @return self for chaining statements
         */
        public CountingData progress(final int amount) {
            return add(amount * directionFactor);
        }

        /**
         * Move the one of unit away from completion of the objective. This is direction-aware.
         *
         * @return self for chaining statements
         */
        public CountingData regress() {
            return regress(1);
        }

        /**
         * Move the given amount of units away from completion of the objective. This is direction-aware.
         *
         * @param amount units to regress
         * @return self for chaining statements
         */
        public CountingData regress(final int amount) {
            return subtract(amount * directionFactor);
        }

        /**
         * Move one unit in a positive direction. Same as {@link #progress()} when the target unit amount is positive.
         * This is <b>not</b> direction-aware.
         *
         * @return self for chaining statements
         */
        public CountingData add() {
            return add(1);
        }

        /**
         * Move the given amount of units in positive direction. Same as {@link #progress(int)} when the target unit
         * amount is positive. This is <b>not</b> direction-aware.
         *
         * @param amount units to progress
         * @return self for chaining statements
         */
        public CountingData add(final int amount) {
            return change(-amount);
        }

        /**
         * Move one unit in a negative direction. Same as {@link #regress()} when the target unit amount is negative.
         * This is <b>not</b> direction-aware.
         *
         * @return self for chaining statements
         */
        public CountingData subtract() {
            return subtract(1);
        }

        /**
         * Move the given amount of units in negative direction. Same as {@link #regress(int)} when the target unit
         * amount is negative. This is <b>not</b> direction-aware.
         *
         * @param amount units to progress
         * @return self for chaining statements
         */
        public CountingData subtract(final int amount) {
            return change(amount);
        }

        private CountingData change(final int amount) {
            final ObjectiveService service;
            try {
                service = BetonQuest.getInstance().getQuestTypeApi().getObjective(objID).getService();
            } catch (final QuestException e) {
                throw new IllegalStateException("Could not get objective service for objective '" + objID + "'", e);
            }
            amountLeft.accumulateAndGet(amount, Integer::sum);
            lastChange.set(amount);
            update(service);
            return this;
        }

        @Override
        public String toString() {
            return String.join("/",
                    Integer.toString(targetAmount),
                    Integer.toString(amountLeft.get()),
                    Integer.toString(directionFactor),
                    Integer.toString(lastChange.get()));
        }
    }
}
