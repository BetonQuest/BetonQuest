/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
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
package pl.betoncraft.betonquest.inout;

import org.bukkit.Location;

/**
 * 
 * @author Co0sh
 */
public class UnifiedLocation {
	
	private final double x;
	private final double y;
	private final double z;
	private final String world;

	public UnifiedLocation(Location location) {
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.world = location.getWorld().getName();
	}
	
	public UnifiedLocation(double x, double y, double z, String world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}

	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return the z
	 */
	public double getZ() {
		return z;
	}

	/**
	 * @return the world
	 */
	public String getWorld() {
		return world;
	}
}
