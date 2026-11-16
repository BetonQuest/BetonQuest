package org.betonquest.betonquest.lib.config.util;

@SuppressWarnings({"unused", "PMD.CommentRequired", "PMD.TooManyMethods", "PMD.ExcessivePublicCount",
        "MissingJavadocMethod", "MissingJavadocType", "PMD.MethodNamingConventions"})
public interface ConfigurationSectionInterfaceTest {

    void get_keys_deep_false();

    void get_keys_deep_true();

    void get_values_deep_false();

    void get_values_deep_true();

    void contains();

    void contains_on_invalid_config_path();

    void contains_ignore_default_false();

    void contains_ignore_default_false_on_invalid_config_path();

    void contains_ignore_default_true();

    void contains_ignore_default_true_on_invalid_config_path();

    void contains_on_default();

    void contains_on_default_on_invalid_config_path();

    void contains_ignore_default_false_on_default();

    void contains_ignore_default_false_on_default_on_invalid_config_path();

    void contains_ignore_default_true_on_default();

    void contains_ignore_default_true_on_default_on_invalid_config_path();

    void is_set();

    void is_set_on_invalid_config_path();

    void get_current_path();

    void get_name();

    void get_root();

    void get_parent();

    void get();

    void get_on_invalid_config_path();

    void get_with_default();

    void get_with_default_on_invalid_config_path();

    void set();

    void set_section_overwriting_existing();

    void set_existing_section_to_null();

    void set_on_existing_config_path();

    void create_section();

    void create_section_on_existing_config_path();

    void create_section_with_values();

    void create_section_with_values_on_existing_config_path();

    void get_string();

    void get_string_on_invalid_config_path();

    void get_string_with_default();

    void get_string_with_default_on_invalid_config_path();

    void is_string();

    void is_string_on_invalid_config_path();

    void get_int();

    void get_int_on_invalid_config_path();

    void get_int_with_default();

    void get_int_with_default_on_invalid_config_path();

    void is_int();

    void is_int_on_invalid_config_path();

    void get_boolean();

    void get_boolean_on_invalid_config_path();

    void get_boolean_with_default();

    void get_boolean_with_default_on_invalid_config_path();

    void is_boolean();

    void is_boolean_on_invalid_config_path();

    void get_double();

    void get_double_on_invalid_config_path();

    void get_double_with_default();

    void get_double_with_default_on_invalid_config_path();

    void is_double();

    void is_double_on_invalid_config_path();

    void get_long();

    void get_long_on_invalid_config_path();

    void get_long_with_default();

    void get_long_with_default_on_invalid_config_path();

    void is_long();

    void is_long_on_invalid_config_path();

    void get_list();

    void get_list_on_invalid_config_path();

    void get_list_with_default();

    void get_list_with_default_on_invalid_config_path();

    void is_list();

    void is_list_on_invalid_config_path();

    void get_string_list();

    void get_string_list_on_invalid_config_path();

    void get_integer_list();

    void get_integer_list_on_invalid_config_path();

    void get_boolean_list();

    void get_boolean_list_on_invalid_config_path();

    void get_double_list();

    void get_double_list_on_invalid_config_path();

    void get_float_list();

    void get_float_list_on_invalid_config_path();

    void get_long_list();

    void get_long_list_on_invalid_config_path();

    void get_byte_list();

    void get_byte_list_on_invalid_config_path();

    void get_character_list();

    void get_character_list_on_invalid_config_path();

    void get_short_list();

    void get_short_list_on_invalid_config_path();

    void get_map_list();

    void get_map_list_on_invalid_config_path();

    void get_object();

    void get_object_on_invalid_config_path();

    void get_object_with_default();

    void get_object_with_default_on_invalid_config_path();

    void get_serializable();

    void get_serializable_on_invalid_config_path();

    void get_serializable_with_default();

    void get_serializable_with_default_on_invalid_config_path();

    void get_vector();

    void get_vector_on_invalid_config_path();

    void get_vector_with_default();

    void get_vector_with_default_on_invalid_config_path();

    void is_vector();

    void is_vector_on_invalid_config_path();

    void get_offline_player();

    void get_offline_player_on_invalid_config_path();

    void get_offline_player_with_default();

    void get_offline_player_with_default_on_invalid_config_path();

    void is_offline_player();

    void is_offline_player_on_invalid_config_path();

    void get_item_stack();

    void get_item_stack_on_invalid_config_path();

    void get_item_stack_with_default();

    void get_item_stack_with_default_on_invalid_config_path();

    void is_item_stack();

    void is_item_stack_on_invalid_config_path();

    void get_color();

    void get_color_on_invalid_config_path();

    void get_color_with_default();

    void get_color_with_default_on_invalid_config_path();

    void is_color();

    void is_color_on_invalid_config_path();

    void get_location();

    void get_location_on_invalid_config_path();

    void get_location_with_default();

    void get_location_with_default_on_invalid_config_path();

    void is_location();

    void is_location_on_invalid_config_path();

    void get_configuration_section();

    void get_configuration_section_on_invalid_config_path();

    void is_configuration_section();

    void is_configuration_section_on_invalid_config_path();

    void get_default_section();

    void get_default_section_on_invalid_config_path();

    void add_default();

    void add_default_on_existing_config_path();

    void set_comments();

    void set_comments_on_invalid();

    void set_inline_comments();

    void set_inline_comments_on_invalid();

    void get_comments();

    void get_comments_on_invalid();

    void get_inline_comments();

    void get_inline_comments_on_invalid();

    void to_string();
}
