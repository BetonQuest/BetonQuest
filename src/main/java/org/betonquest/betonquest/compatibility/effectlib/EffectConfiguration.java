package org.betonquest.betonquest.compatibility.effectlib;

import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

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
public record EffectConfiguration(String effectClass, Variable<List<Location>> locations, Variable<List<NpcID>> npcs,
                                  Variable<List<ConditionID>> conditions, ConfigurationSection settings,
                                  Integer conditionCheckInterval) {
}
