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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

/**
 * Filters /betonquestanswer commands
 *
 * @author Jakub Sapalski
 */
public class AnswerFilter implements Filter {

    public AnswerFilter() {
    }

    @Override
    public Result filter(final LogEvent record) {
        if (record != null && record.getMessage() != null && record.getMessage().getFormattedMessage() != null
                && record.getMessage().getFormattedMessage().contains(" issued server command: /betonquestanswer ")) {
            return Result.DENY;
        }
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String s, final Object... objects) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String s, final Object o) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String s, final Object o, final Object o1) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String s, final Object o, final Object o1, final Object o2) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String s, final Object o, final Object o1, final Object o2, final Object o3) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String s, final Object o, final Object o1, final Object o2, final Object o3, final Object o4) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String s, final Object o, final Object o1, final Object o2, final Object o3, final Object o4, final Object o5) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String s, final Object o, final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String s, final Object o, final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String s, final Object o, final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String s, final Object o, final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final Object o, final Throwable throwable) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final Message message, final Throwable throwable) {
        return null;
    }

    @Override
    public Result getOnMatch() {
        return null;
    }

    @Override
    public Result getOnMismatch() {
        return null;
    }

    @Override
    public State getState() {
        return null;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public boolean isStopped() {
        return false;
    }
}
