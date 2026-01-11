package org.betonquest.betonquest.compatibility.skript;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.ProfileProvider;
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
        this.log = plugin.getLoggerFactory().create(getClass());
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
                    final IdentifierFactory<ActionIdentifier> actionIdentifierFactory =
                            plugin.getQuestRegistries().identifiers().getFactory(ActionIdentifier.class);
                    plugin.getQuestTypeApi().action(profileProvider.getProfile(player.getSingle(event)),
                            actionIdentifierFactory.parseIdentifier(null, actionID));
                } catch (final QuestException e) {
                    log.warn("Error when running Skript event - could not load '" + actionID + "' action: " + e.getMessage(), e);
                }
            }
        }.runTask(plugin);
    }
}
