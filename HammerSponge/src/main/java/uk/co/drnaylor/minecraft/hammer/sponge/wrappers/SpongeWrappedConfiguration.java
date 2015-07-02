package uk.co.drnaylor.minecraft.hammer.sponge.wrappers;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Game;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedConfiguration;
import uk.co.drnaylor.minecraft.hammer.sponge.HammerSponge;

import java.io.IOException;

public class SpongeWrappedConfiguration implements WrappedConfiguration {
    private final ConfigurationLoader<CommentedConfigurationNode> configManager;

    public SpongeWrappedConfiguration(HammerSponge plugin, Game game) {
        this.configManager = plugin.getConfigurationManager();
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
        try {
            return configManager.load().getNode(createNodePath(nodePath)).getValue();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getConfigStringValue(String... nodePath) {
        try {
            return configManager.load().getNode(createNodePath(nodePath)).getString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Integer getConfigIntegerValue(String... nodePath) {
        try {
            return configManager.load().getNode(createNodePath(nodePath)).getInt();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
        try {
            return configManager.load().getNode(createNodePath(nodePath)).getBoolean(false);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Object[] createNodePath(String[] path) {
        Object[] o = new Object[path.length];
        for (int i = 0; i < path.length; i++) {
            o[i] = path[i];
        }

        return o;
    }
}
