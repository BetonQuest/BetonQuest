/**
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.slikey.effectlib.util.DynamicLocation;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.ConditionID;
import pl.betoncraft.betonquest.ObjectNotFoundException;
import pl.betoncraft.betonquest.compatibility.effectlib.EffectLibIntegrator;
import pl.betoncraft.betonquest.compatibility.protocollib.NPCHider;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Displays a particle above NPCs with conversations.
 * 
 * @author Jakub Sapalski
 */
public class CitizensParticle extends BukkitRunnable {

    private Set<Integer> npcs = new HashSet<>();
    private Map<UUID, Map<Integer, Effect>> players = new HashMap<>();
    private List<Effect> effects = new ArrayList<>();
    private static CitizensParticle instance;
    private int interval;
    private int tick = 0;
    private boolean enabled = false;

    public CitizensParticle() {
        instance = this;

        // loop across all packages
        for (ConfigPackage pack : Config.getPackages().values()) {

            // load all NPC ids
            for (String npcID : pack.getMain().getConfig().getConfigurationSection("npcs").getKeys(false)) {
                try {
                    npcs.add(Integer.parseInt(npcID));
                } catch (NumberFormatException e) {}
            }

            // npc_effects contains all effects for NPCs
            ConfigurationSection section = pack.getCustom().getConfig().getConfigurationSection("npc_effects");

            // if it's not defined then we're not displaying effects
            if (section == null) {
                continue;
            }

            // there's a setting to disable npc effects altogether
            if ("true".equalsIgnoreCase(section.getString("disabled"))) {
                continue;
            }

            // load the condition check interval
            interval = section.getInt("check_interval", 100);

            // loading all effects
            for (String key : section.getKeys(false)) {
                ConfigurationSection settings = section.getConfigurationSection(key);

                // if the key is not a configuration section then it's not an effect
                if (settings == null) {
                    continue;
                }

                Effect effect = new Effect();

                // the type of the effect, it's required
                effect.name = settings.getString("class");
                if (effect.name == null) {
                    continue;
                }

                // load the interval between animations
                effect.interval = settings.getInt("interval", 20);

                // load all NPCs for which this effect can be displayed
                effect.npcs = new HashSet<>();
                for (int id : settings.getIntegerList("npcs")) {
                    effect.npcs.add(id);
                }

                // if the effect does not specify any NPCs then it's global
                if (effect.npcs.isEmpty()) {
                    effect.def = true;
                }

                // load all conditions
                effect.conditions = new ArrayList<>();
                for (String cond : settings.getStringList("conditions")) {
                    try {
                        effect.conditions.add(new ConditionID(pack, cond));
                    } catch (ObjectNotFoundException e) {}
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

    private class Effect {

        private String name;
        private int interval;
        private boolean def;
        private Set<Integer> npcs;
        private List<ConditionID> conditions;
        private ConfigurationSection settings;
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
        for (Player player : Bukkit.getOnlinePlayers()) {

            // create an assignment map
            Map<Integer, Effect> assignments = new HashMap<>();

            // handle all effects
            effects:
            for (Effect effect : effects) {

                // skip the effect if conditions are not met
                for (ConditionID condition : effect.conditions) {
                    if (!BetonQuest.condition(PlayerConverter.getID(player), condition)) {
                        continue effects;
                    }
                }

                // determine which NPCs should receive this effect
                Collection<Integer> applicableNPCs = effect.def ? new HashSet<>(npcs) : effect.npcs;

                // assign this effect to all NPCs which don't have already assigned effects
                for (Integer npc : applicableNPCs) {
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
        for (Player player : Bukkit.getOnlinePlayers()) {

            // get NPC-effect assignments for this player
            Map<Integer, Effect> assignments = players.get(player.getUniqueId());
            
            // skip if there are no assignments for this player
            if (assignments == null) {
                continue;
            }

            // display effects on all NPCs
            for (Entry<Integer, Effect> entry : assignments.entrySet()) {
                Integer id = entry.getKey();
                Effect effect = entry.getValue();

                // skip this effect if it's not its time
                if (tick % effect.interval != 0) {
                    continue;
                }

                // get the NPC from its ID
                NPC npc = CitizensAPI.getNPCRegistry().getById(id);
                
                // skip if there are no such NPC or it's not spawned or not visible
                if (npc == null || !npc.isSpawned() ||
                        (NPCHider.getInstance() != null && NPCHider.getInstance().isInvisible(player, npc))) {
                    continue;
                }
                
                // prepare effect location
                Location loc = npc.getStoredLocation().clone();
                loc.setPitch(-90);

                // fire the effect
                EffectLibIntegrator.getEffectManager().start(
                        effect.name,
                        effect.settings,
                        new DynamicLocation(loc, null),
                        new DynamicLocation(null, null),
                        null,
                        player);
                
            }
        }
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

}
