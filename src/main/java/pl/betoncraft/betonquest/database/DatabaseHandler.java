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
package pl.betoncraft.betonquest.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.core.Journal;
import pl.betoncraft.betonquest.core.Point;
import pl.betoncraft.betonquest.core.Pointer;
import pl.betoncraft.betonquest.core.QuestItem;
import pl.betoncraft.betonquest.database.Database.QueryType;
import pl.betoncraft.betonquest.database.Database.UpdateType;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Represents a handler for all player-related data, which can load and save it.
 * 
 * @author Coosh
 */
public class DatabaseHandler {

    private String playerID;
    private Database database = BetonQuest.getInstance().getDB();

    /**
     * Stores player's tags.
     */
    private List<String> tags = new ArrayList<>();
    /**
     * Temporarily stores player's pointers to journal entries.
     */
    private List<Pointer> entries = new ArrayList<>();
    /**
     * Stores player's Points.
     */
    private List<Point> points = new ArrayList<>();
    /**
     * Temporarily stores player's objective strings, ready to start by
     * startObjectives() method. They are deleted after starting.
     */
    private List<String> objectives = new ArrayList<>();
    /**
     * Stores active Objectives' instances.
     */
    private List<Objective> activeObjectives = new ArrayList<>();
    /**
     * Stores player's journal.
     */
    private Journal journal;
    /**
     * Stores all items in player's backpack
     */
    private List<ItemStack> backpack = new ArrayList<>();

    /**
     * Creates new DatabaseHandler for the player represented by playerID.
     * 
     * @param playerID
     *            - ID of the player
     */
    public DatabaseHandler(String playerID) {
        this.playerID = playerID;
        // load data from the database
        loadAllPlayerData();
    }

    /**
     * Loads all data for the player and puts it in appropriate lists.
     */
    public void loadAllPlayerData() {
        try {
            // open connection to the database
            database.openConnection();

            // load objectives
            ResultSet res1 = database.querySQL(QueryType.SELECT_OBJECTIVES,
                    new String[] { playerID });
            // put them into the list
            while (res1.next())
                objectives.add(res1.getString("instructions"));

            // load tags
            ResultSet res2 = database.querySQL(QueryType.SELECT_TAGS, new String[] { playerID });
            // put them into the list
            while (res2.next())
                tags.add(res2.getString("tag"));

            // load journals
            ResultSet res3 = database.querySQL(QueryType.SELECT_JOURNAL, new String[] { playerID });
            // put them into the list
            while (res3.next())
                entries.add(new Pointer(res3.getString("pointer"), res3.getTimestamp("date")));

            // load points
            ResultSet res4 = database.querySQL(QueryType.SELECT_POINTS, new String[] { playerID });
            // put them into the list
            while (res4.next())
                points.add(new Point(res4.getString("category"), res4.getInt("count")));

            // load backpack
            ResultSet res5 = database
                    .querySQL(QueryType.SELECT_BACKPACK, new String[] { playerID });
            // put items into the list
            while (res5.next()) {
                String instruction = res5.getString("instruction");
                int amount = res5.getInt("amount");
                ItemStack item = Utils.generateItem(new QuestItem(instruction), amount);
                backpack.add(item);
            }

            // everything loaded, close the connection
            if (!BetonQuest.getInstance().isMySQLUsed())
                database.closeConnection();

            // log data to debugger
            if (Debug.debugging()) {
                Debug.info("There are " + objectives.size() + " objectives, " + tags.size()
                    + " tags, " + points.size() + " points, " + entries.size() + " journal entries"
                    + " and " + backpack.size() + " items loaded for player " + playerID);
            }

            // generate journal
            journal = new Journal(playerID, entries);
            // entries.clear();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts all Objectives for this player. The instruction strings are
     * deleted afterwards.
     */
    public void startObjectives() {
        for (String instruction : objectives) {
            BetonQuest.objective(playerID, instruction);
        }
        objectives.clear();
    }

    /**
     * Returns the list of objective strings in this handler
     * 
     * @return list of objective strings
     */
    public List<String> getRawObjectives() {
        return objectives;
    }

    /**
     * Adds an objective to the list.
     * 
     * @param objective
     *            objective to add
     */
    public void addObjective(Objective objective) {
        activeObjectives.add(objective);
    }

    /**
     * Adds objective string to list of objective strings
     * 
     * @param instruction
     *            instruction string to add
     */
    public void addRawObjective(String instruction) {
        objectives.add(instruction);
    }

    /**
     * Saves all data to the database and removes it from handler. This also
     * ends all objectives.
     */
    public void saveData() {
        database.openConnection();
        // delete old data
        database.updateSQL(UpdateType.DELETE_OBJECTIVES, new String[] { playerID });
        database.updateSQL(UpdateType.DELETE_TAGS, new String[] { playerID });
        database.updateSQL(UpdateType.DELETE_JOURNAL, new String[] { playerID });
        database.updateSQL(UpdateType.DELETE_POINTS, new String[] { playerID });
        database.updateSQL(UpdateType.DELETE_BACKPACK, new String[] { playerID });
        // insert new data
        for (String instruction : objectives) {
            database.updateSQL(UpdateType.ADD_OBJECTIVES, new String[] { playerID, instruction });
        }
        for (Objective objective : activeObjectives) {
            database.updateSQL(UpdateType.ADD_OBJECTIVES,
                    new String[] { playerID, objective.getInstructions() });
        }
        for (String tag : tags) {
            database.updateSQL(UpdateType.ADD_TAGS, new String[] { playerID, tag });
        }
        for (Point point : points) {
            database.updateSQL(UpdateType.ADD_POINTS, new String[] { playerID, point.getCategory(),
                String.valueOf(point.getCount()) });
        }
        for (Pointer pointer : journal.getPointers()) {
            database.updateSQL(UpdateType.ADD_JOURNAL,
                    new String[] { playerID, pointer.getPointer(),
                        pointer.getTimestamp().toString() });
        }
        for (ItemStack itemStack : backpack) {
            String instruction = Utils.itemToString(itemStack);
            String amount = String.valueOf(itemStack.getAmount());
            database.updateSQL(UpdateType.ADD_BACKPACK, new String[] { playerID, instruction, amount });
        }
        database.closeConnection();
        // log debug message about saving
        Debug.info("Saved " + (objectives.size() + activeObjectives.size()) + " objectives, "
            + tags.size() + " tags, " + points.size() + " points, "
            + journal.getPointers().size() + " journal entries and " + backpack.size()
            + " items for player " + playerID);
        // clear all lists
        objectives.clear();
        activeObjectives.clear();
        tags.clear();
        points.clear();
        journal.clear();
        backpack.clear();
    }

    /**
     * Returns the List of active Objectives for this player;
     * 
     * @return the List of active Objectives
     */
    public List<Objective> getObjectives() {
        return activeObjectives;
    }

    /**
     * Returns the List of Tags for this player.
     * 
     * @return the List of Tags
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Returns the List of Points for this player.
     * 
     * @return the List of Points
     */
    public List<Point> getPoints() {
        return points;
    }

    /**
     * Creates new Journal instance for this player and populates it with
     * entries.
     * 
     * @return new Journal instance
     */
    public Journal getJournal() {
        return journal;
    }

    /**
     * Purges all player's data from the database and from this handler.
     */
    public void purgePlayer() {
        // clear all lists
        activeObjectives.clear();
        tags.clear();
        points.clear();
        entries.clear();
        journal.clear();
        backpack.clear();
        // clear the database
        if (BetonQuest.getInstance().isMySQLUsed()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    database.openConnection();
                    database.updateSQL(UpdateType.DELETE_OBJECTIVES, new String[] { playerID });
                    database.updateSQL(UpdateType.DELETE_JOURNAL, new String[] { playerID });
                    database.updateSQL(UpdateType.DELETE_POINTS, new String[] { playerID });
                    database.updateSQL(UpdateType.DELETE_TAGS, new String[] { playerID });
                    database.updateSQL(UpdateType.DELETE_BACKPACK, new String[] { playerID });
                }
            }.runTask(BetonQuest.getInstance());
        } else {
            database.openConnection();
            database.updateSQL(UpdateType.DELETE_OBJECTIVES, new String[] { playerID });
            database.updateSQL(UpdateType.DELETE_JOURNAL, new String[] { playerID });
            database.updateSQL(UpdateType.DELETE_POINTS, new String[] { playerID });
            database.updateSQL(UpdateType.DELETE_TAGS, new String[] { playerID });
            database.updateSQL(UpdateType.DELETE_BACKPACK, new String[] { playerID });
            database.closeConnection();
        }
        // update the journal so it's empty
        if (PlayerConverter.getPlayer(playerID) != null) {
            journal.updateJournal();
        }
    }

    /**
     * Checks if the player has specified tag.
     * 
     * @param tag
     *            tag to check
     * @return true if the player has this tag
     */
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    /**
     * Adds the specified tag to player's list. It won't double it however.
     * 
     * @param tag
     *            tag to add
     */
    public void addTag(String tag) {
        if (!tags.contains(tag))
            tags.add(tag);
    }

    /**
     * Removes the specified tag from player's list. If there is no tag, nothing
     * happens.
     * 
     * @param tag
     *            tag to remove
     */
    public void removeTag(String tag) {
        tags.remove(tag);
    }

    /**
     * Deletes all the objectives with the specified tag.
     * 
     * @param tag
     *            objective's tag
     */
    public void deleteObjective(String tag) {
        // search active objectives
        for (Iterator<Objective> iterator = activeObjectives.iterator(); iterator.hasNext();) {
            Objective objective = iterator.next();
            // if it matches then delete the objective and remove it from list
            if (objective.getTag().equalsIgnoreCase(tag)) {
                objective.getInstructions();
                iterator.remove();
            }
        }
        // search inactive objectives
        for (Iterator<String> iterator = objectives.iterator(); iterator.hasNext();) {
            String instruction = iterator.next();
            String[] parts = instruction.split(" ");
            for (String part : parts) {
                // if it matches then remove it from list
                if (part.matches("tag:"))
                    iterator.remove();
            }
        }
    }

    /**
     * Adds points to specified category. If there is no such category it will
     * be created.
     * 
     * @param category
     *            points will be added to this category
     * @param count
     *            how much points will be added (or subtracted)
     */
    public void addPoints(String category, int count) {
        // check if the category already exists
        for (Point point : points) {
            if (point.getCategory().equalsIgnoreCase(category)) {
                // if it does, add points to it
                point.addPoints(count);
                return;
            }
        }
        // if not then create new point category with given amount of points
        points.add(new Point(category, count));
    }

    /**
     * Returns player's backpack as the list of itemstacks.
     * 
     * @return list of itemstacks
     */
    public List<ItemStack> getBackpack() {
        return backpack;
    }
    
    public void setBackpack(List<ItemStack> list) {
        this.backpack = list;
    }

    /**
     * Adds the item to backpack. The amount of the itemstack doesn't matter,
     * it's overwritten by amount parameter.
     * 
     * @param item
     *            ItemStack to add to backpack
     * @param amount
     *            amount of the items
     */
    public void addItem(ItemStack item, int amount) {
        Debug.info("  Cloned item type: " + item.getType());
        for (ItemStack itemStack : backpack) {
            if (item.isSimilar(itemStack)) {
                // if items are similar they can be joined in a single itemstack
                if (amount + itemStack.getAmount() <= itemStack.getMaxStackSize()) {
                    // if they will fit all together, then just add them
                    itemStack.setAmount(itemStack.getAmount() + amount);
                    return;
                } else {
                    // if the stack will be overflown, set max size and continue
                    amount -= itemStack.getMaxStackSize() - itemStack.getAmount();
                    itemStack.setAmount(itemStack.getMaxStackSize());
                }
            }
        }
        // every item checked, time to add a new itemstack
        while (amount > 0) {
            // if the amount is greater than max size of the itemstack, create max
            // stacks until it's lower
            ItemStack newItem = item.clone();
            int maxSize = newItem.getType().getMaxStackSize();
            if (amount > maxSize) {
                if (maxSize == 0) {
                    maxSize = 64;
                }
                newItem.setAmount(maxSize);
                amount -= maxSize;
            } else {
                newItem.setAmount(amount);
                amount = 0;
            }
            Debug.info("    Adding item of type " + newItem.getType() + ", amount left to ad is " + amount);
            backpack.add(newItem);
        }
    }
}
