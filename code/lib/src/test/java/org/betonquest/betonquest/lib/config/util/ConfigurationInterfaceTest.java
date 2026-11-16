package org.betonquest.betonquest.lib.config.util;

@SuppressWarnings({"unused", "PMD.CommentRequired", "PMD.MethodNamingConventions", "MissingJavadocMethod",
        "MissingJavadocType"})
public interface ConfigurationInterfaceTest {

    void add_default_on_root_section();

    void add_default_on_root_section_on_existing_config_path();

    void add_defaults_as_map();

    void add_defaults_as_map_on_existing_config_path();

    void add_defaults_as_configuration();

    void add_defaults_as_configuration_on_existing_config_path();

    void get_defaults();

    void set_defaults();

    void set_defaults_on_existing_config_path();

    void options();

    void options_path_separator();
}
