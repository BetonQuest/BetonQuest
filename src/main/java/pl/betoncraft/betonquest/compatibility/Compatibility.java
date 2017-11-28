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
package pl.betoncraft.betonquest.compatibility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.betonlangapi.BetonLangAPIIntegrator;
import pl.betoncraft.betonquest.compatibility.bountifulapi.BountifulAPIIntegrator;
import pl.betoncraft.betonquest.compatibility.brewery.BreweryIntegrator;
import pl.betoncraft.betonquest.compatibility.citizens.CitizensIntegrator;
import pl.betoncraft.betonquest.compatibility.denizen.DenizenIntegrator;
import pl.betoncraft.betonquest.compatibility.effectlib.EffectLibIntegrator;
import pl.betoncraft.betonquest.compatibility.heroes.HeroesIntegrator;
import pl.betoncraft.betonquest.compatibility.holographicdisplays.HolographicDisplaysIntegrator;
import pl.betoncraft.betonquest.compatibility.legendquest.LegendQuestIntegrator;
import pl.betoncraft.betonquest.compatibility.magic.MagicIntegrator;
import pl.betoncraft.betonquest.compatibility.mcmmo.McMMOIntegrator;
import pl.betoncraft.betonquest.compatibility.mythicmobs.MythicMobsIntegrator;
import pl.betoncraft.betonquest.compatibility.placeholderapi.PlaceholderAPIIntegrator;
import pl.betoncraft.betonquest.compatibility.playerpoints.PlayerPointsIntegrator;
import pl.betoncraft.betonquest.compatibility.protocollib.ProtocolLibIntegrator;
import pl.betoncraft.betonquest.compatibility.quests.QuestsIntegrator;
import pl.betoncraft.betonquest.compatibility.racesandclasses.RacesAndClassesIntegrator;
import pl.betoncraft.betonquest.compatibility.shopkeepers.ShopkeepersIntegrator;
import pl.betoncraft.betonquest.compatibility.skillapi.SkillAPIIntegrator;
import pl.betoncraft.betonquest.compatibility.skript.SkriptIntegrator;
import pl.betoncraft.betonquest.compatibility.vault.VaultIntegrator;
import pl.betoncraft.betonquest.compatibility.worldedit.WorldEditIntegrator;
import pl.betoncraft.betonquest.compatibility.worldguard.WorldGuardIntegrator;
import pl.betoncraft.betonquest.utils.Debug;

/**
 * Compatibility with other plugins
 * 
 * @author Jakub Sapalski
 */
public class Compatibility implements Listener {

    private static Compatibility instance;
    private Map<String, Integrator> integrators = new HashMap<>();
	private BetonQuest plugin = BetonQuest.getInstance();
	private List<String> hooked = new ArrayList<>();

    private void hook(Plugin hook) {
        
        // don't want to hook twice
        if (hooked.contains(hook.getName())) {
            return;
        }
        
        // don't want to hook into disabled plugins
        if (!hook.isEnabled()) {
            return;
        }
        
        String name = hook.getName();
        Integrator integrator = integrators.get(name);
        
        // this plugin is not an integration
        if (integrator == null) {
            return;
        }
        
        // hook into the plugin if it's enabled in the config
        if ("true".equalsIgnoreCase(plugin.getConfig().getString("hook." + name.toLowerCase()))) {
            Debug.broadcast("Hooking into " + name);
            
            // log important information in case of an error
            try {
                integrator.hook();
                hooked.add(name);
            } catch (Exception e) {
                Debug.error(String.format("There was an error while hooking into %s %s"
                        + " (BetonQuest %s, Spigot %s). Please post it on GitHub <"
                        + "https://github.com/Co0sh/BetonQuest/issues>",
                        name, hook.getDescription().getVersion(),
                        plugin.getDescription().getVersion(), Bukkit.getVersion()));
                e.printStackTrace();
                Debug.error("BetonQuest will work correctly save for that single integration. "
                        + "You can turn it off by setting 'hook." + name.toLowerCase()
                        + "' to false in config.yml file.");
            }
        }
    }
	
	@EventHandler
	public void onPluginEnable(PluginEnableEvent event) {
	    hook(event.getPlugin());
	}

	public Compatibility() {
		instance = this;

		integrators.put("MythicMobs", new MythicMobsIntegrator());
		integrators.put("Citizens", new CitizensIntegrator());
		integrators.put("Vault", new VaultIntegrator());
		integrators.put("Skript", new SkriptIntegrator());
		integrators.put("WorldGuard", new WorldGuardIntegrator());
		integrators.put("WorldEdit", new WorldEditIntegrator());
		integrators.put("mcMMO", new McMMOIntegrator());
		integrators.put("EffectLib", new EffectLibIntegrator());
		integrators.put("PlayerPoints", new PlayerPointsIntegrator());
		integrators.put("Heroes", new HeroesIntegrator());
		integrators.put("Magic", new MagicIntegrator());
		integrators.put("Denizen", new DenizenIntegrator());
		integrators.put("SkillAPI", new SkillAPIIntegrator());
		integrators.put("Quests", new QuestsIntegrator());
		integrators.put("Shopkeepers", new ShopkeepersIntegrator());
		integrators.put("PlaceholderAPI", new PlaceholderAPIIntegrator());
		integrators.put("HolographicDisplays", new HolographicDisplaysIntegrator());
		integrators.put("RacesAndClasses", new RacesAndClassesIntegrator());
		integrators.put("LegendQuest", new LegendQuestIntegrator());
		integrators.put("BetonLangAPI", new BetonLangAPIIntegrator());
		integrators.put("BountifulAPI", new BountifulAPIIntegrator());
		integrators.put("ProtocolLib", new ProtocolLibIntegrator());
		integrators.put("Brewery", new BreweryIntegrator());

		// hook into already enabled plugins in case Bukkit messes up the loading order
		for (Plugin hook : Bukkit.getPluginManager().getPlugins()) {
            hook(hook);
		}
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
		
		// hook into ProtocolLib
		if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")
		        && plugin.getConfig().getString("hook.protocollib").equalsIgnoreCase("true")) {
		    hooked.add("ProtocolLib");
		}
		new BukkitRunnable() {
		    @Override
		    public void run() {
		        // log which plugins have been hooked
		        if (hooked.size() > 0) {
		            StringBuilder string = new StringBuilder();
		            for (String plugin : hooked) {
		                string.append(plugin + ", ");
		            }
		            String plugins = string.substring(0, string.length() - 2);
		            plugin.getLogger().info("Hooked into " + plugins + "!");
		        }
		    }
		}.runTask(plugin);

	}
	
	/**
	 * @return the list of hooked plugins
	 */
	public static List<String> getHooked() {
	    return instance.hooked;
	}
	
	public static void reload() {
	    for (String hooked : getHooked()) {
	        instance.integrators.get(hooked).reload();
	    }
	}
	
	public static void disable() {
        for (String hooked : getHooked()) {
            instance.integrators.get(hooked).close();
        }
	}

}
