/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.compatibility.citizens;

import de.slikey.effectlib.util.DynamicLocation;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.effectlib.EffectLibIntegrator;
import pl.betoncraft.betonquest.compatibility.protocollib.NPCHider;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

/**
 * Displays a particle above NPCs with conversations.
 *
 * @author Jakub Sapalski
 */
public class CitizensParticle extends BukkitRunnable {

    private static CitizensParticle instance;
    private Set<Integer> npcs = new HashSet<>();
    private Map<UUID, Map<Integer, Effect>> players = new HashMap<>();
    private List<Effect> effects = new ArrayList<>();
    private int interval = 100;
    private int tick = 0;
    private boolean enabled = false;

    public CitizensParticle() {
        super();
        instance = this;
        // loop across all packages
        for (final ConfigPackage pack : Config.getPackages().values()) {

            // load all NPC ids
            if (pack.getMain().getConfig().getConfigurationSection("npcs") != null) {
                for (final String npcID : pack.getMain().getConfig().getConfigurationSection("npcs").getKeys(false)) {
                    try {
                        npcs.add(Integer.parseInt(npcID));
                    } catch (NumberFormatException e) {
                        LogUtils.logThrowableIgnore(e);
                    }
                }
            }

            // npc_effects contains all effects for NPCs
            final ConfigurationSection section = pack.getCustom().getConfig().getConfigurationSection("npc_effects");

            // if it's not defined then we're not displaying effects
            if (section == null) {
                continue;
            }
            // there's a setting to disable npc effects altogether
            if ("true".equalsIgnoreCase(section.getString("disabled"))) {
                return;
            }

            // load the condition check interval
            interval = section.getInt("check_interval", 100);
            if (interval <= 0) {
                LogUtils.getLogger().log(Level.WARNING, "Could not load npc effects of package " + pack.getName() + ": " +
                        "Check interval must be bigger than 0.");
                return;
            }

            // loading all effects
            for (final String key : section.getKeys(false)) {
                final ConfigurationSection settings = section.getConfigurationSection(key);

                // if the key is not a configuration section then it's not an effect
                if (settings == null) {
                    continue;
                }

                final Effect effect = new Effect();

                // the type of the effect, it's required
                effect.name = settings.getString("class");
                if (effect.name == null) {
                    continue;
                }

                // load the interval between animations
                effect.interval = settings.getInt("interval", 100);
                if (effect.interval <= 0) {
                    LogUtils.getLogger().log(Level.WARNING, "Could not load npc effect " + key + " in package " + pack.getName() + ": " +
                            "Effect interval must be bigger than 0.");
                    continue;
                }

                // load all NPCs for which this effect can be displayed
                effect.npcs = new HashSet<>();
                for (final int id : settings.getIntegerList("npcs")) {
                    effect.npcs.add(id);
                }

                // if the effect does not specify any NPCs then it's global
                if (effect.npcs.isEmpty()) {
                    effect.def = true;
                }

                // load all conditions
                effect.conditions = new ArrayList<>();
                for (final String cond : settings.getStringList("conditions")) {
                    try {
                        effect.conditions.add(new ConditionID(pack, cond));
                    } catch (ObjectNotFoundException e) {
                        LogUtils.logThrowableIgnore(e);
                    }
                }

                // set the effect settings
                effect.settings = settings;

                // add Effect
                effects.add(effect);

            }
        }

        runTaskTimer(BetonQuest.getInstance(), 1, 1);
        enabled = true;
    }

    /**
     * Reloads the particle effect
     */
    public static void reload() {
        if (instance.enabled) {
            instance.cancel();
        }
        new CitizensParticle();
    }

    @Override
    public void run() {

        // check conditions if it's the time
        if (tick % interval == 0) {
            checkConditions();
        }

        // run effects for all players
        activateEffects();

        tick++;
    }

    private void checkConditions() {

        // clear previous assignments
        players.clear();

        // every player needs to generate their assignment
        for (final Player player : Bukkit.getOnlinePlayers()) {

            // wrap an assignment map
            final Map<Integer, Effect> assignments = new HashMap<>();

            // handle all effects
            effects:
            for (final Effect effect : effects) {

                // skip the effect if conditions are not met
                for (final ConditionID condition : effect.conditions) {
                    if (!BetonQuest.condition(PlayerConverter.getID(player), condition)) {
                        continue effects;
                    }
                }

                // determine which NPCs should receive this effect
                final Collection<Integer> applicableNPCs = effect.def ? new HashSet<>(npcs) : effect.npcs;

                // assign this effect to all NPCs which don't have already assigned effects
                for (final Integer npc : applicableNPCs) {
                    if (!assignments.containsKey(npc)) {
                        assignments.put(npc, effect);
                    }
                }

            }

            // put assignments into the main map
            players.put(player.getUniqueId(), assignments);
        }
    }

    private void activateEffects() {

        // display effects for all players
        for (final Player player : Bukkit.getOnlinePlayers()) {

            // get NPC-effect assignments for this player
            final Map<Integer, Effect> assignments = players.get(player.getUniqueId());

            // skip if there are no assignments for this player
            if (assignments == null) {
                continue;
            }

            // display effects on all NPCs
            for (final Entry<Integer, Effect> entry : assignments.entrySet()) {
                final Integer npcId = entry.getKey();
                final Effect effect = entry.getValue();

                // skip this effect if it's not its time
                if (tick % effect.interval != 0) {
                    continue;
                }

                // get the NPC from its ID
                final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);

                // skip if there are no such NPC or it's not spawned or not visible
                if (npc == null || !npc.isSpawned() || npc.getEntity().getWorld() != player.getWorld() ||
                        NPCHider.getInstance() != null && NPCHider.getInstance().isInvisible(player, npc)) {
                    continue;
                }

                // prepare effect location
                final Location loc = npc.getStoredLocation().clone();
                loc.setPitch(-90);

                // fire the effect
                EffectLibIntegrator.getEffectManager().start(
                        effect.name,
                        effect.settings,
                        new DynamicLocation(loc, null),
                        new DynamicLocation(null, null),
                        (ConfigurationSection) null,
                        player);
            }
        }
    }

    private class Effect {

        private String name;
        private int interval;
        private boolean def;
        private Set<Integer> npcs;
        private List<ConditionID> conditions;
        private ConfigurationSection settings;

        private Effect() {}
    }

}
