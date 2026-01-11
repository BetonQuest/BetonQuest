package org.betonquest.betonquest.id.schedule;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
import org.betonquest.betonquest.api.identifier.factory.DefaultIdentifierFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A {@link DefaultIdentifierFactory} for {@link ScheduleIdentifier}s.
 */
public class ScheduleIdentifierFactory extends DefaultIdentifierFactory<ScheduleIdentifier> {

    /**
     * Create a new schedule identifier factory.
     *
     * @param packManager the quest package manager to resolve relative paths
     */
    public ScheduleIdentifierFactory(final QuestPackageManager packManager) {
        super(packManager);
    }

    @Override
    public ScheduleIdentifier parseIdentifier(@Nullable final QuestPackage source, final String input) throws QuestException {
        final Map.Entry<QuestPackage, String> entry = parse(source, input);
        return new DefaultScheduleIdentifier(entry.getKey(), entry.getValue());
    }
}
