package org.betonquest.betonquest.notify;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.argument.parser.NumberParser;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Sends a text notification to a player, potentially with a sound added.
 */
public abstract class NotifyIO {

    /**
     * Message to use when a data value does not exist.
     */
    protected static final String CATCH_MESSAGE_TYPE = "%s with the name '%s' does not exists!";

    /**
     * Variable processor to create and resolve variables.
     */
    protected final Variables variables;

    /**
     * Customization data.
     */
    protected final Map<String, String> data;

    /**
     * Source pack to resolve variables.
     */
    @Nullable
    protected final QuestPackage pack;

    /**
     * Notify sound to send sounds as additional part of the IO.
     */
    private final NotifySound sound;

    /**
     * Create a new Notify IO.
     *
     * @param variables the variable processor to create and resolve variables
     * @param pack      the source pack to resolve variables
     * @throws QuestException when data could not be parsed
     */
    protected NotifyIO(final Variables variables, final QuestPackage pack) throws QuestException {
        this(variables, pack, new HashMap<>());
    }

    /**
     * Create a new Notify IO.
     *
     * @param variables the variable processor to create and resolve variables
     * @param pack      the source pack to resolve variables
     * @param data      the customization data for notifications
     * @throws QuestException when data could not be parsed
     */
    protected NotifyIO(final Variables variables, @Nullable final QuestPackage pack, final Map<String, String> data) throws QuestException {
        this.variables = variables;
        this.data = data;
        this.pack = pack;
        sound = new NotifySound(this);
    }

    /**
     * Sends a message to a profile.
     * If a sound is set it will also play.
     *
     * @param message       the message to send
     * @param onlineProfile the profile to send the notification to
     * @throws QuestException when variables could not be resolved
     */
    public void sendNotify(final Component message, final OnlineProfile onlineProfile) throws QuestException {
        notifyPlayer(message, onlineProfile);
        sound.sendSound(onlineProfile);
    }

    /**
     * Sends the message to the profile.
     *
     * @param message       the message to send
     * @param onlineProfile the profile to send the notification to
     * @throws QuestException when variables could not be resolved
     */
    protected abstract void notifyPlayer(Component message, OnlineProfile onlineProfile) throws QuestException;

    /**
     * Get a number value for a key.
     *
     * @param dataKey     the key to get the value from
     * @param defaultData the default value if there is no custom value set for the key
     * @return a new variable
     * @throws QuestException when the value is not a number
     */
    protected final Argument<Number> getNumberData(final String dataKey, final Number defaultData) throws QuestException {
        final String dataString = data.get(dataKey);
        if (dataString == null) {
            return new DefaultArgument<>(defaultData);
        }
        return new DefaultArgument<>(variables, pack, dataString, NumberParser.DEFAULT);
    }
}
