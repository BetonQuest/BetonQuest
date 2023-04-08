package org.betonquest.betonquest.quest.event.notify;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.notify.NotifyIO;

import java.util.Map;

/**
 * {@link Event} the implementation of the notify events.
 */
public class NotifyEvent implements Event {

    /**
     * The {@link NotifyIO} to use.
     */
    private final NotifyIO notifyIO;
    /**
     * The translations to use.
     */
    private final Map<String, VariableString> translations;

    /**
     * Creates a new {@link NotifyEvent}.
     *
     * @param notifyIO     the {@link NotifyIO} to use
     * @param translations the translations to use
     */
    public NotifyEvent(final NotifyIO notifyIO, final Map<String, VariableString> translations) {
        this.notifyIO = notifyIO;
        this.translations = translations;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final String playerLanguageKey = BetonQuest.getInstance().getPlayerData(profile).getLanguage();
        final String defaultLanguageKey = Config.getLanguage();

        final VariableString message = translations.containsKey(playerLanguageKey)
                ? translations.get(playerLanguageKey)
                : translations.get(defaultLanguageKey);
        notifyIO.sendNotify(message.getString(profile), profile.getOnlineProfile().get());
    }
}
