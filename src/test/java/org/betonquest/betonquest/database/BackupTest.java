package org.betonquest.betonquest.database;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.config.DefaultConfigAccessorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests the backup class.
 */
class BackupTest {

    @SuppressWarnings("PMD.CloseResource")
    @Test
    void enum_existence(@TempDir final Path tempDir) throws SQLException {
        final Connector connector = mock(Connector.class);
        final Backup backup = new Backup(mock(BetonQuestLogger.class),
                new DefaultConfigAccessorFactory(mock(BetonQuestLoggerFactory.class), mock(BetonQuestLogger.class)),
                tempDir.toFile(), connector);
        final ResultSet emptyResult = mock(ResultSet.class);
        when(connector.querySQL(any())).thenReturn(emptyResult);
        final ResultSetMetaData metaData = mock(ResultSetMetaData.class);
        when(emptyResult.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(0);
        assertDoesNotThrow(backup::backupDatabase, "The backup should not throw!");
    }
}
