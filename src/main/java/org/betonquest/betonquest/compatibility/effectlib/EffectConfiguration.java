package org.betonquest.betonquest.compatibility.effectlib;

import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Set;

/**
 * All relevant data for displaying effects to player.
 *
 * @param effectClass            the EffectLib effectClass
 * @param locations              the locations in the configurationSection
 * @param npcs                   the npcs in the configurationSection
 * @param conditions             the conditions when the effect should be shown
 * @param settings               the whole configuration settings
 * @param conditionCheckInterval the interval when the conditions should be checked
 */
public record EffectConfiguration(String effectClass, List<VariableLocation> locations, Set<NpcID> npcs,
                                  List<ConditionID> conditions, ConfigurationSection settings,
                                  Integer conditionCheckInterval) {
}
