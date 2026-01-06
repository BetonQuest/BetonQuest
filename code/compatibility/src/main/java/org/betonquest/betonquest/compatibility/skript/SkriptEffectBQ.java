package org.betonquest.betonquest.compatibility.skript;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

/**
 * Skript effect, which fires specified BetonQuest's action.
 */
@SuppressWarnings("NullAway.Init")
public class SkriptEffectBQ extends Effect {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest plugin;

    /**
     * The {@link Placeholders} to create and resolve placeholders.
     */
    private final Placeholders placeholders;

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * The action identifier to be fired.
     */
    private Expression<String> action;

    /**
     * The player for whom the action is fired.
     */
    private Expression<Player> player;

    /**
     * Constructs a new SkriptEffect for BetonQuest actions.
     */
    public SkriptEffectBQ() {
        super();
        this.plugin = BetonQuest.getInstance();
        this.placeholders = plugin.getQuestTypeApi().placeholders();
        this.log = plugin.getLoggerFactory().create(getClass());
        packManager = plugin.getQuestPackageManager();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
        this.action = (Expression<String>) exprs[0];
        this.player = (Expression<Player>) exprs[1];
        return true;
    }

    @Override
    @SuppressFBWarnings("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
    public String toString(@Nullable final Event event, final boolean debug) {
        return "fire " + this.action + " for " + player.getSingle(event).getName();
    }

    @Override
    protected void execute(final Event event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                final String actionID = SkriptEffectBQ.this.action.getSingle(event);
                try {
                    final ProfileProvider profileProvider = plugin.getProfileProvider();
                    plugin.getQuestTypeApi().action(profileProvider.getProfile(player.getSingle(event)),
                            new ActionID(placeholders, packManager, null, actionID));
                } catch (final QuestException e) {
                    log.warn("Error when running Skript event - could not load '" + actionID + "' action: " + e.getMessage(), e);
                }
            }
        }.runTask(plugin);
    }
}
