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

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.compatibility.Integrator;
import pl.betoncraft.betonquest.compatibility.protocollib.NPCHider;
import pl.betoncraft.betonquest.compatibility.protocollib.UpdateVisibilityNowEvent;

import java.util.Arrays;


public class CitizensIntegrator implements Integrator {

    private BetonQuest plugin;

    public CitizensIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        new CitizensListener();
        new CitizensWalkingListener();
        if (Compatibility.getHooked().contains("EffectLib"))
            new CitizensParticle();

        // if HolographicAPI is hooked, start CitizensHologram
        if (Compatibility.getHooked().contains("HolographicDisplays")) {
            new CitizensHologram();
        }

        // if ProtocolLib is hooked, start NPCHider
        if (Compatibility.getHooked().contains("ProtocolLib")) {
            NPCHider.start();
            plugin.registerEvents("updatevisibility", UpdateVisibilityNowEvent.class);
        }
        plugin.registerObjectives("npckill", NPCKillObjective.class);
        plugin.registerObjectives("npcinteract", NPCInteractObjective.class);
        plugin.registerObjectives("npcrange", NPCRangeObjective.class);
        plugin.registerEvents("movenpc", NPCMoveEvent.class);
        plugin.registerConversationIO("chest", CitizensInventoryConvIO.class);
        plugin.registerConversationIO("combined", CitizensInventoryConvIO.CitizensCombined.class);
        plugin.registerVariable("citizen", CitizensVariable.class);
        plugin.registerConditions("npcdistance", NPCDistanceCondition.class);
        plugin.registerConditions("npclocation", NPCLocationCondition.class);
        if (Compatibility.getHooked().contains("WorldGuard")) {
            plugin.registerConditions("npcregion", NPCRegionCondition.class);
        }
    }

    @Override
    public void reload() {
        if (Compatibility.getHooked().containsAll(Arrays.asList("Citizens", "EffectLib"))) {
            CitizensParticle.reload();
        }

        if (Compatibility.getHooked().containsAll(Arrays.asList("Citizens", "HolographicDisplays"))) {
            CitizensHologram.reload();
        }
    }

    @Override
    public void close() {

    }

}
