package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public abstract class NotifyIO {
    protected final static String CATCH_MESSAGE_FLOAT = "%s '%s' couldn't be parsed, it is not a valid floating point number!";
    protected final static String CATCH_MESSAGE_INTEGER = "%s '%s' couldn't be parsed, it is not a valid number!";
    protected final static String CATCH_MESSAGE_TYPE = "%s with the name '%s' does not exists!";

    protected final Map<String, String> data;
    private final NotifySound sound;

    protected NotifyIO() throws InstructionParseException {
        this(new HashMap<>());
    }

    protected NotifyIO(final Map<String, String> data) throws InstructionParseException {
        this.data = data;
        sound = new NotifySound(this);
    }

    public void sendNotify(@NotNull final String message) throws QuestRuntimeException {
        for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
            sendNotify(message, player);
        }
    }

    public void sendNotify(@NotNull final String message, @NotNull final Player player) throws QuestRuntimeException {
        notifyPlayer(Utils.format(message), player);
        sound.sendSound(player);
    }

    protected abstract void notifyPlayer(final String message, final Player player);

    protected float getFloatData(final String dataKey, final float defaultData) throws InstructionParseException {
        final String dataString = data.get(dataKey);
        try {
            return dataString == null ? defaultData : Float.parseFloat(dataString);
        } catch (final NumberFormatException exception) {
            throw new InstructionParseException(String.format(CATCH_MESSAGE_FLOAT, dataKey, dataString), exception);
        }
    }

    protected int getIntegerData(final String dataKey, final int defaultData) throws InstructionParseException {
        final String dataString = data.get(dataKey);
        try {
            return dataString == null ? defaultData : Integer.parseInt(dataString);
        } catch (final NumberFormatException exception) {
            throw new InstructionParseException(String.format(CATCH_MESSAGE_INTEGER, dataKey, dataString), exception);
        }
    }
}
