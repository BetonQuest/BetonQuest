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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.core.InstructionParseException;
import pl.betoncraft.betonquest.core.Journal;
import pl.betoncraft.betonquest.core.Point;
import pl.betoncraft.betonquest.core.Pointer;
import pl.betoncraft.betonquest.core.QuestItem;
import pl.betoncraft.betonquest.database.Connector.QueryType;
import pl.betoncraft.betonquest.database.Connector.UpdateType;
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
    private HashMap<String, String> objectives = new HashMap<>();
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
            Connector database = new Connector();

            // load objectives
            ResultSet res1 = database.querySQL(QueryType.SELECT_OBJECTIVES,
                    new String[] { playerID });
            // put them into the list
            while (res1.next())
                objectives.put(res1.getString("objective"), res1.getString("instructions"));

            // load tags
            ResultSet res2 = database.querySQL(QueryType.SELECT_TAGS, new String[] { playerID });
            // put them into the list
            while (res2.next())
                tags.add(res2.getString("tag"));

            // load journals
            ResultSet res3 = database.querySQL(QueryType.SELECT_JOURNAL, new String[] { playerID });
            // put them into the list
            while (res3.next())
                entries.add(new Pointer(res3.getString("pointer"), res3.getTimestamp("date").getTime()));

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
                ItemStack item;
                try {
                    item = new QuestItem(instruction).generateItem(amount);
                } catch (InstructionParseException e) {
                    Debug.error("Could not load item: " + e.getCause().getMessage());
                    continue;
                }
                backpack.add(item);
            }

            // log data to debugger
            if (Debug.debugging()) {
                Debug.info("There are " + objectives.size() + " objectives, " + tags.size()
                    + " tags, " + points.size() + " points, " + entries.size() + " journal entries"
                    + " and " + backpack.size() + " items loaded for player " + PlayerConverter.getName(playerID));
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
        for (String objective : objectives.keySet()) {
            BetonQuest.resumeObjective(playerID, objective, objectives.get(objective));
        }
        objectives.clear();
    }

    /**
     * Saves all data to the database. It does not remove the data, you need to
     * use {@link #removeData() removeDate} for that.
     */
    public void saveData() {
        Connector con = new Connector();
        // delete old data
        con.updateSQL(UpdateType.DELETE_OBJECTIVES, new String[] { playerID });
        con.updateSQL(UpdateType.DELETE_TAGS, new String[] { playerID });
        con.updateSQL(UpdateType.DELETE_JOURNAL, new String[] { playerID });
        con.updateSQL(UpdateType.DELETE_POINTS, new String[] { playerID });
        con.updateSQL(UpdateType.DELETE_BACKPACK, new String[] { playerID });
        // insert new data
        for (String objective : new HashMap<>(objectives).keySet()) {
            con.updateSQL(UpdateType.ADD_OBJECTIVES, new String[] { playerID, objective, objectives.get(objective) });
        }
        ArrayList<Objective> objectivesList = getObjectives();
        for (Objective objective : new ArrayList<>(objectivesList)) {
            con.updateSQL(UpdateType.ADD_OBJECTIVES,
                    new String[] { playerID, objective.getLabel(), objective.getData(playerID) });
        }
        for (String tag : new ArrayList<>(tags)) {
            con.updateSQL(UpdateType.ADD_TAGS, new String[] { playerID, tag });
        }
        for (Point point : new ArrayList<>(points)) {
            con.updateSQL(UpdateType.ADD_POINTS, new String[] { playerID, point.getCategory(),
                String.valueOf(point.getCount()) });
        }
        for (Pointer pointer : new ArrayList<>(journal.getPointers())) {
            con.updateSQL(UpdateType.ADD_JOURNAL,
                    new String[] { playerID, pointer.getPointer(),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(pointer.getTimestamp())) });
        }
        for (ItemStack itemStack : new ArrayList<>(backpack)) {
            String instruction = Utils.itemToString(itemStack);
            String amount = String.valueOf(itemStack.getAmount());
            con.updateSQL(UpdateType.ADD_BACKPACK, new String[] { playerID, instruction, amount });
        }
        // log debug message about saving
        Debug.info("Saved " + (objectives.size() + objectivesList.size()) + " objectives, "
            + tags.size() + " tags, " + points.size() + " points, "
            + journal.getPointers().size() + " journal entries and " + backpack.size()
            + " items for player " + PlayerConverter.getName(playerID));
    }
    
    /**
     * Removes the data of the player. It does not save anything, you need to
     * use {@link #saveData() saveData} for that.
     */
    public void removeData() {
        // unregister objectives
        for (Objective objective : getObjectives()) {
            objective.removePlayer(playerID);;
        }
        // clear all lists
        objectives.clear();
        tags.clear();
        points.clear();
        journal.clear();
        backpack.clear();
    }
    
    public HashMap<String, String> getRawObjectives() {
        return objectives;
    }
    
    /**
     * Adds new objective to a list of not initialized objectives,
     * ready to be started or saved to the database.
     * 
     * @param objectiveID
     *          ID of the objective
     */
    public void addNewRawObjective(String objectiveID) {
        String data = BetonQuest.getInstance().getObjective(objectiveID)
                .getDefaultDataInstruction();
        addRawObjective(objectiveID, data);
    }
    
    /**
     * Adds objective to a list of not initialized objectives,
     * ready to be started or saved to the database.
     * 
     * @param objectiveID
     *          ID of the objective
     * @param data
     *          data instruction string to use
     */
    public void addRawObjective(String objectiveID, String data) {
        objectives.put(objectiveID, data);
    }

    /**
     * Returns the List of active Objectives for this player;
     * 
     * @return the List of active Objectives
     */
    public ArrayList<Objective> getObjectives() {
        return BetonQuest.getInstance().getPlayerObjectives(playerID);
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
        for (Objective obj : getObjectives()) {
            obj.removePlayer(playerID);
        }
        // clear all lists
        objectives.clear();
        tags.clear();
        points.clear();
        entries.clear();
        journal.clear();
        backpack.clear();
        // clear the database
        Connector database = new Connector();
        database.updateSQL(UpdateType.DELETE_OBJECTIVES, new String[] { playerID });
        database.updateSQL(UpdateType.DELETE_JOURNAL, new String[] { playerID });
        database.updateSQL(UpdateType.DELETE_POINTS, new String[] { playerID });
        database.updateSQL(UpdateType.DELETE_TAGS, new String[] { playerID });
        database.updateSQL(UpdateType.DELETE_BACKPACK, new String[] { playerID });
        // update the journal so it's empty
        if (PlayerConverter.getPlayer(playerID) != null) {
            journal.updateJournal();
        }
    }

    /**
     * Checks if the player has specified tag.
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
    public void deleteObjective(String label) {
        // search active objectives
        for (Objective objective : getObjectives()) {
            if (objective.getLabel().equalsIgnoreCase(label)) {
                objective.removePlayer(playerID);
            }
        }
        // search inactive objectives
        objectives.remove(label);
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
    
    public void removePointsCategory(String category) {
        Point pointToRemove = null;
        for (Point point : points) {
            if (point.getCategory().equalsIgnoreCase(category)) {
                pointToRemove = point;
            }
        }
        if (pointToRemove != null) {
            points.remove(pointToRemove);
        }
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
            Debug.info("    Adding item of type " + newItem.getType()
                    + ", amount left to ad is " + amount);
            backpack.add(newItem);
        }
    }
    
    /**
     * Cancels the quest by removing all defined tags, objectives
     * 
     * @param name
     */
    public void cancelQuest(String name) {
        Debug.info("Canceling the quest " + name + " for player " + PlayerConverter.getName(playerID));
        // get the instruction
        String[] parts = name.split("\\.");
        String packName = parts[0];
        String cancelerName = parts[1];
        String instruction = Config.getPackage(packName).getString("main.cancel." + cancelerName);
        String[] events     = null,
                 tags       = null,
                 objectives = null,
                 points     = null,
                 journal    = null,
                 loc        = null;
        String questName = null;
        // parse it to get the data
        for (String part : instruction.split(" ")) {
            if (part.startsWith("name:")) {
                questName = part.substring(5).replace("_", " ");
            } else if (part.startsWith("events:")) {
                events = part.substring(7).split(",");
            } else if (part.startsWith("tags:")) {
                tags = part.substring(5).split(",");
            } else if (part.startsWith("objectives:")) {
                objectives = part.substring(11).split(",");
            } else if (part.startsWith("points:")) {
                points = part.substring(7).split(",");
            } else if (part.startsWith("journal:")) {
                journal = part.substring(8).split(",");
            } else if (part.startsWith("loc:")) {
                loc = part.substring(4).split(";");
            }
        }
        // remove tags, points, objectives and journals
        if (tags != null) {
            for (String tag : tags) {
                Debug.info("  Removing tag " + tag);
                removeTag(tag);
            }
        }
        if (points != null) {
            for (String point : points) {
                Debug.info("  Removing points " + point);
                removePointsCategory(point);
            }
        }
        if (objectives != null) {
            for (String obj : objectives) {
                Debug.info("  Removing objective " + obj);
                deleteObjective(obj);
            }
        }
        if (journal != null) {
            Journal j = getJournal();
            for (String entry : journal) {
                Debug.info("  Removing entry " + entry);
                String pointer;
                if (entry.contains(".")) {
                    pointer = entry;
                } else {
                    pointer = packName + "." + entry;
                }
                j.removePointer(pointer);
            }
            j.updateJournal();
        }
        // teleport player to the location
        if (loc != null) {
            if (loc.length != 4 && loc.length != 6) {
                Debug.error("Wrong location format in quest canceler " + name);
                return;
            }
            double x, y, z;
            try {
                x = Double.parseDouble(loc[0]);
                y = Double.parseDouble(loc[1]);
                z = Double.parseDouble(loc[2]);
            } catch (NumberFormatException e) {
                Debug.error("Could not parse location in quest canceler " + name);
                return;
            }
            World world = Bukkit.getWorld(loc[3]);
            if (world == null) {
                Debug.error("The world doesn't exist in quest canceler " + name);
                return;
            }
            float yaw = 0, pitch = 0;
            if (loc.length == 6) {
                try {
                    yaw = Float.parseFloat(loc[4]);
                    pitch = Float.parseFloat(loc[5]);
                } catch (NumberFormatException e) {
                    Debug.error("Could not parse yaw/pitch in quest canceler " + name
                        + ", setting to 0");
                    yaw = 0;
                    pitch = 0;
                }
            }
            Debug.info("  Teleporting to new location");
            Location location = new Location(world, x, y, z, yaw, pitch);
            PlayerConverter.getPlayer(playerID).teleport(location);
        }
        // fire all events
        if (events != null) {
            for (String event : events) {
                if (!event.contains(".")) {
                    event = packName + "." + event;
                }
                BetonQuest.event(playerID, event);
            }
        }
        // done
        Debug.info("Quest removed!");
        PlayerConverter.getPlayer(playerID).sendMessage(Config.getMessage("quest_canceled").replace("&", "§").replace("%quest%", questName));
    }
}
