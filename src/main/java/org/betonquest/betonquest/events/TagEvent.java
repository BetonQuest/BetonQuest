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
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Adds or removes tags from the player
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CommentRequired"})
public class TagEvent extends QuestEvent {

    protected final String[] tags;
    protected final boolean add;

    public TagEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        persistent = true;
        staticness = true;
        add = "add".equalsIgnoreCase(instruction.next());
        tags = instruction.getArray();
        for (int i = 0; i < tags.length; i++) {
            tags[i] = Utils.addPackage(instruction.getPackage(), tags[i]);
        }
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    @Override
    protected Void execute(final Profile profile) {
        if (profile == null) {
            if (!add) {
                for (final String tag : tags) {
                    for (final Profile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                        final PlayerData playerData = BetonQuest.getInstance().getPlayerData(onlineProfile);
                        playerData.removeTag(tag);
                    }
                    BetonQuest.getInstance().getSaver().add(new Saver.Record(UpdateType.REMOVE_ALL_TAGS, tag));
                }
            }
        } else if (profile.getPlayer().isEmpty()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    final PlayerData playerData = new PlayerData(profile);
                    if (add) {
                        for (final String tag : tags) {
                            playerData.addTag(tag);
                        }
                    } else {
                        for (final String tag : tags) {
                            playerData.removeTag(tag);
                        }
                    }
                }
            }.runTaskAsynchronously(BetonQuest.getInstance());
        } else {
            final PlayerData playerData = BetonQuest.getInstance().getPlayerData(profile);
            if (add) {
                for (final String tag : tags) {
                    playerData.addTag(tag);
                }
            } else {
                for (final String tag : tags) {
                    playerData.removeTag(tag);
                }
            }
        }
        return null;
    }
}
