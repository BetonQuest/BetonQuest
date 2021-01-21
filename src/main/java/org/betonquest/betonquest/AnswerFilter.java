package org.betonquest.betonquest;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

/**
 * Filters /betonquestanswer commands
 */
@SuppressWarnings({"PMD.ExcessiveParameterList", "PMD.TooManyMethods", "PMD.CommentRequired"})
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
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object... objects) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object obj) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object obj, final Object obj1) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object obj, final Object obj1, final Object obj2) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object obj, final Object obj1, final Object obj2, final Object obj3) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object obj, final Object obj1, final Object obj2, final Object obj3, final Object obj4) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object obj, final Object obj1, final Object obj2, final Object obj3, final Object obj4, final Object obj5) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object obj, final Object obj1, final Object obj2, final Object obj3, final Object obj4, final Object obj5, final Object obj6) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object obj, final Object obj1, final Object obj2, final Object obj3, final Object obj4, final Object obj5, final Object obj6, final Object obj7) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object obj, final Object obj1, final Object obj2, final Object obj3, final Object obj4, final Object obj5, final Object obj6, final Object obj7, final Object obj8) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object obj, final Object obj1, final Object obj2, final Object obj3, final Object obj4, final Object obj5, final Object obj6, final Object obj7, final Object obj8, final Object obj9) {
        return null;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final Object obj, final Throwable throwable) {
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
        // Empty
    }

    @Override
    public void start() {
        // Empty
    }

    @Override
    public void stop() {
        // Empty
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
