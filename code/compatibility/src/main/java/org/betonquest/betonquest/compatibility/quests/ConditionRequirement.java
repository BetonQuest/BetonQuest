package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.module.BukkitCustomRequirement;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Requires the player to meet specified condition.
 */
@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
public class ConditionRequirement extends BukkitCustomRequirement {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The BetonQuest ConditionManager.
     */
    private final ConditionManager conditionManager;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Identifier factory.
     */
    private final IdentifierFactory<ConditionIdentifier> identifierFactory;

    /**
     * Create a new 'Quests' Condition Requirement.
     *
     * @param log               the custom logger
     * @param conditionManager  the condition manager
     * @param profileProvider   the profile provider instance
     * @param identifierFactory the identifier factory
     */
    public ConditionRequirement(final BetonQuestLogger log, final ConditionManager conditionManager, final ProfileProvider profileProvider,
                                final IdentifierFactory<ConditionIdentifier> identifierFactory) {
        super();
        this.log = log;
        this.conditionManager = conditionManager;
        this.profileProvider = profileProvider;
        this.identifierFactory = identifierFactory;
        setName("BetonQuest condition");
        setAuthor("BetonQuest");
        addStringPrompt("Condition", "Specify BetonQuest condition with the package and the name", null);
    }

    @Override
    public boolean testRequirement(final UUID uuid, final Map<String, Object> dataMap) {
        final Object object = dataMap.get("Condition");
        if (object == null) {
            log.warn("Error while checking quest requirement - Missing Condition Object");
            return false;
        }
        final String string = object.toString();
        try {
            final Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                log.warn("Error while running quest reward, player with UUID '%s' not found.".formatted(uuid));
                return false;
            }
            final ConditionIdentifier condition = identifierFactory.parseIdentifier(null, string);
            return conditionManager.test(profileProvider.getProfile(player), condition);
        } catch (final QuestException e) {
            log.warn("Error while checking quest requirement, BetonQuest condition '%s' not found: %s".formatted(string, e.getMessage()), e);
            return false;
        }
    }
}
