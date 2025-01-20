package org.betonquest.betonquest.quest.event.notify;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.notify.NotifyIO;

import java.util.Map;

/**
 * {@link Event} the implementation of the notify events.
 */
public class NotifyEvent implements OnlineEvent {
    /**
     * The {@link NotifyIO} to use.
     */
    private final NotifyIO notifyIO;

    /**
     * The translations to use.
     */
    private final Map<String, VariableString> translations;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Creates a new {@link NotifyEvent}.
     *
     * @param notifyIO     the {@link NotifyIO} to use
     * @param translations the translations to use
     * @param dataStorage  the storage providing player data
     */
    public NotifyEvent(final NotifyIO notifyIO, final Map<String, VariableString> translations, final PlayerDataStorage dataStorage) {
        this.notifyIO = notifyIO;
        this.translations = translations;
        this.dataStorage = dataStorage;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final String playerLanguageKey = dataStorage.get(profile).getLanguage();
        final String defaultLanguageKey = Config.getLanguage();

        final VariableString message = translations.containsKey(playerLanguageKey)
                ? translations.get(playerLanguageKey)
                : translations.get(defaultLanguageKey);
        if (message == null) {
            throw new QuestException("Could not find a message!");
        }
        notifyIO.sendNotify(message.getValue(profile), profile);
    }
}
