package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.module.BukkitCustomReward;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.action.ActionManager;
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
     * The BetonQuest ActionManager.
     */
    private final ActionManager actionManager;

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
     * @param actionManager     the action manager
     * @param profileProvider   the profile provider instance
     * @param identifierFactory the identifier factory
     */
    public ActionReward(final BetonQuestLogger log, final ActionManager actionManager, final ProfileProvider profileProvider,
                        final IdentifierFactory<ActionIdentifier> identifierFactory) {
        super();
        this.log = log;
        this.actionManager = actionManager;
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
            log.warn("Error while running quest reward - Missing Action Object.");
            return;
        }
        final String string = object.toString();
        try {
            final Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                log.warn("Error while running quest reward - Player with UUID '%s' not found.".formatted(uuid));
                return;
            }
            final ActionIdentifier action = identifierFactory.parseIdentifier(null, string);
            actionManager.run(profileProvider.getProfile(player), action);
        } catch (final QuestException e) {
            log.warn("Error while running quest reward - BetonQuest action '%s' not found: %s".formatted(string, e.getMessage()), e);
        }
    }
}
