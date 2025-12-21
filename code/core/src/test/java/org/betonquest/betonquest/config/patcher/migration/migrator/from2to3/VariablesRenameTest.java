package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test renaming of placeholders to constants.
 */
class VariablesRenameTest extends QuestFixture {

    private static Stream<Arguments> entriesToMigrate() {
        return Stream.of(
                Arguments.of("$foo$",
                        "%constant.foo%"
                ),
                Arguments.of("$_-_-path-to.foo$",
                        "%_-_-path-to.constant.foo%"
                ),
                Arguments.of("%math.calc:$foo$%",
                        "%math.calc:{constant.foo}%"
                ),
                Arguments.of("%math.calc:$_-_-path-to.foo$%",
                        "%math.calc:{_-_-path-to.constant.foo}%"
                ),
                Arguments.of("foo %math.calc:1+$bla$+2-$-a-b.c$% $bar$ $-a-b.c$ bar",
                        "foo %math.calc:1+{constant.bla}+2-{-a-b.constant.c}% %constant.bar% %-a-b.constant.c% bar"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("entriesToMigrate")
    void variable_rename(final String original, final String expected) {
        final VariablesRename variablesRename = new VariablesRename();
        final String actual = variablesRename.replaceGlobalVariables(original);

        assertEquals(expected, actual, "Constant is not migrated correctly");
    }

    @Test
    void simple_migration() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                variables:
                  #This is comment 1
                  Variable_1: one # Inline comment 1
                  Variable_2: two # Inline comment 2
                  menu_string: Can you see this global variable in the menu?
                  loc_evt1_top_tp: -261;64;345;World
                  loc_evt2_top_tp: -261;64;355;World
                  loc_obj_tp1_top: -261;64;348;World
                  #Last Comment
                  loc_obj_tp2_top: -261;64;352;World
                """);
        expected.loadFromString("""
                constants:
                  #This is comment 1
                  Variable_1: one # Inline comment 1
                  Variable_2: two # Inline comment 2
                  menu_string: Can you see this global variable in the menu?
                  loc_evt1_top_tp: -261;64;345;World
                  loc_evt2_top_tp: -261;64;355;World
                  loc_obj_tp1_top: -261;64;348;World
                  #Last Comment
                  loc_obj_tp2_top: -261;64;352;World
                """);

        final Quest quest = setupQuest("constants.yml");
        new VariablesRename().migrate(quest);
        quest.saveAll();

        checkAssertion(quest, "constants.yml");
    }
}
