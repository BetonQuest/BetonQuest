package org.betonquest.betonquest.conversation;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * Filters /betonquestanswer commands.
 */
@SuppressWarnings({"PMD.ExcessiveParameterList", "PMD.TooManyMethods"})
public class AnswerFilter implements Filter {
    /**
     * Constructs the /betonquestanswer filter.
     */
    public AnswerFilter() {
    }

    @Override
    public Result filter(@Nullable final LogEvent record) {
        if (record != null && record.getMessage() != null && record.getMessage().getFormattedMessage() != null
                && record.getMessage().getFormattedMessage().contains(" issued server command: /betonquestanswer ")) {
            return Result.DENY;
        }
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object... objects) {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object obj) {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object obj, final Object obj1) {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object obj, final Object obj1, final Object obj2) {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object obj, final Object obj1, final Object obj2, final Object obj3) {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object obj, final Object obj1, final Object obj2, final Object obj3, final Object obj4) {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object obj, final Object obj1, final Object obj2, final Object obj3, final Object obj4, final Object obj5) {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object obj, final Object obj1, final Object obj2, final Object obj3, final Object obj4, final Object obj5, final Object obj6) {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object obj, final Object obj1, final Object obj2, final Object obj3, final Object obj4, final Object obj5, final Object obj6, final Object obj7) {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object obj, final Object obj1, final Object obj2, final Object obj3, final Object obj4, final Object obj5, final Object obj6, final Object obj7, final Object obj8) {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String str, final Object obj, final Object obj1, final Object obj2, final Object obj3, final Object obj4, final Object obj5, final Object obj6, final Object obj7, final Object obj8, final Object obj9) {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final Object obj, final Throwable throwable) {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final Message message, final Throwable throwable) {
        return Result.NEUTRAL;
    }

    @Override
    public Result getOnMatch() {
        return Result.NEUTRAL;
    }

    @Override
    public Result getOnMismatch() {
        return Result.NEUTRAL;
    }

    @Override
    public State getState() {
        return State.STARTED;
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
        return true;
    }

    @Override
    public boolean isStopped() {
        return false;
    }

    @Override
    public boolean stop(final long timeout, final TimeUnit timeUnit) {
        return true;
    }
}
