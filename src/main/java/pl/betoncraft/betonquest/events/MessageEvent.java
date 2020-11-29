package pl.betoncraft.betonquest.events;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.conversation.Conversation;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Sends a message to the player, in his language
 *
 * @deprecated Use the {@link NotifyEvent} instead,
 * this will be removed in 2.0 release
 */
// TODO Delete in BQ 2.0.0
@Deprecated
public class MessageEvent extends QuestEvent {

    private final Map<String, String> messages = new HashMap<>();
    private final List<String> variables = new ArrayList<>();

    @SuppressWarnings("PMD.CyclomaticComplexity")
    public MessageEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        LogUtils.getLogger().log(Level.WARNING, "Message event will be REMOVED! Usage in package '"
                + instruction.getPackage().getName() + "'. Use the Notify system instead: "
                + "https://betonquest.github.io/BetonQuest/versions/dev/User-Documentation/Notifications/");
        final String[] parts;
        try {
            parts = instruction.getInstruction().substring(8).split(" ");
        } catch (IndexOutOfBoundsException e) {
            throw new InstructionParseException("Message missing", e);
        }
        if (parts.length < 1) {
            throw new InstructionParseException("Message missing");
        }
        String currentLang = Config.getLanguage();
        StringBuilder string = new StringBuilder();
        for (final String part : parts) {
            if (part.startsWith("conditions:") || part.startsWith("condition:")) {
                continue;
            } else if (part.matches("^\\{.+\\}$")) {
                if (string.length() > 0) {
                    messages.put(currentLang, string.toString().trim());
                    string = new StringBuilder();
                }
                currentLang = part.substring(1, part.length() - 1);
            } else {
                string.append(part).append(" ");
            }
        }
        if (string.length() > 0) {
            messages.put(currentLang, string.toString().trim());
        }
        if (messages.isEmpty()) {
            throw new InstructionParseException("Message missing");
        }
        for (final String message : messages.values()) {
            for (final String variable : BetonQuest.resolveVariables(message)) {
                try {
                    BetonQuest.createVariable(instruction.getPackage(), variable);
                } catch (InstructionParseException e) {
                    throw new InstructionParseException("Could not create '" + variable + "' variable: "
                            + e.getMessage(), e);
                }
                if (!variables.contains(variable)) {
                    variables.add(variable);
                }
            }
        }
    }

    @Override
    protected Void execute(final String playerID) {
        final String lang = BetonQuest.getInstance().getPlayerData(playerID).getLanguage();
        String message = messages.get(lang);
        if (message == null) {
            message = messages.get(Config.getLanguage());
        }
        if (message == null) {
            message = messages.values().iterator().next();
        }
        for (final String variable : variables) {
            message = message.replace(variable,
                    BetonQuest.getInstance().getVariableValue(instruction.getPackage().getName(), variable, playerID));
        }
        final String formattedMessage = Utils.format(message);
        final Conversation conversation = Conversation.getConversation(playerID);
        if (conversation == null || conversation.getInterceptor() == null) {
            PlayerConverter.getPlayer(playerID).sendMessage(formattedMessage);
        } else {
            conversation.getInterceptor().sendMessage(formattedMessage);
        }
        return null;
    }

}
