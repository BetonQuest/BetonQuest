package pl.betoncraft.betonquest.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Config.class, LogUtils.class, BetonQuest.class,})
public class UtilsTest {

    public UtilsTest() {
    }

    @Before
    public void setUp() {
        PowerMockito.mockStatic(BetonQuest.class);
        final BetonQuest betonQuestInstance = Mockito.mock(BetonQuest.class);
        PowerMockito.suppress(MemberMatcher.methodsDeclaredIn(JavaPlugin.class));
        PowerMockito.when(BetonQuest.getInstance()).thenReturn(betonQuestInstance);
        PowerMockito.mockStatic(LogUtils.class);
        PowerMockito.when(LogUtils.getLogger()).thenReturn(Logger.getGlobal());
        PowerMockito.mockStatic(Config.class);
        PowerMockito.when(Config.getString("config.journal.lines_per_page")).thenReturn("13");
        PowerMockito.when(Config.getString("config.journal.chars_per_line")).thenReturn("19");

    }

    @Test
    public void testPagesFromString() {
        final String journalText = "&aActive Quest: &aFlint &1wants you to visit the Farm located at 191, 23, -167!";

        final List<String> journalTextFormatted = new ArrayList<>();
        journalTextFormatted.add("&aActive Quest: &aFlint\n" + "&1wants you to visit\n" + "the Farm located at\n" + "191, 23, -167!\n");

        final List<String> journal = Utils.pagesFromString(journalText);
        assertEquals(journalTextFormatted, journal);
    }
}
