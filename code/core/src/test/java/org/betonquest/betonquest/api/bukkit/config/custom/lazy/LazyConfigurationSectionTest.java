package org.betonquest.betonquest.api.bukkit.config.custom.lazy;

import org.betonquest.betonquest.api.bukkit.config.util.ConfigurationSectionBaseTest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.JUnit5TestShouldBePackagePrivate", "PMD.UnitTestAssertionsShouldIncludeMessage"})
class LazyConfigurationSectionTest extends ConfigurationSectionBaseTest {
    private MemoryConfiguration root;

    @Override
    public ConfigurationSection getConfig() {
        root = new MemoryConfiguration();
        final LazyConfigurationSection lazy = new LazyConfigurationSection(root, "lazy");
        root.set("lazy", lazy);
        final Configuration defaultConfig = super.getDefaultConfig();
        defaultConfig.getValues(true).forEach((key, value) -> {
            if (!(value instanceof ConfigurationSection)) {
                lazy.set(key, value);
                lazy.setComments(key, defaultConfig.getComments(key));
                lazy.setInlineComments(key, defaultConfig.getInlineComments(key));
            }
        });
        defaultConfig.getDefaults().getValues(true).forEach((key, value) -> {
            if (!(value instanceof ConfigurationSection)) {
                lazy.addDefault(key, value);
                final ConfigurationSection defaultSection = lazy.getDefaultSection();
                if (defaultSection != null) {
                    defaultSection.setComments(key, defaultConfig.getComments(key));
                    defaultSection.setInlineComments(key, defaultConfig.getInlineComments(key));
                }
            }
        });
        return lazy;
    }

    @Test
    @Override
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void testGetName() {
        assertEquals("lazy", config.getName());
        final ConfigurationSection nestedChild = config.getConfigurationSection("childSection.nestedChildSection");
        assertNotNull(nestedChild);
        assertEquals("nestedChildSection", nestedChild.getName());
    }

    @Test
    @Override
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void testGetCurrentPath() {
        assertEquals("lazy", config.getCurrentPath());
        final ConfigurationSection nestedChild = config.getConfigurationSection("childSection.nestedChildSection");
        assertNotNull(nestedChild);
        assertEquals("lazy.childSection.nestedChildSection", nestedChild.getCurrentPath());
    }

    @Test
    @Override
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void testGetParent() {
        assertEquals(root, config.getParent());

        final ConfigurationSection nestedChild = config.getConfigurationSection("childSection.nestedChildSection");
        assertNotNull(nestedChild);
        final ConfigurationSection nestedChildParent = nestedChild.getParent();
        assertNotNull(nestedChildParent);
        final ConfigurationSection parentSection = config.getConfigurationSection("childSection");
        assertNotNull(parentSection);
        assertEquals(parentSection.getValues(true).toString(), nestedChildParent.getValues(true).toString());
    }

    @Test
    @Override
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void testGetRoot() {
        final ConfigurationSection root = config.getRoot();
        assertNotNull(root);
        assertEquals(this.root.getValues(true).toString(), root.getValues(true).toString());

        final ConfigurationSection nestedChild = config.getConfigurationSection("childSection.nestedChildSection");
        assertNotNull(nestedChild);
        final ConfigurationSection nestedChildRoot = nestedChild.getRoot();
        assertNotNull(nestedChildRoot);
        assertEquals(this.root.getValues(true).toString(), nestedChildRoot.getValues(true).toString());
    }

    @Test
    @Override
    public void testGetValuesDeepFalse() {
        final ConfigurationSection section = config.getConfigurationSection("childSection");
        assertNotNull(section);
        final Pattern pattern = Pattern.compile(Pattern.quote("{nestedChildSection=") + "\\w+"
                + Pattern.quote("[path='lazy.childSection.nestedChildSection', root='") + "\\w+"
                + Pattern.quote("']}"));
        final String sectionString = section.getValues(false).toString();
        final Matcher matcher = pattern.matcher(sectionString);
        assertTrue(matcher.matches(), "Didn't match regex: " + pattern + "\n" + "Actual string: " + sectionString);
    }

    @Test
    @Override
    public void testGetValuesDeepTrue() {
        final ConfigurationSection section = config.getConfigurationSection("childSection");
        assertNotNull(section);
        final Pattern pattern = Pattern.compile(Pattern.quote("{nestedChildSection=") + "\\w+"
                + Pattern.quote("[path='lazy.childSection.nestedChildSection', root='") + "\\w+"
                + Pattern.quote("'], nestedChildSection.key=value}"));
        final String sectionString = section.getValues(true).toString();
        final Matcher matcher = pattern.matcher(sectionString);
        assertTrue(matcher.matches(), "Didn't match regex: " + pattern + "\n" + "Actual string: " + sectionString);
    }

    @Test
    @Override
    public void testToString() {
        final Pattern pattern = Pattern.compile("\\w+"
                + Pattern.quote("[path='lazy', root='") + "\\w+"
                + Pattern.quote("']"));
        final String sectionString = config.toString();
        final Matcher matcher = pattern.matcher(sectionString);
        assertTrue(matcher.matches(), "Didn't match regex: " + pattern + "\n" + "Actual string: " + sectionString);
    }
}
