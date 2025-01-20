package org.betonquest.betonquest.config.patcher.migration.migrators.from1to2;

import org.betonquest.betonquest.config.patcher.migration.Migration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Handles the PackageStructure migration.
 */
public class PackageStructure implements Migration {
    /**
     * The BetonQuest folder.
     */
    public static final Path BETONQUEST = Paths.get("plugins/BetonQuest");

    /**
     * The BetonQuest quest packages folder.
     */
    public static final Path BETONQUEST_QUEST_PACKAGES = BETONQUEST.resolve("QuestPackages");

    /**
     * The BetonQuest quest templates folder.
     */
    public static final Path BETONQUEST_QUEST_TEMPLATES = BETONQUEST.resolve("QuestTemplates");

    /**
     * Creates a new PackageStructure migrator.
     */
    public PackageStructure() {
    }

    @SuppressWarnings("PMD.PreserveStackTrace")
    @Override
    public void migrate() throws IOException {
        try {
            final List<Path> oldQuestFolders = getOldQuestFolders();
            final List<Path> movedOldQuestFiles = moveOldQuestFolderFiles(oldQuestFolders);
            final List<Path> renamedQuestFiles = renameMainToPackage(movedOldQuestFiles);
            createNestedSectionsInConfigs(renamedQuestFiles);
        } catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    private List<Path> getOldQuestFolders() throws IOException {
        if (!Files.exists(BETONQUEST)) {
            return List.of();
        }
        try (Stream<Path> paths = Files.list(BETONQUEST)) {
            return paths.filter(Files::isDirectory)
                    .filter(path -> !path.equals(BETONQUEST_QUEST_PACKAGES))
                    .filter(path -> !path.equals(BETONQUEST_QUEST_TEMPLATES))
                    .filter(path -> {
                        try (Stream<Path> findings = Files.find(path, Integer.MAX_VALUE, (p, a) -> "main.yml".equals(p.getFileName().toString()))) {
                            return findings.findAny().isPresent();
                        } catch (final IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .toList();
        }
    }

    private List<Path> moveOldQuestFolderFiles(final List<Path> questFolders) throws IOException {
        final List<Path> movedOldQuestFiles = new ArrayList<>();
        for (final Path folder : questFolders) {
            final Path targetFolder = BETONQUEST_QUEST_PACKAGES.resolve(BETONQUEST.relativize(folder));
            Files.move(folder, targetFolder);
            try (Stream<Path> files = Files.walk(targetFolder)) {
                files
                        .filter(file -> !Files.isDirectory(file))
                        .filter(file -> file.getFileName().toString().endsWith(".yml"))
                        .forEach(movedOldQuestFiles::add);
            }
        }
        return movedOldQuestFiles;
    }

    private List<Path> renameMainToPackage(final List<Path> oldQuestFiles) {
        return oldQuestFiles.stream()
                .map(path -> {
                    final String mainFile = "main.yml";
                    if (mainFile.equals(path.getFileName().toString())) {
                        try {
                            final Path target = path.resolveSibling("package.yml");
                            Files.move(path, target);
                            return target;
                        } catch (final IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    }
                    return path;
                })
                .toList();
    }

    private void createNestedSectionsInConfigs(final List<Path> questFiles) {
        createNestedSection(questFiles, "events");
        createNestedSection(questFiles, "objectives");
        createNestedSection(questFiles, "conditions");
        createNestedSection(questFiles, "journal");
        createNestedSection(questFiles, "items");
        createNestedSectionInSubFolders(questFiles, "conversations");
        createNestedSectionInSubFolders(questFiles, "menus");
    }

    private void createNestedSection(final List<Path> questFiles, final String identifier) {
        questFiles.stream()
                .filter(file -> file.endsWith(identifier + ".yml"))
                .forEach(file -> {
                    final YamlConfiguration oldConfig = YamlConfiguration.loadConfiguration(file.toFile());
                    final YamlConfiguration newConfig = new YamlConfiguration();
                    newConfig.set(identifier, oldConfig);
                    try {
                        newConfig.save(file.toFile());
                    } catch (final IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }

    private void createNestedSectionInSubFolders(final List<Path> questFiles, final String identifier) {
        questFiles.stream()
                .filter(file -> file.getParent() != null && file.getParent().endsWith(identifier))
                .forEach(file -> {
                    final YamlConfiguration oldConfig = YamlConfiguration.loadConfiguration(file.toFile());
                    final YamlConfiguration newConfig = new YamlConfiguration();
                    final String fileName = file.getFileName().toString();
                    newConfig.set(identifier + "." + fileName.substring(0, fileName.lastIndexOf('.')), oldConfig);
                    try {
                        newConfig.save(file.toFile());
                    } catch (final IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }
}
