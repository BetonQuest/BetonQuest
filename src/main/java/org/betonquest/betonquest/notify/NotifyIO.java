package org.betonquest.betonquest.notify;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.message.Message;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public abstract class NotifyIO {
    protected static final String CATCH_MESSAGE_FLOAT = "%s '%s' couldn't be parsed, it is not a valid floating point number!";

    protected static final String CATCH_MESSAGE_INTEGER = "%s '%s' couldn't be parsed, it is not a valid number!";

    protected static final String CATCH_MESSAGE_TYPE = "%s with the name '%s' does not exists!";

    protected final Map<String, String> data;

    @Nullable
    protected final QuestPackage pack;

    private final NotifySound sound;

    protected NotifyIO(final QuestPackage pack) throws QuestException {
        this(pack, new HashMap<>());
    }

    protected NotifyIO(@Nullable final QuestPackage pack, final Map<String, String> data) throws QuestException {
        this.data = data;
        this.pack = pack;
        sound = new NotifySound(this);
    }

    public void sendNotify(final Message message, final OnlineProfile onlineProfile) throws QuestException {
        notifyPlayer(message.asComponent(onlineProfile), onlineProfile);
        sound.sendSound(onlineProfile);
    }

    protected abstract void notifyPlayer(Component message, OnlineProfile onlineProfile);

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
        }
        final ProfileProvider profileProvider = BetonQuest.getInstance().getProfileProvider();
        return new VariableNumber(BetonQuest.getInstance().getVariableProcessor(), pack, dataString)
                .getValue(profileProvider.getProfile(player)).floatValue();
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
