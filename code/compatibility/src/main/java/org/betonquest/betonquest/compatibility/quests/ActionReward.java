package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.module.BukkitCustomReward;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Fires a BetonQuest action as a quest reward.
 */
@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
public class ActionReward extends BukkitCustomReward {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Identifier factory.
     */
    private final IdentifierFactory<ActionIdentifier> identifierFactory;

    /**
     * Create a new 'Quests' Reward.
     *
     * @param log               the custom logger
     * @param questTypeApi      the Quest Type API
     * @param profileProvider   the profile provider instance
     * @param identifierFactory the identifier factory
     */
    public ActionReward(final BetonQuestLogger log, final QuestTypeApi questTypeApi, final ProfileProvider profileProvider,
                        final IdentifierFactory<ActionIdentifier> identifierFactory) {
        super();
        this.log = log;
        this.questTypeApi = questTypeApi;
        this.profileProvider = profileProvider;
        this.identifierFactory = identifierFactory;
        setName("BetonQuest action");
        setAuthor("BetonQuest");
        addStringPrompt("Action", "Specify BetonQuest action with the package and the name", null);
    }

    @Override
    public void giveReward(final UUID uuid, final Map<String, Object> dataMap) {
        final Object object = dataMap.get("Action");
        if (object == null) {
            log.warn("Error while checking quest requirement - Missing Action Object");
            return;
        }
        final String string = object.toString();
        try {
            final Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                log.warn("Error while running quest reward - Player with UUID '" + uuid + "' not found.");
                return;
            }
            final ActionIdentifier action = identifierFactory.parseIdentifier(null, string);
            questTypeApi.action(profileProvider.getProfile(player), action);
        } catch (final QuestException e) {
            log.warn("Error while running quest reward - BetonQuest action '" + string + "' not found: " + e.getMessage(), e);
        }
    }
}
