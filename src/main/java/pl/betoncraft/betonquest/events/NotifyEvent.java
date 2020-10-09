package pl.betoncraft.betonquest.events;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.notify.Notify;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Send a Notification Message
 */

public class NotifyEvent extends QuestEvent {

    private final Map<String, String> data;
    private String category;
    private final String message;

    /**
     * Provide a Notification
     * <p>
     * Format of instruction:
     * notify message to send category:value [optional_data:value...]
     *
     * @param instruction Instruction to parse
     * @throws InstructionParseException
     */

    public NotifyEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        data = new HashMap<>();
        final StringJoiner messageBuilder = new StringJoiner(" ");
        for (int i = 0; i < instruction.size() - 1; i++) {
            instruction.next();
            if (!instruction.current().contains(":")) {
                messageBuilder.add(instruction.current());
                continue;
            }
            final String[] parts = instruction.current().split(":", 2);

            if (parts[0].trim().equalsIgnoreCase("category")) {
                category = parts[1].trim();
                continue;
            }

            data.put(parts[0].trim(), parts[1].trim());
        }

        message = messageBuilder.toString();
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        Notify.get(category, data).sendNotify(instruction.getPackage().getName(), message, player);
        return null;
    }

}
