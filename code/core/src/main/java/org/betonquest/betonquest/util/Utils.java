package org.betonquest.betonquest.util;

import it.unimi.dsi.fastutil.Pair;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Various utilities.
 */
@SuppressWarnings({"LocalFinalVariableName", "CatchParameterName"})
public final class Utils {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuest.getInstance().getLoggerFactory().create(Utils.class);

    private Utils() {
    }

    /**
     * Gets the party of the location.
     * A range of 0 means worldwide and -1 means server-wide.
     *
     * @param conditionManager the condition manager to use
     * @param profiles         the online profiles in question
     * @param location         the location to get the party of
     * @param range            the range of the party
     * @param conditions       conditions that the party members must meet
     * @return the party of the location
     */
    public static Map<OnlineProfile, Double> getParty(final ConditionManager conditionManager, final Collection<OnlineProfile> profiles,
                                                      final Location location, final double range, final List<ConditionIdentifier> conditions) {
        final World world = location.getWorld();
        final double squared = range * range;

        final Stream<OnlineProfile> players = profiles.stream();
        final Stream<OnlineProfile> worldPlayers = range == -1 ? players : players.filter(profile -> world.equals(profile.getPlayer().getWorld()));
        final Stream<Pair<OnlineProfile, Double>> distancePlayers = worldPlayers.map(profile -> Pair.of(profile, getDistanceSquared(profile, location)));
        final Stream<Pair<OnlineProfile, Double>> rangePlayers = range <= 0 ? distancePlayers : distancePlayers.filter(pair -> pair.right() <= squared);
        return rangePlayers
                .filter(pair -> conditionManager.testAll(pair.left(), conditions))
                .collect(Collectors.toMap(Pair::left, Pair::right));
    }

    private static double getDistanceSquared(final OnlineProfile profile, final Location loc) {
        try {
            return profile.getPlayer().getLocation().distanceSquared(loc);
        } catch (final IllegalArgumentException e) {
            return Double.MAX_VALUE;
        }
    }

    /**
     * Parses the string as RGB or as DyeColor and returns it as Color.
     *
     * @param string string to parse as a Color
     * @return the Color (never null)
     * @throws QuestException when something goes wrong
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
    public static Color getColor(final String string) throws QuestException {
        if (string.isEmpty()) {
            throw new QuestException("Color is not specified");
        }
        try {
            return Color.fromRGB(Integer.parseInt(string));
        } catch (final NumberFormatException e1) {
            LOG.debug("Could not parse number!", e1);
            // string is not a decimal number
            try {
                return Color.fromRGB(Integer.parseInt(string.replace("#", ""), 16));
            } catch (final NumberFormatException e2) {
                LOG.debug("Could not parse number!", e2);
                // string is not a hexadecimal number, try dye color
                try {
                    return DyeColor.valueOf(string.trim().toUpperCase(Locale.ROOT).replace(' ', '_')).getColor();
                } catch (final IllegalArgumentException e3) {
                    // this was not a dye color name
                    throw new QuestException("Dye color does not exist: " + string, e3);
                }
            }
        } catch (final IllegalArgumentException e) {
            // string was a number, but incorrect
            throw new QuestException("Incorrect RGB code: " + string, e);
        }
    }

    /**
     * Checks the argument for null and throws when it is actual not present.
     * <p>
     * Primary used in constructors to check against nullable values.
     *
     * @param argument to check for null
     * @param message  of the exception when the argument is null
     * @param <A>      type of the argument
     * @return the argument, if not null
     * @throws QuestException if the argument is null
     */
    public static <A> A getNN(@Nullable final A argument, final String message) throws QuestException {
        if (argument == null) {
            throw new QuestException(message);
        }
        return argument;
    }
}
