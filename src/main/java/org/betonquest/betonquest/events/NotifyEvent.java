package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.notify.Notify;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("PMD.CommentRequired")
public class NotifyEvent extends QuestEvent {
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("(?<key>[a-zA-Z]+?):(?<value>\\S+)");
    private static final Pattern LANGUAGE_PATTERN = Pattern.compile("\\{(?<lang>[a-z]{2})\\} (?<message>.*?)(?=(?: )\\{[a-z]{2}\\} |$)");

    private final Map<String, String> messages;
    private final List<String> variables;
    private final NotifyIO notifyIO;

    public NotifyEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        messages = new HashMap<>();
        variables = new ArrayList<>();

        final HashMap<String, String> data = new HashMap<>();

        final String rawInstruction = instruction.getInstruction();
        final Matcher keyValueMatcher = KEY_VALUE_PATTERN.matcher(rawInstruction);

        final int indexStart = rawInstruction.indexOf(' ') + 1;
        if (indexStart != 0) {
            final int indexEnd = keyValueMatcher.find() ? keyValueMatcher.start() : rawInstruction.length();
            keyValueMatcher.reset();

            final String langMessages = rawInstruction.substring(indexStart, indexEnd);

            final Matcher languageMatcher = LANGUAGE_PATTERN.matcher(langMessages);
            while (languageMatcher.find()) {
                final String lang = languageMatcher.group("lang");
                final String message = languageMatcher.group("message")
                        .replace("\\{", "{")
                        .replace("\\:", ":");
                checkVariables(message);
                messages.put(lang, message);
            }
            if (messages.isEmpty()) {
                final String message = langMessages
                        .replace("\\{", "{")
                        .replace("\\:", ":");
                checkVariables(message);
                messages.put(Config.getLanguage(), message);
            }
            if (!messages.containsKey(Config.getLanguage())) {
                throw new InstructionParseException("No message defined for default language '" + Config.getLanguage() + "'!");
            }

            while (keyValueMatcher.find()) {
                final String key = keyValueMatcher.group("key");
                final String value = keyValueMatcher.group("value");
                data.put(key, value);
            }
            data.remove("events");
            data.remove("conditions");
        }
        final String category = data.remove("category");
        notifyIO = Notify.get(category, data);
    }

    private void checkVariables(final String message) throws InstructionParseException {
        for (final String variable : BetonQuest.resolveVariables(message)) {
            try {
                BetonQuest.createVariable(instruction.getPackage(), variable);
            } catch (final InstructionParseException exception) {
                throw new InstructionParseException("Could not create '" + variable + "' variable: "
                        + exception.getMessage(), exception);
            }
            if (!variables.contains(variable)) {
                variables.add(variable);
            }
        }
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final String lang = BetonQuest.getInstance().getPlayerData(playerID).getLanguage();
        String message = messages.get(lang);
        if (message == null) {
            message = messages.get(Config.getLanguage());
        }
        for (final String variable : variables) {
            message = message.replace(variable,
                    BetonQuest.getInstance().getVariableValue(instruction.getPackage().getName(), variable, playerID));
        }

        final Player player = PlayerConverter.getPlayer(playerID);
        notifyIO.sendNotify(message, player);
        return null;
    }

}
