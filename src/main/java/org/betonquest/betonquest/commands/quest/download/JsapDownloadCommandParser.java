package org.betonquest.betonquest.commands.quest.download;

import com.martiansoftware.jsap.*;
import org.betonquest.betonquest.commands.CommandParser;

import java.util.Arrays;

/**
 * Command parser for the {@link DownloadCommand} that delegates the actual parsing to JSAP.
 */
public class JsapDownloadCommandParser implements CommandParser<DownloadCommand> {

    /**
     * Internal flag name for the download template switch in the JSAP parser.
     */
    private static final String DOWNLOAD_TEMPLATE_FLAG = "downloadTemplate";

    /**
     * Internal flag name for the download package switch in the JSAP parser.
     */
    private static final String DOWNLOAD_PACKAGE_FLAG = "downloadPackage";

    /**
     * Internal flag name for the raw layout switch in the JSAP parser.
     */
    private static final String LAYOUT_RAW_FLAG = "raw";

    /**
     * Internal flag name for the structured layout switch in the JSAP parser.
     */
    private static final String LAYOUT_STRUCTURED_FLAG = "structured";

    /**
     * Internal flag name for the repository source path option in the JSAP parser.
     */
    private static final String REPOSITORY_SOURCE_PATH_FLAG = "repositorySourcePath";

    /**
     * Internal flag name for the repository base package option in the JSAP parser.
     */
    private static final String REPOSITORY_BASE_PACKAGE_FLAG = "repositoryBasePackage";

    /**
     * Internal flag name for the local base package option in the JSAP parser.
     */
    private static final String LOCAL_BASE_PACKAGE_FLAG = "localBasePackage";

    /**
     * Internal flag name for the package selection repeatable option in the JSAP parser.
     */
    private static final String SELECT_PACKAGE_FLAG = "selectedPackages";

    /**
     * Internal flag name for the file selection repeatable option in the JSAP parser.
     */
    private static final String SELECT_FILE_FLAG = "selectedFiles";

    /**
     * Internal flag name for the recursion switch in the JSAP parser.
     */
    private static final String RECURSIVE_FLAG = "recursive";

    /**
     * Internal flag name for the force switch in the JSAP parser.
     */
    private static final String FORCE_FLAG = "force";

    /**
     * Internal flag name for the GitHub repository argument in the JSAP parser.
     */
    public static final String GITHUB_REPOSITORY_FLAG = "gitHubRepository";

    /**
     * Internal flag name for the git reference argument in the JSAP parser.
     */
    public static final String GIT_REFERENCE_FLAG = "gitRef";

    /**
     * Internal JSAP parser that can correctly parse download commands.
     */
    private final JSAP parser;

    /**
     * Create the DownloadCommandParser.
     */
    public JsapDownloadCommandParser() {
        this.parser = createDownloadSubcommandJsap();
    }

    private JSAP createDownloadSubcommandJsap() {
        try {
            final JSAP jsap = new JSAP();
            jsap.registerParameter(new Switch(DOWNLOAD_TEMPLATE_FLAG, 'T', "download-template"));
            jsap.registerParameter(new Switch(DOWNLOAD_PACKAGE_FLAG, 'P', "download-package"));
            jsap.registerParameter(new Switch(LAYOUT_RAW_FLAG, 'R', "raw"));
            jsap.registerParameter(new Switch(LAYOUT_STRUCTURED_FLAG, 'S', "structured"));
            jsap.registerParameter(new FlaggedOption(REPOSITORY_SOURCE_PATH_FLAG, JSAP.STRING_PARSER, "", true, 's', "source"));
            jsap.registerParameter(new FlaggedOption(REPOSITORY_BASE_PACKAGE_FLAG, JSAP.STRING_PARSER, "", true, 'b', "base-package"));
            jsap.registerParameter(new FlaggedOption(LOCAL_BASE_PACKAGE_FLAG, JSAP.STRING_PARSER, "", true, 'l', "local"));
            jsap.registerParameter(new FlaggedOption(SELECT_PACKAGE_FLAG, JSAP.STRING_PARSER, null, true, 'p', "package").setAllowMultipleDeclarations(true));
            jsap.registerParameter(new FlaggedOption(SELECT_FILE_FLAG, JSAP.STRING_PARSER, null, true, 'F', "file").setAllowMultipleDeclarations(true));
            jsap.registerParameter(new Switch(RECURSIVE_FLAG, 'r', "recursive"));
            jsap.registerParameter(new Switch(FORCE_FLAG, 'f', "force"));
            jsap.registerParameter(new UnflaggedOption(GITHUB_REPOSITORY_FLAG).setStringParser(JSAP.STRING_PARSER));
            jsap.registerParameter(new UnflaggedOption(GIT_REFERENCE_FLAG).setStringParser(JSAP.STRING_PARSER));
            return jsap;
        } catch (JSAPException jsapException) {
            throw new UnsupportedOperationException("JSAP parser is configured incorrectly!", jsapException);
        }
    }

    @Override
    public DownloadCommand parse(final String... args) {
        final JSAPResult result = parser.parse(args);
        final boolean downloadPackagesSelected = result.getBoolean(DOWNLOAD_PACKAGE_FLAG);
        final boolean downloadTemplatesSelected = result.getBoolean(DOWNLOAD_TEMPLATE_FLAG);
        return new DownloadCommand(
                RepositoryLayoutRule.fromFlags(result.getBoolean(LAYOUT_STRUCTURED_FLAG), result.getBoolean(LAYOUT_RAW_FLAG)),
                downloadPackagesSelected || !downloadTemplatesSelected,
                downloadTemplatesSelected || !downloadPackagesSelected,
                result.getString(REPOSITORY_SOURCE_PATH_FLAG),
                result.getString(REPOSITORY_BASE_PACKAGE_FLAG),
                result.getString(LOCAL_BASE_PACKAGE_FLAG),
                Arrays.asList(result.getStringArray(SELECT_PACKAGE_FLAG)),
                Arrays.asList(result.getStringArray(SELECT_FILE_FLAG)),
                result.getBoolean(RECURSIVE_FLAG),
                result.getBoolean(FORCE_FLAG)
        );
    }
}
