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
package pl.betoncraft.betonquest.database;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.QuestDataUpdateEvent;
import pl.betoncraft.betonquest.database.Connector.UpdateType;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

/**
 * Saves the data to the database asynchronously
 *
 * @author Jakub Sapalski
 */
public class Saver extends Thread implements Listener {

    Connector con;
    ConcurrentLinkedQueue<Record> queue;
    boolean run;
    boolean active;

    /**
     * Creates new database saver thread
     */
    public Saver() {
        this.con = new Connector();
        this.queue = new ConcurrentLinkedQueue<>();
        this.run = true;
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    public void run() {
        while (true) {
            while (queue.isEmpty()) {
                if (!run) {
                    return;
                }
                synchronized (this) {
                    try {
                        active = false;
                        wait();
                    } catch (InterruptedException e) {
                        LogUtils.getLogger().log(Level.SEVERE, "There was a exception with SQL");
                        LogUtils.logThrowable(e);
                    }
                }
            }
            if (!active) {
                con.refresh();
                active = true;
            }
            Record rec = queue.poll();
            con.updateSQL(rec.getType(), rec.getArgs());
        }
    }

    /**
     * Adds new record to the queue, where it will be saved to the database.
     *
     * @param rec Record to save
     */
    public synchronized void add(Record rec) {
        queue.add(rec);
        notify();
    }

    /**
     * Ends this saver's job, letting it save all remaining data.
     */
    public synchronized void end() {
        run = false;
        notify();
    }

    @EventHandler(ignoreCancelled = true)
    public void onDataUpdate(QuestDataUpdateEvent e) {
        add(new Record(UpdateType.REMOVE_OBJECTIVES, new String[]{e.getPlayerID(), e.getObjID()}));
        add(new Record(UpdateType.ADD_OBJECTIVES, new String[]{e.getPlayerID(), e.getObjID(), e.getData()}));
    }

    /**
     * Holds the data and the method of saving them to the database
     *
     * @author Jakub Sapalski
     */
    public static class Record {

        private UpdateType type;
        private String[] args;

        /**
         * Creates new Record, which can be saved to the database using
         * Saver.add()
         *
         * @param type method used for saving the data
         * @param args list of Strings which will be saved to the database
         */
        public Record(UpdateType type, String[] args) {
            this.type = type;
            this.args = args;
        }

        private UpdateType getType() {
            return type;
        }

        private String[] getArgs() {
            return args;
        }
    }

}
