package org.betonquest.betonquest.compatibility.skript;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

/**
 * Skript condition, which checks specified BetonQuest's condition.
 */
@SuppressWarnings("NullAway.Init")
public class SkriptConditionBQ extends Condition {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest plugin;

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * The player for whom the condition is checked.
     */
    private Expression<Player> player;

    /**
     * The condition identifier to be checked.
     */
    private Expression<String> condition;

    /**
     * Constructs a new SkriptCondition for BetonQuest conditions.
     */
    public SkriptConditionBQ() {
        super();
        this.plugin = BetonQuest.getInstance();
        this.log = plugin.getLoggerFactory().create(getClass());
        this.packManager = plugin.getQuestPackageManager();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(final Expression<?>[] arg0, final int arg1, final Kleenean arg2, final ParseResult arg3) {
        player = (Expression<Player>) arg0[0];
        condition = (Expression<String>) arg0[1];
        return true;
    }

    @Override
    @SuppressFBWarnings("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
    public String toString(@Nullable final Event event, final boolean debug) {
        return player.getSingle(event).getName() + " meets " + condition;
    }

    @Override
    public boolean check(final Event event) {
        final String conditionID = condition.getSingle(event);
        try {
            final ProfileProvider profileProvider = plugin.getProfileProvider();
            return plugin.getQuestTypeApi().condition(profileProvider.getProfile(player.getSingle(event)), new ConditionID(packManager, null, conditionID));
        } catch (final QuestException e) {
            log.warn("Error while checking Skript condition - could not load condition with ID '" + conditionID + "': " + e.getMessage(), e);
            return false;
        }
    }
}
