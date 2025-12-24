package org.betonquest.betonquest.compatibility.mythicmobs;

import io.lumine.mythic.api.mobs.MobManager;
import io.lumine.mythic.api.mobs.MythicMob;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;

import java.util.Optional;

/**
 * Parses a string to a {@link MythicMob}.
 *
 * @param mobManager the Mob Manager to get the mob from
 */
public record MythicMobParser(MobManager mobManager) implements SimpleArgumentParser<MythicMob> {

    @Override
    public MythicMob apply(final String value) throws QuestException {
        final Optional<MythicMob> mythicMob = mobManager.getMythicMob(value);
        if (mythicMob.isEmpty()) {
            throw new QuestException("MythicMob type " + value + " is invalid.");
        }
        return mythicMob.get();
    }
}
