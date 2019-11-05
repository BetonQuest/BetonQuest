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

package pl.betoncraft.betonquest.utils;

import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * A method of selectinig blocks (pre 1.13 version)
 * <p>
 * Block selector format:
 * MATERIAL:data
 * <p>
 * Where:
 * - MATERIAL - The material type.
 * - data - optional data type. If left out then it will not be compared against
 * <p>
 * Example:
 * - LOG - Test against all LOG's
 * - LOG:1 - Test only against LOG:1
 */
public class BlockSelector {

    private Integer data;
    private Material material;
    private String string;

    public BlockSelector(String string) {
        String materialName;
        this.string = string;

        if (string.contains(":")) {
            String[] parts = string.split(":");
            materialName = parts[0];
            try {
                data = Integer.valueOf(parts[1]);
            } catch (IllegalArgumentException e) {
                LogUtils.getLogger().log(Level.WARNING, "Invalid data type: " + parts[1]);
                LogUtils.logThrowable(e);
            }
        } else {
            materialName = string;
        }

        material = Material.matchMaterial(materialName);
    }

    public void setData(int data) {
        this.data = data;
    }

    public String asString() {
        return string;
    }

    public boolean isValid() {
        return material != null;
    }


    /**
     * Return true if material matches our selector. State is ignored
     */
    public boolean match(Material material) {
        return (material == this.material);
    }

    /**
     * Return true if block matches our selector
     *
     * @param block Block to test
     * @return boolean True if a match occurred
     */
    @SuppressWarnings("deprecation")
    public boolean match(Block block) {
        if (block.getType() != material) {
            return false;
        }

        return data == null || block.getData() == data;
    }

}
