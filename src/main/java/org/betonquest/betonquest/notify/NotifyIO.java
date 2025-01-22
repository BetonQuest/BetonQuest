package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.util.PlayerConverter;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public abstract class NotifyIO {
    protected static final String CATCH_MESSAGE_FLOAT = "%s '%s' couldn't be parsed, it is not a valid floating point number!";

    protected static final String CATCH_MESSAGE_INTEGER = "%s '%s' couldn't be parsed, it is not a valid number!";

    protected static final String CATCH_MESSAGE_TYPE = "%s with the name '%s' does not exists!";

    protected final Map<String, String> data;

    protected final QuestPackage pack;

    private final NotifySound sound;

    protected NotifyIO(final QuestPackage pack) throws QuestException {
        this(pack, new HashMap<>());
    }

    protected NotifyIO(final QuestPackage pack, final Map<String, String> data) throws QuestException {
        this.data = data;
        this.pack = pack;
        sound = new NotifySound(this);
    }

    public void sendNotify(final String message) throws QuestException {
        for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
            sendNotify(message, onlineProfile);
        }
    }

    public void sendNotify(final String message, final OnlineProfile onlineProfile) throws QuestException {
        notifyPlayer(Utils.format(message), onlineProfile);
        sound.sendSound(onlineProfile);
    }

    protected abstract void notifyPlayer(String message, OnlineProfile onlineProfile);

    protected final float getFloatData(final String dataKey, final float defaultData) throws QuestException {
        final String dataString = data.get(dataKey);

        if (dataString == null || dataString.startsWith("%")) {
            return defaultData;
        }
        try {
            return Float.parseFloat(dataString);
        } catch (final NumberFormatException exception) {
            throw new QuestException(String.format(CATCH_MESSAGE_FLOAT, dataKey, dataString), exception);
        }
    }

    protected float getFloatData(final Player player, final String dataKey, final float defaultData) throws QuestException {
        final String dataString = data.get(dataKey);
        if (dataString == null) {
            return defaultData;
        } else if (dataString.startsWith("%")) {
            return new VariableNumber(BetonQuest.getInstance().getVariableProcessor(), pack, dataString)
                    .getValue(PlayerConverter.getID(player)).floatValue();
        }
        try {
            return Float.parseFloat(dataString);
        } catch (final NumberFormatException exception) {
            throw new QuestException(String.format(CATCH_MESSAGE_FLOAT, dataKey, dataString), exception);
        }
    }

    protected final int getIntegerData(final String dataKey, final int defaultData) throws QuestException {
        final String dataString = data.get(dataKey);
        try {
            return dataString == null ? defaultData : Integer.parseInt(dataString);
        } catch (final NumberFormatException exception) {
            throw new QuestException(String.format(CATCH_MESSAGE_INTEGER, dataKey, dataString), exception);
        }
    }
}
