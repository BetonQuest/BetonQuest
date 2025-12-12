package org.betonquest.betonquest.api.logger;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

/**
 * This facade uses the {@link PluginLogger} from the {@link Plugin#getLogger()} method.
 * It registers a new child logger for each class it's used in.
 * <p>
 * Therefore, all of BetonQuest's logging needs to be done with the methods of this class.
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface BetonQuestLogger {

    /**
     * Logs a debug message with the {@link Level#FINE} level to the log.
     * <p>
     * Use this for additional debug log information.
     * <p>
     * If you can provide an exception use {@link BetonQuestLogger#debug(String, Throwable)} instead.
     * <p>
     * Don't use this method, if you can provide a {@link QuestPackage}.
     * Use {@link BetonQuestLogger#debug(QuestPackage, String)} instead.
     *
     * @param msg The message to log.
     */
    void debug(@Nullable String msg);

    /**
     * Logs a debug message with the {@link Level#FINE} level to the log.
     * The {@link Throwable} is logged together with the message.
     * <p>
     * Use this for additional debug log information.
     * <p>
     * If you cannot provide an exception use {@link BetonQuestLogger#debug(String)} instead.
     * <p>
     * Don't use this method, if you can provide a {@link QuestPackage}.
     * Use {@link BetonQuestLogger#debug(QuestPackage, String, Throwable)} instead.
     *
     * @param msg    The message to log.
     * @param thrown The throwable to log.
     */
    void debug(@Nullable String msg, Throwable thrown);

    /**
     * Logs a debug message with the {@link Level#FINE} level to the log.
     * <p>
     * Use this for additional debug log information.
     * <p>
     * If you can provide an exception use {@link BetonQuestLogger#debug(QuestPackage, String, Throwable)} instead.
     *
     * @param pack The related {@link QuestPackage} or null.
     * @param msg  The message to log.
     */
    void debug(@Nullable QuestPackage pack, @Nullable String msg);

    /**
     * Logs a debug message with the {@link Level#FINE} level to the log.
     * The {@link Throwable} is logged together with the message.
     * <p>
     * Use this for additional debug log information.
     * <p>
     * If you cannot provide an exception use {@link BetonQuestLogger#debug(QuestPackage, String)} instead.
     *
     * @param pack   The related {@link QuestPackage} or null.
     * @param msg    The message to log.
     * @param thrown The throwable to log.
     */
    void debug(@Nullable QuestPackage pack, @Nullable String msg, Throwable thrown);

    /**
     * Logs a normal message with the {@link Level#INFO} level to the log.
     * <p>
     * Use this for normal log information.
     * <p>
     * Don't use this method, if you can provide a {@link QuestPackage}.
     * Use {@link BetonQuestLogger#info(QuestPackage, String)} instead.
     *
     * @param msg The message to log.
     */
    void info(String msg);

    /**
     * Logs a normal message with the {@link Level#INFO} level to the log.
     * <p>
     * Use this for normal log information.
     *
     * @param pack The related {@link QuestPackage} or null.
     * @param msg  The message to log.
     */
    void info(@Nullable QuestPackage pack, @Nullable String msg);

    /**
     * Logs a warning message with the {@link Level#WARNING} level to the log.
     * <p>
     * Use this if you can provide useful information how to fix the underlying problem.
     * <p>
     * If you can provide an exception use {@link BetonQuestLogger#warn(String, Throwable)} instead.
     * <p>
     * Don't use this method, if you can provide a {@link QuestPackage}.
     * Use {@link BetonQuestLogger#warn(QuestPackage, String)} instead.
     *
     * @param msg The message to log.
     */
    void warn(@Nullable String msg);

    /**
     * Logs a warning message with the {@link Level#WARNING} level to the log.
     * The {@link Throwable} is logged with the {@link Level#FINE} level to the log.
     * <p>
     * Use this if you can provide useful information how to fix the underlying problem.
     * <p>
     * If you cannot provide an exception use {@link BetonQuestLogger#warn(String)} instead.
     * <p>
     * Don't use this method, if you can provide a {@link QuestPackage}.
     * Use {@link BetonQuestLogger#warn(QuestPackage, String, Throwable)} instead.
     *
     * @param msg    The message to log.
     * @param thrown The throwable to log.
     */
    void warn(@Nullable String msg, Throwable thrown);

    /**
     * Logs a warning message with the {@link Level#WARNING} level to the log.
     * <p>
     * Use this if you can provide useful information how to fix the underlying problem.
     * <p>
     * If you can provide an exception use {@link BetonQuestLogger#warn(QuestPackage, String, Throwable)} instead.
     *
     * @param pack The related {@link QuestPackage} or null.
     * @param msg  The message to log.
     */
    void warn(@Nullable QuestPackage pack, @Nullable String msg);

    /**
     * Logs a warning message with the {@link Level#WARNING} level to the log.
     * The {@link Throwable} is logged with the {@link Level#FINE} level to the log.
     * <p>
     * Use this if you can provide useful information how to fix the underlying problem.
     * <p>
     * If you cannot provide an exception use {@link BetonQuestLogger#warn(QuestPackage, String)} instead.
     *
     * @param pack   The related {@link QuestPackage} or null.
     * @param msg    The message to log.
     * @param thrown The throwable to log.
     */
    void warn(@Nullable QuestPackage pack, @Nullable String msg, Throwable thrown);

    /**
     * Logs an error message with the {@link Level#SEVERE} level to the log.
     * <p>
     * Use this if the underlying problem affects the servers security or functionality.
     * Usage is also allowed if you don't know how the user can fix the underlying issue.
     * <p>
     * If you can provide an exception use {@link BetonQuestLogger#error(String, Throwable)} instead.
     * <p>
     * Don't use this method, if you can provide a {@link QuestPackage}.
     * Use {@link BetonQuestLogger#error(QuestPackage, String)} instead.
     *
     * @param msg The message to log.
     */
    void error(@Nullable String msg);

    /**
     * Logs an error message with the {@link Level#SEVERE} level to the log.
     * The {@link Throwable} is logged together with the message.
     * <p>
     * Use this if the underlying problem affects the servers security or functionality.
     * Usage is also allowed if you don't know how the user can fix the underlying issue.
     * <p>
     * If you cannot provide an exception use {@link BetonQuestLogger#error(String)} instead.
     * <p>
     * Don't use this method, if you can provide a {@link QuestPackage}.
     * Use {@link BetonQuestLogger#error(QuestPackage, String, Throwable)} instead.
     *
     * @param msg    The message to log.
     * @param thrown The throwable to log.
     */
    void error(@Nullable String msg, Throwable thrown);

    /**
     * Logs an error message with the {@link Level#SEVERE} level to the log.
     * <p>
     * Use this if the underlying problem affects the servers security or functionality.
     * Usage is also allowed if you don't know how the user can fix the underlying issue.
     * <p>
     * If you can provide an exception use {@link BetonQuestLogger#error(QuestPackage, String, Throwable)} instead.
     *
     * @param pack The related {@link QuestPackage} or null.
     * @param msg  The message to log.
     */
    void error(@Nullable QuestPackage pack, @Nullable String msg);

    /**
     * Logs an error message with the {@link Level#SEVERE} level to the log.
     * The {@link Throwable} is logged together with the message.
     * <p>
     * Use this if the underlying problem affects the servers security or functionality.
     * Usage is also allowed if you don't know how the user can fix the underlying issue.
     * <p>
     * If you cannot provide an exception use {@link BetonQuestLogger#error(QuestPackage, String)} instead.
     *
     * @param pack   The related {@link QuestPackage} or null.
     * @param msg    The message to log.
     * @param thrown The throwable to log.
     */
    void error(@Nullable QuestPackage pack, @Nullable String msg, Throwable thrown);

    /**
     * Logs a {@link Throwable} with the {@link Level#SEVERE} level to the log.
     * The Throwable is logged together with a message that informs the user that the error
     * needs to be reported to the issue tracker.
     * <p>
     * Only use this in cases that should never occur and indicate an error that must be reported.
     * <p>
     * Don't use this method, if you can provide a {@link QuestPackage}.
     * Use {@link BetonQuestLogger#reportException(QuestPackage, Throwable)} instead.
     *
     * @param thrown The throwable to log.
     */
    void reportException(Throwable thrown);

    /**
     * Logs a {@link Throwable} with the {@link Level#SEVERE} level to the log.
     * The Throwable is logged together with a message that informs the user that the error
     * needs to be reported to the issue tracker.
     * <p>
     * Only use this in cases that should never occur and indicate an error that must be reported.
     *
     * @param pack   The related {@link QuestPackage} or null.
     * @param thrown The throwable to log.
     */
    void reportException(@Nullable QuestPackage pack, Throwable thrown);
}
