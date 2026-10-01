package org.betonquest.betonquest.lib.config.section.handle;

import org.betonquest.betonquest.lib.config.section.handle.util.HandleModificationToConfigurationFixture;
import org.junit.jupiter.api.Tag;

/**
 * This is a test for the {@link HandleModificationConfigurationSection}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestShouldIncludeAssert", "PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.JUnitJupiterTestShouldBePackagePrivate"})
public class HandleModificationConfigurationSectionTest extends AbstractHandleModificationConfigurationSectionTest {

    public HandleModificationConfigurationSectionTest() {
        super(new HandleModificationConfigurationSection(getDefaultConfig(), new HandleModificationToConfigurationFixture()));
    }
}
