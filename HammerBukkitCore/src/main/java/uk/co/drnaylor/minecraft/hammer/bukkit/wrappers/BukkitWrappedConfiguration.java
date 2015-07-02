package uk.co.drnaylor.minecraft.hammer.bukkit.wrappers;

import org.bukkit.plugin.java.JavaPlugin;
import uk.co.drnaylor.minecraft.hammer.bukkit.HammerBukkitPlugin;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedConfiguration;

public class BukkitWrappedConfiguration implements WrappedConfiguration {

    private final HammerBukkitPlugin plugin;

    public BukkitWrappedConfiguration(HammerBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets the object specified by the set of strings provided.
     * <p/>
     * <p>
     * This uses zml's Configurate standard, as it is ultimately more flexible.
     * Getting <code>node1.node2</code> should be entered as "node1", "node2".
     * </p>
     *
     * @param nodePath The path to the configuration node.
     * @return The returned object.
     */
    @Override
    public Object getConfigValue(String... nodePath) {
        return plugin.getConfig().get(getNodePath(nodePath));
    }

    @Override
    public String getConfigStringValue(String... nodePath) {
        return plugin.getConfig().getString(getNodePath(nodePath));
    }

    @Override
    public Integer getConfigIntegerValue(String... nodePath) {
        return plugin.getConfig().getInt(getNodePath(nodePath));
    }

    /**
     * Gets the boolean value from the node specified by the set of strings provided.
     * <p/>
     * <p>
     * This uses zml's Configurate standard, as it is ultimately more flexible.
     * Getting <code>node1.node2</code> should be entered as "node1", "node2".
     * </p>
     * <p>
     * This will return false if no value could be found.
     * </p>
     *
     * @param nodePath The path to the configuration node.
     * @return The returned object.
     */
    @Override
    public boolean getConfigBooleanValue(String... nodePath) {
        return plugin.getConfig().getBoolean(getNodePath(nodePath), false);
    }

    private String getNodePath(String[] nodePath) {
        StringBuilder sb = new StringBuilder();
        for (String n : nodePath) {
            if (sb.length() > 0) {
                sb.append(".");
            }

            sb.append(n);
        }

        return sb.toString();
    }
}
