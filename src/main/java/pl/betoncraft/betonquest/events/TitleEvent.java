package pl.betoncraft.betonquest.events;

import org.bukkit.Bukkit;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * @deprecated Use the {@link NotifyEvent} instead,
 * this will be removed in 2.0 release
 */
// TODO Delete in BQ 2.0.0
@Deprecated
public class TitleEvent extends QuestEvent {

    protected TitleType type;
    protected Map<String, String> messages = new HashMap<>();
    protected List<String> variables = new ArrayList<>();
    protected int fadeIn, stay, fadeOut;

    public TitleEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        LogUtils.getLogger().log(Level.WARNING, "Title event will be REMOVED! Usage in package '"
                + instruction.getPackage().getName() + "'. Use the Notify system instead: "
                + "https://betonquest.github.io/BetonQuest/versions/dev/User-Documentation/Notifications/");
        type = instruction.getEnum(TitleType.class);
        final String times = instruction.next();
        if (!times.matches("^\\d+;\\d+;\\d+$")) {
            throw new InstructionParseException("Could not parse title time.");
        }
        final String[] timeParts = times.split(";");
        try {
            fadeIn = Integer.parseInt(timeParts[0]);
            stay = Integer.parseInt(timeParts[1]);
            fadeOut = Integer.parseInt(timeParts[2]);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse title time.", e);
        }
        final String[] parts = instruction.getInstruction().split(" ");
        String currentLang = Config.getLanguage();
        StringBuilder string = new StringBuilder();
        for (int i = 3; i < parts.length; i++) {
            final String part = parts[i];
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
    protected Void execute(final String playerID) throws QuestRuntimeException {
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
        final String name = PlayerConverter.getName(playerID);
        if ((fadeIn != 20 || stay != 100 || fadeOut != 20) && (fadeIn != 0 || stay != 0 || fadeOut != 0)) {
            final String times = String.format("title %s times %d %d %d", name, fadeIn, stay, fadeOut);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), times);
        }
        final String title = String.format("title %s %s {\"text\":\"%s\"}",
                name, type.toString().toLowerCase(), message.replaceAll("&", "§"));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), title);
        return null;
    }

    public enum TitleType {
        TITLE, SUBTITLE
    }

}
