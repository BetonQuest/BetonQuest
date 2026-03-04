package org.betonquest.betonquest.quest.action.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.data.Persistence;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;

import java.util.List;

/**
 * Action to delete tags from all players.
 */
public class DeleteTagPlayerlessAction implements PlayerlessAction {

    /**
     * Storage for persistent data.
     */
    private final Persistence persistence;

    /**
     * The saver to inject into database-using actions.
     */
    private final Saver saver;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * The list of tags to delete.
     */
    private final Argument<List<String>> tags;

    /**
     * Create a new delete tag playerless action.
     *
     * @param persistence     the storage providing persistent data
     * @param saver           database saver to use
     * @param profileProvider the profile provider instance
     * @param tags            the list of tags to delete
     */
    public DeleteTagPlayerlessAction(final Persistence persistence, final Saver saver, final ProfileProvider profileProvider, final Argument<List<String>> tags) {
        this.persistence = persistence;
        this.saver = saver;
        this.profileProvider = profileProvider;
        this.tags = tags;
    }

    @Override
    public void execute() throws QuestException {
        for (final String tag : tags.getValue(null)) {
            for (final OnlineProfile onlineProfile : profileProvider.getOnlineProfiles()) {
                persistence.profile(onlineProfile).tags().remove(tag);
            }
            saver.add(new Saver.Record(UpdateType.REMOVE_ALL_TAGS, tag));
        }
    }
}
