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

package pl.betoncraft.betonquest.compatibility.protocollib.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.primitives.Ints;

import java.util.List;

public class WrapperPlayServerEntityDestroy extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_DESTROY;

    public WrapperPlayServerEntityDestroy() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerEntityDestroy(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve the IDs of the entities that will be destroyed.
     *
     * @return The current entities.
     */
    public List<Integer> getEntities() {
        return Ints.asList(handle.getIntegerArrays().read(0));
    }

    /**
     * Set the entities that will be destroyed.
     */
    public void setEntities(List<Integer> entities) {
        setEntities(Ints.toArray(entities));
    }

    /**
     * Set the entities that will be destroyed.
     */
    public void setEntities(int[] entities) {
        handle.getIntegerArrays().write(0, entities);
    }
}

