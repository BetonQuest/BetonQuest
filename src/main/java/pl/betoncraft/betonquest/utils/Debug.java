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
package pl.betoncraft.betonquest.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;

/**
 * Handels error logging and debugging.
 * 
 * @author Jakub Sapalski
 */
public class Debug {

	private static boolean debugging = false;
	private static Debug instance;
	private Logger logger = BetonQuest.getInstance().getLogger();
	private File debug;
	private File error;

	public Debug() {
		instance = this;
		// if debug option isn't set (yet), then assume it's off
		String configDebug = BetonQuest.getInstance().getConfig().getString("debug");
		if (configDebug == null) {
			configDebug = "true";
		}
		// create logs folder if it doesn't already exist
		File logFolder = new File(BetonQuest.getInstance().getDataFolder(), "logs");
		if (!logFolder.isDirectory()) {
			logFolder.mkdirs();
		}
		// if debugging is set to true then initialize debugger
		if (configDebug.equals("true")) {
			debugging = true;
			debug = new File(logFolder, "debug.log");
			try {
				debug.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			debugging = false;
		}
		// initialize error log
		error = new File(logFolder, "error.log");
		try {
			error.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (debugging) {
			if (debug == null) {
				return;
			}
			try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(debug, true)))) {
				out.println("--------------------[New debug session]--------------------");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// display a message in the console
		if (debugging)
			BetonQuest.getInstance().getLogger().info("Debugging is turned on!");
	}

	private enum LogType {
		DEBUG, ERROR, BROAD
	}

	/**
	 * Checks if the debugging is turned on.
	 * 
	 * @return true if it is turned on
	 */
	public static boolean debugging() {
		return debugging;
	}

	/**
	 * Logs a debug message to "debug.log" file. Only works if "config.debug" is
	 * true. Can be called from an async thread.
	 * 
	 * @param message
	 *            message to log into the file
	 */
	public static void info(String message) {
		if (debugging)
			instance.log(message, LogType.DEBUG);
	}

	/**
	 * Logs an error message to "error.log" (and "debug.log" if debugging is
	 * turned on). Can be called from an async thread.
	 * 
	 * @param message
	 *            error to log into the file
	 */
	public static void error(String message) {
		instance.log(message, LogType.ERROR);
	}

	/**
	 * Sends a message to the console and logs it to "debug.log" if debugging is
	 * turned on
	 * 
	 * @param message
	 *            message to broadcast
	 */
	public static void broadcast(String message) {
		instance.log(message, LogType.BROAD);
	}

	private void log(String message, LogType type) {
		// errors should be displayed in the console at severe level and logged
		// into error.log file
		if (type == LogType.ERROR) {
			logger.severe(message);
			save(message, error, LogType.ERROR);
		}
		// broadcast should only be displayed in the console
		if (type == LogType.BROAD)
			logger.info(message);
		// addictionally everything should be logged to debug.log if debugging
		// is turned on
		if (debugging)
			save(message, debug, type);
	}

	private void save(final String message, final File file, final LogType type) {
		// if the thread isn't primary then it's unsafe to access the file.
		// schedule it to be done on next tick
		if (Bukkit.isPrimaryThread()) {
			sync(message, file, type);
		} else {
			new BukkitRunnable() {
				@Override
				public void run() {
					sync(message, file, type);
				}
			}.runTask(BetonQuest.getInstance());
		}
	}

	private void sync(String message, File file, LogType type) {
		// log it to the file
		String date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
			out.println("[" + date + "] " + type + ": " + message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
