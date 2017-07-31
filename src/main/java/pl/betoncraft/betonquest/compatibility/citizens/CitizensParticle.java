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

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.ConditionID;
import pl.betoncraft.betonquest.ObjectNotFoundException;
import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.ArrayList;
import java.util.Set;

/**
 * Displays a particle above NPCs with conversations.
 * 
 * @author Jakub Sapalski
 */
public class CitizensParticle extends BukkitRunnable{

    private ArrayList<NPC> npcs = new ArrayList<>();
    private String name;
    private ConfigurationSection section;
    private static CitizensParticle instance;
    private boolean enabled = false;

    public CitizensParticle() {
        instance = this;
        section = BetonQuest.getInstance().getConfig().getConfigurationSection("effectlib_npc_effect");
        if (section == null)
            return;
        if ("true".equalsIgnoreCase(section.getString("disabled")))
            return;
        name = section.getString("class");
        if (name == null)
            return;
        int delay = section.getInt("delay");
        new BukkitRunnable() {
            @Override
            public void run() {
                for (ConfigPackage pack : Config.getPackages().values()) {
                    String packName = pack.getName();
                    for (String npcID : Config.getPackages().get(packName).getMain().getConfig()
                            .getConfigurationSection("npcs").getKeys(false)) {
                        try {
                            int ID = Integer.parseInt(npcID);
                            npcs.add(CitizensAPI.getNPCRegistry().getById(ID));
                        } catch (NumberFormatException e) {
                        }
                    }
                }
            }
        }.runTaskLater(BetonQuest.getInstance(), 10);
        runTaskTimer(BetonQuest.getInstance(), delay * 20, delay * 20);
        enabled = true;
    }


    @Override
    public void run() {
        for (NPC npc : npcs) {
            if (npc == null) {
                continue;
            }
            Entity e = npc.getEntity();
            if (e == null) {
                continue;
            }
            Location loc = e.getLocation().clone();
            loc.setPitch(-90);

            Compatibility.getEffectManager().start(name, section, loc);

        }


        Config.getPackages().forEach((n, c) -> activateEffects(n, c));
    }

    private void activateEffects(String name, ConfigPackage cfgPack){
        ConfigurationSection cfg = cfgPack.getCustom().getConfig().getConfigurationSection("particles");
        if(cfg == null) return;


        Set<String> keys = cfg.getKeys(false);

        for(String key: keys){
            String npcID = cfg.getString(key + ".NPC");
            if(npcID == null){
                Bukkit.getLogger().warning("Malformed particles for NPC Key: " + key);
                continue;
            }

            NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(npcID));
            if(npc == null) continue;

            Location loc = npc.getStoredLocation().clone();
            loc.setPitch(-90);

            String type = cfg.getString(key + ".type");
            String condition = cfg.getString(key + ".conditions");


            ArrayList<ConditionID> condID = new ArrayList<>();
            try{
                for(String cond: condition.split(","))
                    condID.add(new ConditionID(cfgPack, cond));
            } catch (ObjectNotFoundException e1) {
                continue;
            }

            player: for(Player p: Bukkit.getOnlinePlayers()) {
                for (ConditionID cond : condID)
                    if (!BetonQuest.condition(PlayerConverter.getID(p), cond))
                        continue player;

                ConfigurationSection sec = cfgPack.getCustom().getConfig().getConfigurationSection("effects." + type);

                Compatibility.getEffectManager().start(sec.getString("class"), sec, loc, p);
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
