package org.betonquest.betonquest.commands.quest.download;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsapDownloadCommandParserTest {
    @ParameterizedTest
    @MethodSource("validCommands")
    void parse_correct_commands(final String[] commandLineArguments, final DownloadCommand expectedCommand) {
        final JsapDownloadCommandParser parser = new JsapDownloadCommandParser();
        final DownloadCommand actualCommand = parser.parse(commandLineArguments);
        assertEquals(expectedCommand, actualCommand);
    }

    public static Stream<Arguments> validCommands() {
        return Stream.of(
                Arguments.of(new String[]{"BetonQuest/Quest-Tutorials", "main"}, new DownloadCommand(
                        RepositoryLayoutRule.AUTO_DETECT,
                        true,
                        true,
                        "",
                        "",
                        "",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        false,
                        false
                )),
                Arguments.of(new String[]{"-RP", "BetonQuest/Quest-Tutorials", "main"}, new DownloadCommand(
                        RepositoryLayoutRule.FORCE_RAW,
                        true,
                        false,
                        "",
                        "",
                        "",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        false,
                        false
                )),
                Arguments.of(new String[]{"-RT", "BetonQuest/Quest-Tutorials", "main"}, new DownloadCommand(
                        RepositoryLayoutRule.FORCE_RAW,
                        false,
                        true,
                        "",
                        "",
                        "",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        false,
                        false
                )),
                Arguments.of(new String[]{"-S", "BetonQuest/Quest-Tutorials", "main"}, new DownloadCommand(
                        RepositoryLayoutRule.FORCE_STRUCTURED,
                        true,
                        true,
                        "",
                        "",
                        "",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        false,
                        false
                )),
                Arguments.of(new String[]{"-P", "BetonQuest/Quest-Tutorials", "main"}, new DownloadCommand(
                        RepositoryLayoutRule.AUTO_DETECT,
                        true,
                        false,
                        "",
                        "",
                        "",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        false,
                        false
                )),
                Arguments.of(new String[]{"-T", "BetonQuest/Quest-Tutorials", "main"}, new DownloadCommand(
                        RepositoryLayoutRule.AUTO_DETECT,
                        false,
                        true,
                        "",
                        "",
                        "",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        false,
                        false
                )),
                Arguments.of(new String[]{"-PT", "BetonQuest/Quest-Tutorials", "main"}, new DownloadCommand(
                        RepositoryLayoutRule.AUTO_DETECT,
                        true,
                        true,
                        "",
                        "",
                        "",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        false,
                        false
                )),
                Arguments.of(new String[]{"-s", "chapter_one", "BetonQuest/Quest-Tutorials", "main"}, new DownloadCommand(
                        RepositoryLayoutRule.AUTO_DETECT,
                        true,
                        true,
                        "chapter_one",
                        "",
                        "",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        false,
                        false
                )),
                Arguments.of(new String[]{"-b", "first_act.scene_one", "BetonQuest/Quest-Tutorials", "main"}, new DownloadCommand(
                        RepositoryLayoutRule.AUTO_DETECT,
                        true,
                        true,
                        "",
                        "first_act.scene_one",
                        "",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        false,
                        false
                )),
                Arguments.of(new String[]{"-l", "external", "BetonQuest/Quest-Tutorials", "main"}, new DownloadCommand(
                        RepositoryLayoutRule.AUTO_DETECT,
                        true,
                        true,
                        "",
                        "",
                        "external",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        false,
                        false
                )),
                Arguments.of(new String[]{"-p", "evil.jake", "BetonQuest/Quest-Tutorials", "main"}, new DownloadCommand(
                        RepositoryLayoutRule.AUTO_DETECT,
                        true,
                        true,
                        "",
                        "",
                        "",
                        Collections.singletonList("evil.jake"),
                        Collections.emptyList(),
                        false,
                        false
                )),
                Arguments.of(new String[]{"-p", "evil.jake", "-p", "evil.bob", "BetonQuest/Quest-Tutorials", "main"}, new DownloadCommand(
                        RepositoryLayoutRule.AUTO_DETECT,
                        true,
                        true,
                        "",
                        "",
                        "",
                        List.of("evil.jake", "evil.bob"),
                        Collections.emptyList(),
                        false,
                        false
                )),
                Arguments.of(new String[]{"-F", "good/frank.yml", "BetonQuest/Quest-Tutorials", "main"}, new DownloadCommand(
                        RepositoryLayoutRule.AUTO_DETECT,
                        true,
                        true,
                        "",
                        "",
                        "",
                        Collections.emptyList(),
                        Collections.singletonList("good/frank.yml"),
                        false,
                        false
                )),
                Arguments.of(new String[]{"-F", "good/frank.yml", "-F", "good/joseph.yml", "BetonQuest/Quest-Tutorials", "main"}, new DownloadCommand(
                        RepositoryLayoutRule.AUTO_DETECT,
                        true,
                        true,
                        "",
                        "",
                        "",
                        Collections.emptyList(),
                        List.of("good/frank.yml", "good/joseph.yml"),
                        false,
                        false
                )),
                Arguments.of(new String[]{"-r", "BetonQuest/Quest-Tutorials", "main"}, new DownloadCommand(
                        RepositoryLayoutRule.AUTO_DETECT,
                        true,
                        true,
                        "",
                        "",
                        "",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        true,
                        false
                )),
                Arguments.of(new String[]{"-f", "BetonQuest/Quest-Tutorials", "main"}, new DownloadCommand(
                        RepositoryLayoutRule.AUTO_DETECT,
                        true,
                        true,
                        "",
                        "",
                        "",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        false,
                        true
                ))
        );
    }
}
