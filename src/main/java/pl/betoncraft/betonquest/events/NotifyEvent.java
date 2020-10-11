package pl.betoncraft.betonquest.events;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.notify.Notify;
import pl.betoncraft.betonquest.notify.NotifyIO;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotifyEvent extends QuestEvent {
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("(?<key>[a-zA-Z]+):(?<value>\\S+)");
    private static final Pattern LANGUAGE_PATTERN = Pattern.compile("\\{(?<lang>[a-z]{2})\\} (?<message>.*?)(?=(?: )\\{[a-z]{2}\\} |$)");

    private final HashMap<String, String> messages;
    private final ArrayList<String> variables;
    private final NotifyIO notifyIO;

    public NotifyEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        messages = new HashMap<>();
        variables = new ArrayList<>();

        final String rawInstruction = instruction.getInstruction();
        final Matcher keyValueMatcher = KEY_VALUE_PATTERN.matcher(rawInstruction);

        final int indexStart = rawInstruction.indexOf(" ") + 1;
        final int indexEnd = keyValueMatcher.find() ? keyValueMatcher.start() - 1 : rawInstruction.length();
        keyValueMatcher.reset();

        final String langMessages = rawInstruction.substring(indexStart, indexEnd);
        checkVariables(langMessages);

        final Matcher languageMatcher = LANGUAGE_PATTERN.matcher(langMessages);
        while (languageMatcher.find()) {
            final String lang = languageMatcher.group("lang");
            final String message = languageMatcher.group("message")
                    .replace("\\{", "{")
                    .replace("\\:", ":");
            messages.put(lang, message);
        }
        if (messages.isEmpty()) {
            messages.put(Config.getLanguage(), langMessages);
        }
        if (!messages.containsKey(Config.getLanguage())) {
            throw new InstructionParseException("No message defined for default language '" + Config.getLanguage() + "'!");
        }

        final HashMap<String, String> data = new HashMap<>();
        while (keyValueMatcher.find()) {
            final String key = keyValueMatcher.group("key");
            final String value = keyValueMatcher.group("value");
            data.put(key, value);
        }
        final String category = data.remove("category");
        data.remove("events");
        data.remove("conditions");

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
