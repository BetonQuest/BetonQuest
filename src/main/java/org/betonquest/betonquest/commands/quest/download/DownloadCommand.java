package org.betonquest.betonquest.commands.quest.download;

import java.nio.file.Path;
import java.util.List;

public record DownloadCommand(
        RepositoryLayoutRule layoutRule,
        boolean downloadPackages,
        boolean downloadTemplates,

        String sourcePath,
        String repositoryBasePackage,
        String localBasePackage,
        List<String> packages,
        List<String> files,
        boolean recursive,
        boolean force
) {
}
