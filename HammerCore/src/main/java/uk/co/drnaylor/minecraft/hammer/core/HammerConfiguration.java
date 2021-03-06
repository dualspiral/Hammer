/*
 * This file is part of Hammer, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Daniel Naylor
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package uk.co.drnaylor.minecraft.hammer.core;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.loader.AbstractConfigurationLoader;

import java.io.IOException;

public class HammerConfiguration {

    private final AbstractConfigurationLoader<? extends ConfigurationNode> configurationLoader;
    private ConfigurationNode configNode;

    public HammerConfiguration(AbstractConfigurationLoader<? extends ConfigurationNode> loader) throws IOException {
        this.configurationLoader = loader;
        reloadConfig();
    }

    public ConfigurationNode getConfig() {
        return configNode;
    }

    void reloadConfig() throws IOException {
        ConfigurationNode cn = configurationLoader.load();
        this.configNode = cn.mergeValuesFrom(getDefaultConfig());
        configurationLoader.save(cn);
    }

    private ConfigurationNode getDefaultConfig() {
        CommentedConfigurationNode node = SimpleCommentedConfigurationNode.root();
        node.getNode("database-engine").setValue("sqlite").setComment("The DB engine to use. Valid options are \"sqlite\", \"h2\" and \"mysql\". The required JDBC driver must be on the classpath.");
        node.getNode("mysql").setComment("This section is only required if MySQL is selected for the database engine.");
        node.getNode("mysql", "host").setValue("localhost").setComment("The location of the database");
        node.getNode("mysql", "port").setValue(3306).setComment("The port for the database");
        node.getNode("mysql", "database").setValue("hammer").setComment("The name of the database to connect to.");
        node.getNode("mysql", "username").setValue("username").setComment("The username for the database connection");
        node.getNode("mysql", "password").setValue("password").setComment("The password for the database connection");
        node.getNode("server", "id").setValue(1).setComment("A unique integer id to represent this server");
        node.getNode("server", "name").setValue("New Server").setComment("A display name for this server when using Hammer");
        node.getNode("notifyAllOnBan").setValue(true).setComment("If set to false, only those with the 'hammer.notify' permission will be notified when someone is banned.");
        node.getNode("pollBans", "enable").setValue(true).setComment("If set to true, Hammer will check the database periodically to see if any online player have recieved a global ban and will ban them accordingly.");
        node.getNode("pollBans", "period").setValue(60).setComment("How often, in seconds, Hammer will check the database for new bans");
        node.getNode("audit").setComment("Whether or not an audit log should be kept.");
        node.getNode("audit", "database").setComment("Keep an audit log in the database.").setValue(true);
        node.getNode("audit", "flatfile").setComment("Keep an audit log in flat file.").setValue(false);
        node.getNode("appendBanReasons").setComment("If this is true, and a ban is applied on top of a previous (lesser) ban, the previous ban reason will be appeneded to the new one. " +
                "Otherwise, ban reasons will be replaced.").setValue(true);
        return node;
    }
}
