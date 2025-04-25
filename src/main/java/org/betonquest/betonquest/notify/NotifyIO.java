package org.betonquest.betonquest.notify;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.message.Message;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
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

    protected abstract void notifyPlayer(Component message, OnlineProfile onlineProfile) throws QuestException;

    protected final Variable<Number> getNumberData(final String dataKey, final Number defaultData) throws QuestException {
        final String dataString = data.get(dataKey);
        if (dataString == null) {
            return new Variable<>(defaultData);
        }
        return new Variable<>(BetonQuest.getInstance().getVariableProcessor(), pack, dataString, Argument.NUMBER);
    }
}
