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

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.loader.AbstractConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMapperFactory;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import uk.co.drnaylor.minecraft.hammer.core.config.HammerConfig;

import java.io.IOException;

public class HammerConfiguration {

    private final TypeToken<HammerConfig> typeToken = TypeToken.of(HammerConfig.class);
    private final AbstractConfigurationLoader<? extends ConfigurationNode> configurationLoader;
    private final ObjectMapper<HammerConfig>.BoundInstance config;

    public HammerConfiguration(AbstractConfigurationLoader<? extends ConfigurationNode> loader) throws Exception {
        this.configurationLoader = loader;
        this.config = ObjectMapper.forObject(new HammerConfig());
        reloadConfig();
    }

    public HammerConfig getConfig() {
        return this.config.getInstance();
    }

    void reloadConfig() throws IOException, ObjectMappingException {
        ConfigurationNode cn = configurationLoader.load();
        this.config.populate(cn);
        configurationLoader.save(cn);
    }

}
