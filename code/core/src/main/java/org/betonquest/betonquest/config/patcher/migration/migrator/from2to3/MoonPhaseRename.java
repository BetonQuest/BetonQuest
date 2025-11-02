package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the migration of moon phases.
 */
public class MoonPhaseRename implements QuestMigration {

    /**
     * Moon phases to rename.
     */
    private final Map<String, String> phasesMap;

    /**
     * Creates a new moon phase rename migration.
     */
    public MoonPhaseRename() {
        phasesMap = new HashMap<>();
        phasesMap.put("1", "FULL_MOON");
        phasesMap.put("2", "WANING_GIBBOUS");
        phasesMap.put("3", "THIRD_QUARTER");
        phasesMap.put("4", "WANING_CRESCENT");
        phasesMap.put("5", "NEW_MOON");
        phasesMap.put("6", "WAXING_CRESCENT");
        phasesMap.put("7", "FIRST_QUARTER");
        phasesMap.put("8", "WAXING_GIBBOUS");
    }

    @Override
    public void migrate(final Quest quest) {
        final MultiConfiguration config = quest.getQuestConfig();
        replaceStartValueInSection(config, "conditions", "mooncycle", "moonphase");
        for (final Map.Entry<String, String> entry : phasesMap.entrySet()) {
            replaceValueInSection(config, "conditions", "moonphase", " " + entry.getKey(), " " + entry.getValue());
        }
    }
}
