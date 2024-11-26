---
icon: material/magnify
---
###Fulfil the Contributing Requirements
Run `./mvnw verify` before [Submitting Changes](../Submitting-Changes.md) to check if your change
meets the project's requirements regarding code style and quality.
GitHub Actions (automated code check on GitHub) will also verify these requirements when you open the pull request.

If any requirements are not met, `./mvnw verify` will fail with this log message:
````
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
````
IntelliJ may also show something like `Failed to execute goal`. Here's a guide how to fix any requirement violations:

!!! note ""
    === "PMD"
        Visit the **[PMD Page](https://pmd.github.io/latest/)** for general info.
        <br><br>
        PMD mainly checks for code smells. PMD's errors look like this:
        ````
        [ERROR] Failed to execute goal org.apache.maven.plugins:maven-pmd-plugin:3.14.0:check (default) on project betonquest: You have 1 PMD violation.
        ````
        If you have this message you also have messages, that looks like this:
        ````
        [INFO] PMD Failure: org.betonquest.betonquest.BetonQuest:143 Rule:AvoidLiteralsInIfCondition Priority:3 Avoid using Literals in Conditional Statements.
        ````
        If you read this, you may know what is wrong. If you don't know why, visit the
        [PMD](https://pmd.github.io/latest/) page.
        Then you type in the rule e.g. `AvoidLiteralsInIfCondition` in the search bar and click on the rule.
        You will get a detailed description about what is wrong.
        If you still don't know how to solve it, ask the developers on Discord for help with PMD.
    === "SpotBugs"
        Visit the **[SpotBugs Page](https://spotbugs.readthedocs.io/en/stable/index.html)** for general info.
        <br><br>
        SpotBugs searches for additional problems, most of them are potential bugs. SpotBugs' errors look like this:
        ````
        Failed to execute goal com.github.spotbugs:spotbugs-maven-plugin:4.2.2:check (default) on project betonquest: failed with 1 bugs and 0 errors
        ````
        If your log contains such a message, it will also contain another message that looks like this:
        ````
        [ERROR] Medium: Null passed for non-null parameter of org.betonquest.betonquest.utils.PlayerConverter.getPlayer(String) in org.betonquest.betonquest.BetonQuest.condition(String, ConditionID) [org.betonquest.betonquest.BetonQuest, org.betonquest.betonquest.BetonQuest] Method invoked at BetonQuest.java:[line 349]Known null at BetonQuest.java:[line 344] NP_NULL_PARAM_DEREF
        ````
        SpotBugs errors are a little complicated to read,
        but if you find e.g. `NP_NULL_PARAM_DEREF` at the end of the line you can simply search it on the
        [SpotBugs](https://spotbugs.readthedocs.io/en/stable/bugDescriptions.html) page.
        If you have problems solving these kinds of issues you can ask on our Discord for help with SpotBugs.
    === "CheckStyle"
        Visit the **[CheckStyle Page](https://checkstyle.sourceforge.io/checks.html)** for general info.
        <br><br>
        CheckStyle checks the code formatting and style. We have only configured two checks.
        The first check is the import order, and the second check is that you do not use star imports,
        excepting some junit imports.
        There is only a basic check for the imports, and it looks like this:
        ````
        [ERROR] src/main/java/org/betonquest/betonquest/BetonQuest.java:[16,1] (imports) ImportOrder: Wrong order for 'edu.umd.cs.findbugs.annotations.SuppressFBWarnings' import.
        ````
        CheckStyle is very simple to read, normally it is in your language and self explaining.  
        In the above error you can find e.g. `ImportOrder` and you can search on the
        [CheckStyle](https://checkstyle.sourceforge.io/checks.html) page for it.
        If you need help solving an issue here, you can also ask us on our Discord for help with CheckStyle.
        <br><br><br>
    === "EditorConfig"
        Visit the **[EditorConfig Page](https://editorconfig.org/)** for general info.
        <br><br>
        EditorConfig it natively supported by many IDEs and editors.
        It checks for some really basic formatting like brackets, line endings, indention and some more.
        EditorConfig violations look like this:
        ````
        [ERROR] There are .editorconfig violations. You may want to run
        [ERROR]     mvn editorconfig:format
        [ERROR] to fix them automagically.
        ````
        If your log contains such a message, it will also contain another message that looks like this if you want to solve it manual:
        ````
        [ERROR] src\main\java\org\betonquest\betonquest\BetonQuest.java@284,54: Delete 5 characters - violates trim_trailing_whitespace = true, reported by org.ec4j.linters.TextLinter
        ````
        We recommend running `./mvnw editorconfig:format` or use an IDE that support EditorConfig.
        
