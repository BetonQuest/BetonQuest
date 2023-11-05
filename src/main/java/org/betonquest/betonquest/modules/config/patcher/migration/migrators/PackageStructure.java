package org.betonquest.betonquest.modules.config.patcher.migration.migrators;

import org.betonquest.betonquest.modules.config.patcher.migration.Migrator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Handels the PackageStructure migration.
 */
public class PackageStructure implements Migrator {


    @Override
    public boolean needMigration() {
        final Path questPackages = Paths.get("plugins/BetonQuest/QuestPackages");
        return !Files.exists(questPackages);
    }

    @Override
    public void migrate() {
        try {
            final Path betonquest = Paths.get("plugins/BetonQuest");
            final Path questPackagePath = Paths.get("plugins/BetonQuest/QuestPackages");
            final List<Path> questFiles = getQuestFiles(betonquest);
            moveQuestFiles(questPackagePath, questFiles);
            renameMainToPackage(questPackagePath);
            createNestedSectionsInConfigs(questPackagePath);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private List<Path> getQuestFiles(final Path betonquest) throws IOException {
        try (Stream<Path> paths = Files.list(betonquest)) {
            return paths.filter(Files::isDirectory)
                    .filter(path -> {
                        try {
                            try (Stream<Path> findings = Files.find(path, Integer.MAX_VALUE, (p, a) -> p.getFileName().toString().equals("main.yml"))) {
                                return findings.findAny().isPresent();
                            }
                        } catch (final IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        }
    }

    private void moveQuestFiles(final Path questPackagePath, final List<Path> questFiles) throws IOException {
        Files.createDirectory(questPackagePath);
        questFiles.forEach(path -> {
            try (Stream<Path> files = Files.walk(path)) {
                files.forEach(file -> {
                    try {
                        Files.copy(file, Paths.get("plugins/BetonQuest/QuestPackages/" + file.toString().substring("plugin/BetonQuest/".length())));
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
            try (Stream<Path> files2 = Files.walk(path)) {
                files2.sorted(Comparator.reverseOrder()).forEach(file -> {
                    try {
                        Files.delete(file);
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void renameMainToPackage(final Path questPackagePath) {
        try (Stream<Path> files = Files.find(questPackagePath, Integer.MAX_VALUE, (p, a) -> p.getFileName().toString().equals("main.yml"))) {
            files.forEach(file -> {
                try {
                    Files.move(file, file.resolveSibling("packages.yml"));
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createNestedSectionsInConfigs(final Path questPackagePath) throws IOException {
        createNestedSection(questPackagePath, "events");
        createNestedSection(questPackagePath, "objectives");
        createNestedSection(questPackagePath, "conditions");
        createNestedSection(questPackagePath, "journal");
        createNestedSection(questPackagePath, "items");
        createNestedSectionInSubFolders(questPackagePath, "conversations");
        createNestedSectionInSubFolders(questPackagePath, "menus");
    }

    private void createNestedSection(final Path questPackagePath, final String identifier) {
        try (Stream<Path> files = Files.find(questPackagePath, Integer.MAX_VALUE, (p, a) -> p.getFileName().toString().equals(identifier + ".yml"))) {
            files.map(Path::toFile)
                    .forEach(file -> {
                        final YamlConfiguration oldConfig = YamlConfiguration.loadConfiguration(file);
                        final YamlConfiguration newConfig = new YamlConfiguration();
                        newConfig.set(identifier, oldConfig);
                        try {
                            newConfig.save(file);
                        } catch (final IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createNestedSectionInSubFolders(final Path questPackagePath, final String identifier) {
        try (Stream<Path> files = Files.find(questPackagePath, Integer.MAX_VALUE, (p, a) -> p.getFileName().toString().equals(identifier))) {
            files.flatMap(path -> {
                        try {
                            return Files.walk(path);
                        } catch (final IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .forEach(file -> {
                        final YamlConfiguration oldConfig = YamlConfiguration.loadConfiguration(file);
                        final YamlConfiguration newConfig = new YamlConfiguration();
                        newConfig.set(identifier + "." + file.getName(), oldConfig);
                        try {
                            newConfig.save(file);
                        } catch (final IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
