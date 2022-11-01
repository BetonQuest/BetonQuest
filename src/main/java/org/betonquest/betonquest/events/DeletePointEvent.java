package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.Utils;

/**
 * Modifies player's points
 */
@SuppressWarnings("PMD.CommentRequired")
public class DeletePointEvent extends QuestEvent {

    protected final String category;

    public DeletePointEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        persistent = true;
        staticness = true;
        category = Utils.addPackage(instruction.getPackage(), instruction.next());
    }

    @Override
    protected Void execute(final Profile profile) {
        if (profile == null) {
            for (final Profile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                final PlayerData playerData = BetonQuest.getInstance().getPlayerData(onlineProfile);
                playerData.removePointsCategory(category);
            }
            BetonQuest.getInstance().getSaver().add(new Saver.Record(UpdateType.REMOVE_ALL_POINTS, category));
        } else if (profile.getPlayer() == null) {
            final PlayerData playerData = new PlayerData(profile);
            playerData.removePointsCategory(category);
        } else {
            final PlayerData playerData = BetonQuest.getInstance().getPlayerData(profile);
            playerData.removePointsCategory(category);
        }
        return null;
    }
}
