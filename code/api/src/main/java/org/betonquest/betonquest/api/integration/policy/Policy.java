package org.betonquest.betonquest.api.integration.policy;

/**
 * The Policy interface represents a set of rules or conditions that can be described and validated.
 * Implementations of this interface define specific policies with custom validation logic.
 */
public interface Policy {

    /**
     * Provides a textual description of the policy used for logging purposes.
     * <p>
     * This method returns a human-readable string that describes what the policy validates or enforces.
     * The description is primarily used for logging, debugging, and error reporting to help identify
     * which policy failed validation or to provide context about the policy's purpose.
     * <p>
     * Examples of policy descriptions:
     * <ul>
     *   <li>For a version compatibility policy: {@code "Plugin 'Citizens' required with version >= 2.0.30"}</li>
     *   <li>For a plugin presence policy: {@code "Plugin 'Vault' must be enabled"}</li>
     *   <li>For any conditional policy: {@code "Feature 'economy' must be available"}</li>
     * </ul>
     *
     * @return a string representation describing the policy's purpose or details.
     */
    String description();

    /**
     * Validates the policy by checking if it satisfies all defined rules.
     * The implementation determines the exact validation logic.
     * <p>
     * This method is not supposed to throw any exceptions but instead return false invalidating the policy.
     *
     * @return true if the policy is valid and meets all requirements, false otherwise
     */
    boolean validate();
}
