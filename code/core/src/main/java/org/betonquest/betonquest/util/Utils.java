package org.betonquest.betonquest.util;

import it.unimi.dsi.fastutil.Pair;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Various utilities.
 */
@SuppressWarnings({"LocalFinalVariableName", "CatchParameterName"})
public final class Utils {

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
}
