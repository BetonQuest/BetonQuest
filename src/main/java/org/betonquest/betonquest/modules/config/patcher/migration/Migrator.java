package org.betonquest.betonquest.modules.config.patcher.migration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Class for migrating config files from older versions of BetonQuest.
 */
public class Migrator {

    /**
     * Class for determine in which version of BetonQuest the config is.
     */
    private static final Determiner determiner = new Determiner();

    /**
     * Migrates the config file to the newest version.
     */
    public static void migrate() {
        final int version = determiner.getVersion();
        switch (version) {
            case 0:
                migrateTo98();
            case 98:
                migrateTo238();
            case 238:
                migrateTo337();
            case 337:
                migrateTo450();
            case 450:
                migrateTo485();
            case 485:
                migrateTo538();
            case 538:
                migrateTo539();
            case 539:
                migrateTo644();
            case 644:
                migrateTo647();
            case 647:
                migrateTo674();
            default: {
                final Path file = Paths.get("plugins/BetonQuest/test.txt");
                try {
                    final String out = "ver: " + version + "\n";
                    Files.write(file, out.getBytes(), StandardOpenOption.APPEND);
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void migrateTo98() {
        final Path rpgmenu = Paths.get("plugins/BetonQuest/rpgmenu.config.yml");
        if (Files.exists(rpgmenu)) {
            try {
                Files.move(rpgmenu, rpgmenu.resolveSibling("menuConfig.yml"));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        // Funktioniert!
    }

    private static void migrateTo238() {
        // java 17?
        try {
            final Path betonquest = Paths.get("plugins/BetonQuest");
            final List<Path> questFiles = new ArrayList<>();
            Files.list(betonquest).forEach(path -> {
                        try {
                            if (Files.find(path, Integer.MAX_VALUE, (p, a) -> p.getFileName().toString().equals("main.yml")).findAny().isPresent()) {
                                questFiles.add(path);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );

            Files.createDirectory(Paths.get("plugins/BetonQuest/QuestPackages"));
            questFiles.forEach(path -> {
                try {
                    Files.walk(path).forEach(file -> {
                        try {
                            Files.copy(file, Paths.get("plugins/BetonQuest/QuestPackages/" + file.toString().substring("plugin/BetonQuest/".length())));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    Files.walk(path).sorted(Comparator.reverseOrder()).forEach(file -> {
                        try {
                            Files.delete(file);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            try {
                Files.find(Paths.get("plugins/BetonQuest/QuestPackages/"), Integer.MAX_VALUE, (p, a) -> p.getFileName().toString().equals("main.yml"))
                        .forEach(file -> {
                            try {
                                Files.move(file, file.resolveSibling("packages.yml"));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //TODO: Formatierung

            final StringBuilder sb = new StringBuilder();
            final Path file2 = Paths.get("plugins/BetonQuest/test.txt");
            final List<String> strings = questFiles.stream().map(path -> path.getFileName().toString()).toList();
            final String out = "found: " + String.join(";\n", strings) + "\n";
            sb.append(out);
            Files.write(file2, sb.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void migrateTo337() {
    }

    private static void migrateTo450() {
    }

    private static void migrateTo485() {
    }

    private static void migrateTo538() {
    }

    private static void migrateTo539() {
    }

    private static void migrateTo644() {
    }

    private static void migrateTo647() {
    }

    private static void migrateTo674() {
    }

}

