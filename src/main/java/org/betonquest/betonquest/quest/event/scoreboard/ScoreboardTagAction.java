package org.betonquest.betonquest.quest.event.scoreboard;

import org.betonquest.betonquest.api.profiles.OnlineProfile;

/**
 * The action to perform on a scoreboard tag.
 */
public enum ScoreboardTagAction {
    /**
     * Adds the tag to the player.
     */
    ADD((profile, tag) -> profile.getPlayer().addScoreboardTag(tag)),
    /**
     * Removes the tag from the player.
     */
    REMOVE((profile, tag) -> profile.getPlayer().removeScoreboardTag(tag));

    /**
     * The consumer to use.
     */
    private final BiConsumer biConsumer;

    ScoreboardTagAction(final BiConsumer biConsumer) {
        this.biConsumer = biConsumer;
    }

    /**
     * Executes the action.
     *
     * @param profile the profile to execute the action on
     * @param tag     the tag to execute the action with
     */
    public void execute(final OnlineProfile profile, final String tag) {
        biConsumer.accept(profile, tag);
    }

    /**
     * The consumer interface.
     */
    private interface BiConsumer {

        /**
         * Accepts the profile and tag.
         *
         * @param profile the profile
         * @param tag     the tag
         */
        void accept(OnlineProfile profile, String tag);
    }
}
