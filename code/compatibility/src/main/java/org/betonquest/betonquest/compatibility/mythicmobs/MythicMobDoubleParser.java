package org.betonquest.betonquest.compatibility.mythicmobs;

import io.lumine.mythic.api.mobs.MobManager;
import io.lumine.mythic.api.mobs.MythicMob;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.parser.NumberParser;

import java.util.Map;
import java.util.Optional;

/**
 * Parses a string to a {@link MythicMob} with level.
 * Expected format: {@code identifier:amount}.
 *
 * @param mobManager the Mob Manager to get the mob from
 */
public record MythicMobDoubleParser(
        MobManager mobManager) implements SimpleArgumentParser<Map.Entry<MythicMob, Double>> {

    /**
     * Expected format: {@code identifier:amount}.
     */
    private static final int MOB_FORMAT_LENGTH = 2;

    @Override
    public Map.Entry<MythicMob, Double> apply(final String value) throws QuestException {
        final String[] parts = value.split(":");
        if (parts.length != MOB_FORMAT_LENGTH) {
            throw new QuestException("Wrong mob format");
        }
        final Optional<MythicMob> mythicMob = mobManager.getMythicMob(parts[0]);
        if (mythicMob.isEmpty()) {
            throw new QuestException("MythicMob type " + parts[0] + " is invalid.");
        }
        return Map.entry(mythicMob.get(), NumberParser.DEFAULT.apply(parts[1]).doubleValue());
    }
}
