package uk.co.drnaylor.minecraft.hammer.core.wrappers;

/**
 * Wraps methods to get configuration from the system.
 */
public interface WrappedConfiguration {
    /**
     * Gets the object specified by the set of strings provided.
     *
     * <p>
     *     This uses zml's Configurate standard, as it is ultimately more flexible.
     *     Getting <code>node1.node2</code> should be entered as "node1", "node2".
     * </p>
     *
     * @param nodePath The path to the configuration node.
     * @return The returned object.
     */
    Object getConfigValue(String... nodePath);

    String getConfigStringValue(String... nodePath);

    Integer getConfigIntegerValue(String... nodePath);
}
