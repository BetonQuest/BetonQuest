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
package pl.betoncraft.betonquest;

import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;

public abstract class ID {
	
	protected String id;
	protected ConfigPackage pack;
	protected Instruction instruction;
	protected String rawInstruction;
	
	public ID(ConfigPackage pack, String id) throws ObjectNotFoundException {
		if (id == null || id.length() == 0) {
			throw new ObjectNotFoundException("ID is null");
		}
		if (pack == null) {
			pack = Config.getPackages().get(Config.getDefaultPackage());
		}
		if (id.contains(".")) {
			int dotIndex = id.indexOf('.');
			String packName = id.substring(0, dotIndex);
			this.pack = Config.getPackages().get(packName);
			if (this.pack == null) {
				throw new ObjectNotFoundException("Package not found: " + packName);
			}
			this.id = id.substring(dotIndex + 1);
		} else {
			this.pack = pack;
			this.id = id;
		}
		if (pack == null) {
			throw new ObjectNotFoundException("Package of this object does not exist: " + id);
		}
	}
	
	public ConfigPackage getPackage() {
		return pack;
	}
	
	public String getBaseID() {
		return id;
	}
	
	public String getFullID() {
		return pack.getName() + "." + getBaseID();
	}
	
	@Override
	public String toString() {
		return getFullID();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ID) {
			ID id = (ID) o;
			return id.id.equals(this.id) &&
					id.pack.equals(this.pack);
		}
		return false;
	}
	
	public Instruction generateInstruction() {
		if (rawInstruction == null) {
			return null;
		}
		if (instruction == null) {
			instruction = new Instruction(pack, this, rawInstruction);
		}
		return instruction;
	}

}
