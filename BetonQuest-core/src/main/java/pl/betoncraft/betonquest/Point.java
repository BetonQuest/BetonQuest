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
package pl.betoncraft.betonquest;

/**
 * Represents the point category.
 *
 * @author Jakub Sapalski
 */
public class Point {

    /**
     * Category of these points.
     */
    private String category;
    /**
     * Amount of these points.
     */
    private int count;

    /**
     * Creates new instance of the Point object.
     *
     * @param category category of these points
     * @param count    amount of these points
     */
    public Point(String category, int count) {
        this.category = category;
        this.count = count;
    }

    /**
     * Returns the category of these points
     *
     * @return the category of these points
     */
    public String getCategory() {
        return category;
    }

    /**
     * Returns the amount of these points
     *
     * @return the count of these points
     */
    public int getCount() {
        return count;
    }

    /**
     * Adds points in this category
     *
     * @param add amount of the points to add
     */
    public void addPoints(int add) {
        this.count = this.count + add;
    }
}
